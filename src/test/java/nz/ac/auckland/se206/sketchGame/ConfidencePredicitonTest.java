package nz.ac.auckland.se206.sketchGame;

import ai.djl.modality.Classifications.Classification;
import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.SketchGame;
import nz.ac.auckland.se206.SketchGame.Difficulty;
import nz.ac.auckland.se206.User;
import nz.ac.auckland.se206.UserManager;
import nz.ac.auckland.se206.controllers.CanvasController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test written to make sure confidence check works properly
 *
 * <p>IMPORTANT NOTE: To make this test work, isCategoryPredicted() method and SketchGame game field
 * in canvas controller must be made public. Remember to switch them back to private after you run
 * the tests. Remember to uncomment lines of code in the test
 *
 * @author serge
 */
class ConfidencePredicitonTest {
  private SketchGame game;
  private CanvasController canvas;

  /**
   * Create canvas controller, user manager, and sketch game instances before each test
   *
   * @throws Exception
   */
  @BeforeEach
  void setUp() throws Exception {

    canvas = new CanvasController();
    User user =
        new User(
            "user",
            0,
            0,
            new ArrayList<>(),
            60,
            new ArrayList<>(),
            new ArrayList<>(),
            new ArrayList<>());
    UserManager.addUser(user);
    UserManager.setCurrentUser("user");
    game = new SketchGame();
    // canvas.game = game; //UNCOMMENT
  }

  /**
   * Test if win with confidence difficulty easy, prediction confidence 5%
   *
   * @throws IOException
   * @throws CsvException
   * @throws URISyntaxException
   */
  @Test
  void testConfidenceWin() throws IOException, CsvException, URISyntaxException {

    // hard accuracy to check only first prediction
    game.setDifficulty(Difficulty.E, Difficulty.H, Difficulty.E, Difficulty.E);
    game.resetGame();

    // create a mock prediction list with one prediction
    List<Classification> predictionList = new ArrayList<>();
    Classification prediction = new Classification(game.getCategory(), 0.05);
    predictionList.add(prediction);
    // assertEquals(true, canvas.isCategoryPredicted(predictionList));//UNCOMMENT
  }

  /**
   * Test if win with confidence difficulty easy, prediction confidence 5%
   *
   * @throws IOException
   * @throws CsvException
   * @throws URISyntaxException
   */
  @Test
  void testConfidenceLose() throws IOException, CsvException, URISyntaxException {

    // hard accuracy to check only first prediction
    game.setDifficulty(Difficulty.E, Difficulty.H, Difficulty.E, Difficulty.E);
    game.resetGame();

    List<Classification> predictionList = new ArrayList<>();
    Classification prediction = new Classification(game.getCategory(), 0.04);
    predictionList.add(prediction);
    // assertEquals(false, canvas.isCategoryPredicted(predictionList));//UNCOMMENT
  }

  /**
   * Test if win with confidence difficulty medium, prediction confidence 17%
   *
   * @throws IOException
   * @throws CsvException
   * @throws URISyntaxException
   */
  @Test
  void testConfidenceWinM() throws IOException, CsvException, URISyntaxException {

    // hard accuracy to check only first prediction
    game.setDifficulty(Difficulty.E, Difficulty.H, Difficulty.M, Difficulty.E);
    game.resetGame();

    List<Classification> predictionList = new ArrayList<>();
    Classification prediction = new Classification(game.getCategory(), 0.17);
    predictionList.add(prediction);
    // assertEquals(true, canvas.isCategoryPredicted(predictionList));//UNCOMMENT
  }

  /**
   * Test if lose with confidence difficulty medium, prediction confidence 14%
   *
   * @throws IOException
   * @throws CsvException
   * @throws URISyntaxException
   */
  @Test
  void testConfidenceLoseM() throws IOException, CsvException, URISyntaxException {

    // hard accuracy to check only first prediction
    game.setDifficulty(Difficulty.E, Difficulty.H, Difficulty.M, Difficulty.E);
    game.resetGame();

    List<Classification> predictionList = new ArrayList<>();
    Classification prediction = new Classification(game.getCategory(), 0.14);
    predictionList.add(prediction);
    // assertEquals(false, canvas.isCategoryPredicted(predictionList));//UNCOMMENT
  }

  /**
   * Test if win with confidence difficulty hard, prediction confidence 36%
   *
   * @throws IOException
   * @throws CsvException
   * @throws URISyntaxException
   */
  @Test
  void testConfidenceWinH() throws IOException, CsvException, URISyntaxException {

    // hard accuracy to check only first prediction
    game.setDifficulty(Difficulty.E, Difficulty.H, Difficulty.H, Difficulty.E);
    game.resetGame();

    List<Classification> predictionList = new ArrayList<>();
    Classification prediction = new Classification(game.getCategory(), 0.36);
    predictionList.add(prediction);
    // assertEquals(true, canvas.isCategoryPredicted(predictionList));//UNCOMMENT
  }

  /**
   * Test if lose with confidence difficulty hard, prediction confidence 33%
   *
   * @throws IOException
   * @throws CsvException
   * @throws URISyntaxException
   */
  @Test
  void testConfidenceLoseH() throws IOException, CsvException, URISyntaxException {

    // hard accuracy to check only first prediction
    game.setDifficulty(Difficulty.E, Difficulty.H, Difficulty.H, Difficulty.E);
    game.resetGame();

    List<Classification> predictionList = new ArrayList<>();
    Classification prediction = new Classification(game.getCategory(), 0.33);
    predictionList.add(prediction);
    // assertEquals(false, canvas.isCategoryPredicted(predictionList));//UNCOMMENT
  }
}
