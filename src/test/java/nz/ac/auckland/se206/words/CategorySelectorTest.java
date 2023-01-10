package nz.ac.auckland.se206.words;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import nz.ac.auckland.se206.SketchGame.Difficulty;
import nz.ac.auckland.se206.User;
import nz.ac.auckland.se206.UserManager;
import org.junit.jupiter.api.Test;

class CategorySelectorTest {

  @Test
  public void testCSVreader() throws IOException, CsvException, URISyntaxException {

    CategorySelector category = new CategorySelector();
    List<String[]> result = category.getLines();
    int size = result.size();

    assertTrue(size == 345);
  }

  @Test
  public void testNewRandomCategory() throws IOException, CsvException, URISyntaxException {

    CategorySelector categorySelector = new CategorySelector();
    User user =
        new User(
            "user",
            0,
            0,
            new ArrayList<String>(),
            60,
            new ArrayList<String>(),
            new ArrayList<>(),
            new ArrayList<>());
    UserManager.addUser(user);
    UserManager.setCurrentUser(user.getUsername());

    for (int i = 0; i < 144; i++) {
      String category = categorySelector.getRandomCategory(Difficulty.E);
      System.out.println("Category: " + category);

      // check if category is in played words
      assertFalse(UserManager.getCurrentUser().getHistory().contains(category));

      // add category to played words
      UserManager.getCurrentUser().addCategoryToHistory(category);
    }
  }
  /**
   * test used to check if getRandomCategory method in CategorySelector works
   *
   * @throws IOException
   * @throws CsvException
   * @throws URISyntaxException
   */
  @Test
  public void testNewRandomCategoryWrap() throws IOException, CsvException, URISyntaxException {

    CategorySelector categorySelector = new CategorySelector();
    ArrayList<Boolean> difficultyCompletion = new ArrayList<>();
    for (int k = 0; k < 3; k++) { // currently 3 category difficulties
      difficultyCompletion.add(false);
    }
    User user =
        new User(
            "user",
            0,
            0,
            new ArrayList<String>(),
            60,
            new ArrayList<String>(),
            new ArrayList<>(),
            difficultyCompletion);
    UserManager.addUser(user);
    UserManager.setCurrentUser(user.getUsername());

    for (int i = 0; i < 144; i++) {
      String category = categorySelector.getRandomCategory(Difficulty.E);

      // check if category is in played words
      assertFalse(UserManager.getCurrentUser().getHistory().contains(category));

      // add category to played words
      UserManager.getCurrentUser().addCategoryToHistory(category);
    }
    System.out.println("Category cycle complete");

    // check if another category can be selected
    try {
      String category = categorySelector.getRandomCategory(Difficulty.E);
      System.out.println("Category: " + category);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
