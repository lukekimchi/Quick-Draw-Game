package nz.ac.auckland.se206.words;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import nz.ac.auckland.se206.SketchGame.Difficulty;
import nz.ac.auckland.se206.UserManager;

public class CategorySelector {

  // create a map where the key refers to E, M or H and the
  // value refers to the category
  private Map<Difficulty, List<String>> difficultyOfCategories;
  private List<String> categoryPool;

  /**
   * constructor that reads all possible categories and saves them into a hashmap dependent on their
   * difficulty
   *
   * @throws IOException file not found
   * @throws CsvException csv problem
   * @throws URISyntaxException uri problem
   */
  public CategorySelector() throws IOException, CsvException, URISyntaxException {
    difficultyOfCategories = new HashMap<>();

    // instantiate an ArrayList for each difficulty level
    // later on the categories from the csv file will be added to their respective
    // arraylists, depending on its difficulty
    for (Difficulty difficulty : Difficulty.values()) {
      difficultyOfCategories.put(difficulty, new ArrayList<>());
    }

    for (String[] line : getLines()) {
      Difficulty diff = Difficulty.valueOf(line[1]); // changes string version of difficulty to enum
      String category = line[0];

      // note empty ArrayLists have been made for each difficulty
      // so add the category to these ArrayLists correspondingly
      difficultyOfCategories.get(diff).add(category);
    }
  }

  /**
   * Method used to choose a new random category when difficulty is supplied. Gets available
   * category pool, removes already played words and checks if it is empty, if so then we get
   * category pool again. choose a random category by random integer depending on pool size and
   * return category as string
   *
   * @param enum difficulty of words
   * @return string representing the selected category
   */
  public String getRandomCategory(Difficulty difficulty) {
    categoryPool = new ArrayList<>();
    getCategoryPool(difficulty);

    // remove all played words
    ArrayList<String> playedWords = UserManager.getCurrentUser().getHistory();
    categoryPool.removeAll(playedWords);

    // check if pool is empty
    if (categoryPool.isEmpty()) {
      // update category completion
      UserManager.getCurrentUser().updateCategoryCompletion(difficulty);
      getCategoryPool(difficulty);
    }

    int difficultySize = categoryPool.size();
    // note nextInt(value) chooses any number from 0 inclusive to value exclusive
    String randomCategory = categoryPool.get(new Random().nextInt(difficultySize));

    return randomCategory;
  }

  /**
   * method used to get a category pool related to specified category. returns category pool as a
   * list of strings
   *
   * @param enum specified difficulty of words
   * @return list of strings containing categories
   */
  private List<String> getCategoryPool(Difficulty difficulty) {
    // switch cases by difficulty
    if (difficulty.equals(Difficulty.E)) { // only Easy
      categoryPool.addAll(difficultyOfCategories.get(Difficulty.E));
    } else if (difficulty.equals(Difficulty.M)) { // medium and easy
      categoryPool.addAll(difficultyOfCategories.get(Difficulty.E));
      categoryPool.addAll(difficultyOfCategories.get(Difficulty.M));
    } else if (difficulty.equals(Difficulty.H)) { // all words
      categoryPool.addAll(difficultyOfCategories.get(Difficulty.E));
      categoryPool.addAll(difficultyOfCategories.get(Difficulty.M));
      categoryPool.addAll(difficultyOfCategories.get(Difficulty.H));
    } else if (difficulty.equals(Difficulty.MASTER)) { // only hard
      categoryPool.addAll(difficultyOfCategories.get(Difficulty.H));
    } else { // not recognized
      System.out.println("Difficulty is not recognized");
    }
    return categoryPool;
  }

  /**
   * This method reads the lines of category_difficulty.csv file
   *
   * @return list of strings with the first element being the category, and the second element is
   *     the difficulty
   * @throws IOException file not found
   * @throws CsvException csv problem
   * @throws URISyntaxException uri problem
   */
  protected List<String[]> getLines() throws IOException, CsvException, URISyntaxException {
    // find file
    File file = new File(CategorySelector.class.getResource("/category_difficulty.csv").toURI());
    // read and return all lines in the file
    try (FileReader fr = new FileReader(file, StandardCharsets.UTF_8);
        CSVReader reader = new CSVReader(fr)) {
      return reader.readAll();
    }
  }
}
