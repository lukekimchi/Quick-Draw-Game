package nz.ac.auckland.se206.sketchGame;

import static org.junit.Assert.assertEquals;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import nz.ac.auckland.se206.SketchGame;
import nz.ac.auckland.se206.SketchGame.Difficulty;
import nz.ac.auckland.se206.User;
import nz.ac.auckland.se206.UserManager;
import nz.ac.auckland.se206.controllers.CanvasController;
import nz.ac.auckland.se206.words.CategorySelector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Achievements {

  private SketchGame game;
  private CanvasController canvas;

  @BeforeEach
  void setUp() throws Exception {

    // create default last difficulty parameter
    ArrayList<String> lastDiff = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      lastDiff.add("E");
    }
    // generate achievement array with all false by default.
    ArrayList<Boolean> achievements = new ArrayList<>();
    for (int j = 0; j < 8; j++) { // currently 8 achievements, can be changed
      achievements.add(false);
    }
    ArrayList<Boolean> difficultyCompletion = new ArrayList<>();
    for (int k = 0; k < 3; k++) { // currently 3 category difficulties
      difficultyCompletion.add(false);
    }
    canvas = new CanvasController();
    User user =
        new User("user", 0, 0, new ArrayList<>(), 60, lastDiff, achievements, difficultyCompletion);
    UserManager.addUser(user);
    UserManager.setCurrentUser("user");
    game = new SketchGame();
    // canvas.game = game; //UNCOMMENT
  }

  /**
   * note that this test can only be used when assuming that category selection tests pass. Only
   * achievements 0,2 and 5 must pass
   *
   * @throws IOException
   * @throws CsvException
   * @throws URISyntaxException
   */
  @Test
  void testAllEasyWordsNoLoss() throws IOException, CsvException, URISyntaxException {
    CategorySelector categorySelector = new CategorySelector();

    // one more words than the limit
    for (int i = 0; i < 145; i++) {
      String category = categorySelector.getRandomCategory(Difficulty.E);
      // add category to played words
      UserManager.getCurrentUser().addCategoryToHistory(category);
      UserManager.getCurrentUser().incrementWins();
    }
    ArrayList<Boolean> expected = new ArrayList<>();
    expected.add(true); // one win
    expected.add(false);
    expected.add(true); // 10 wins
    expected.add(false);
    expected.add(false);
    expected.add(true); // all easy words
    expected.add(false);
    expected.add(false);

    assertEquals(expected, UserManager.getCurrentUser().getAchievements());
  }

  /**
   * note that this test can only be used when assuming that category selection tests pass. Only
   * achievements 1,5 and 6 must pass
   *
   * @throws IOException
   * @throws CsvException
   * @throws URISyntaxException
   */
  @Test
  void testAllMediumWordsAllLoss() throws IOException, CsvException, URISyntaxException {
    CategorySelector categorySelector = new CategorySelector();

    // more words than the limit
    for (int i = 0; i < 300; i++) {
      String category = categorySelector.getRandomCategory(Difficulty.M);
      // add category to played words
      UserManager.getCurrentUser().addCategoryToHistory(category);
      UserManager.getCurrentUser().incrementLosses();
    }
    ArrayList<Boolean> expected = new ArrayList<>();
    expected.add(false); // one win
    expected.add(true);
    expected.add(false); // 10 wins
    expected.add(false);
    expected.add(false);
    expected.add(true); // all easy words
    expected.add(true);
    expected.add(false);

    assertEquals(expected, UserManager.getCurrentUser().getAchievements());
  }

  /**
   * note that this test can only be used when assuming that category selection tests pass. Only
   * achievements 1 and 7 must pass
   *
   * @throws IOException
   * @throws CsvException
   * @throws URISyntaxException
   */
  @Test
  void testAllMasterWordsAllLoss() throws IOException, CsvException, URISyntaxException {
    CategorySelector categorySelector = new CategorySelector();

    // more words than the limit
    for (int i = 0; i < 300; i++) {
      String category = categorySelector.getRandomCategory(Difficulty.MASTER);
      // add category to played words
      UserManager.getCurrentUser().addCategoryToHistory(category);
      UserManager.getCurrentUser().incrementLosses();
    }
    ArrayList<Boolean> expected = new ArrayList<>();
    expected.add(false); // one win
    expected.add(true);
    expected.add(false); // 10 wins
    expected.add(false);
    expected.add(false);
    expected.add(false); // all easy words
    expected.add(false);
    expected.add(true);

    assertEquals(expected, UserManager.getCurrentUser().getAchievements());
  }
}
