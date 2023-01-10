package nz.ac.auckland.se206;

import com.opencsv.exceptions.CsvException;
import java.io.IOException;
import java.net.URISyntaxException;
import nz.ac.auckland.se206.words.CategorySelector;
import nz.ac.auckland.se206.words.DefinitionFinder;

/**
 * This class will be an instance of game information. Contains word difficulty, timer value,
 * category, and category selector
 *
 * @author Sergei Ogai
 */
public class SketchGame {
  public enum Difficulty {
    E,
    M,
    H,
    MASTER;
  }

  public enum GameType {
    NORMAL,
    HIDDEN_WORD;
  }

  private Difficulty wordDif = Difficulty.E;
  private Difficulty accuracyDif = Difficulty.E;
  private Difficulty confidenceDif = Difficulty.E;
  private Difficulty timeDif = Difficulty.E;

  private CategorySelector categorySelector;
  private DefinitionFinder definitionFinder;
  private String category;
  private String definition;
  private int accuracy;
  private int confidence;

  private GameType gameType;

  private int timer = 60;
  private boolean isGameOver = true;

  /**
   * constructor for game. Loads the category selector, difficulty and sets the category
   *
   * @throws IOException files can't be found
   * @throws CsvException files can't be read
   * @throws URISyntaxException uri problems with csv files
   */
  public SketchGame() throws IOException, CsvException, URISyntaxException {

    categorySelector = new CategorySelector();
    definitionFinder = new DefinitionFinder();
  }

  /**
   * method that is used to set new difficulties for game
   *
   * @param words. Difficulty of words
   * @param accuracy. Difficulty of accuracy
   * @param confidence. Difficulty of confidence
   * @param time. Difficulty of time
   */
  public void setDifficulty(
      Difficulty words, Difficulty accuracy, Difficulty confidence, Difficulty time) {
    // set game fields to input values
    this.wordDif = words;
    this.accuracyDif = accuracy;
    this.confidenceDif = confidence;
    this.timeDif = time;
  }

  /** method to reset game with already defined difficulties. set over to false */
  public void resetGame() {
    // reset game fields based on difficulties
    setCategory();
    setTimer();
    setAccuracy();
    setConfidence();
    isGameOver = false;
  }

  /** This method will set Category of the game right before you can play it. */
  private void setCategory() {
    // get category and definition
    category = categorySelector.getRandomCategory(wordDif);
    definition = definitionFinder.getDefinition(category);
    // add category to history
    UserManager.getCurrentUser().addCategoryToHistory(category);
  }

  /**
   * This method will set Timer of the game right before you can play it. Easy:60 sec Medium:45 sec
   * Hard: 30 sec Master:15 sec
   */
  private void setTimer() {
    // check difficulty
    if (timeDif.equals(Difficulty.E)) {
      timer = 60;
    } else if (timeDif.equals(Difficulty.M)) {
      timer = 45;
    } else if (timeDif.equals(Difficulty.H)) {
      timer = 30;
    } else if (timeDif.equals(Difficulty.MASTER)) {
      timer = 15;
    }
  }

  /**
   * This method will set Accuracy of the game right before you can play it. Easy: top 3 predictions
   * Medium: top 2 predictions Hard: top 1 prediction
   */
  private void setAccuracy() {
    // check difficulty
    if (accuracyDif.equals(Difficulty.E)) {
      accuracy = 3;
    } else if (accuracyDif.equals(Difficulty.M)) {
      accuracy = 2;
    } else if (accuracyDif.equals(Difficulty.H)) {
      accuracy = 1;
    }
  }

  /**
   * This method will set Confidence of the game right before you can play it. Easy: 1% Medium: 10%
   * Hard: 25% Master: 50%
   */
  private void setConfidence() {
    // check difficulty
    if (confidenceDif.equals(Difficulty.E)) {
      confidence = 1;
    } else if (confidenceDif.equals(Difficulty.M)) {
      confidence = 10;
    } else if (confidenceDif.equals(Difficulty.H)) {
      confidence = 25;
    } else if (confidenceDif.equals(Difficulty.MASTER)) {
      confidence = 50;
    }
  }

  /**
   * returns accuracy of game as integer
   *
   * @return int accuracy, top accurate prediction to win
   */
  public int getAccuracy() {
    return accuracy;
  }

  /**
   * returns difficulty of words as enum
   *
   * @return Difficulty wordDif. difficulty of words
   */
  public Difficulty getDifficulty() {
    return wordDif;
  }

  /**
   * returns the current category of game as String
   *
   * @return String category of game
   */
  public String getCategory() {
    return category;
  }

  /**
   * gets timer value as integer
   *
   * @return int timer of game
   */
  public int getTime() {
    return timer;
  }

  /**
   * gets confidence value as integer
   *
   * @return int confidence percentage to win
   */
  public int getConfidence() {
    return confidence;
  }

  /** decreases timer value by 1. called every second */
  public void decreaseTime() {
    timer--;
  }

  /** increase win count for current user and try to update fastest time */
  public void win() {
    // increase user wins and set game to over
    UserManager.getCurrentUser().incrementWins();
    isGameOver = true;

    // change time depending on difficulty
    if (timeDif.equals(Difficulty.E)) {
      UserManager.getCurrentUser().setFastestTime(60 - timer);
    } else if (timeDif.equals(Difficulty.M)) {
      UserManager.getCurrentUser().setFastestTime(45 - timer);
    } else if (timeDif.equals(Difficulty.H)) {
      UserManager.getCurrentUser().setFastestTime(30 - timer);
    } else { // Master difficulty
      UserManager.getCurrentUser().setFastestTime(15 - timer);
    }
  }

  /** increase loss count for current user and set game to over */
  public void lose() {
    // increase user losses and set game to be over
    UserManager.getCurrentUser().incrementLosses();
    isGameOver = true;
  }

  /**
   * return status of game
   *
   * @return boolean isGameOver status of game
   */
  public boolean getIsGameOver() {
    return isGameOver;
  }

  /**
   * set status of game
   *
   * @param boolean isGameOver status of game
   */
  public void setIsGameOver(boolean isGameOver) {
    this.isGameOver = isGameOver;
  }

  /**
   * return definition of category
   *
   * @return String definition of category
   */
  public String getDefinition() {
    return definition;
  }

  /**
   * return game type
   *
   * @return GameType gameType either NORMAL or HIDDEN_WORD
   */
  public GameType getGameType() {
    return gameType;
  }

  /**
   * set game type
   *
   * @param GameType gameType either NORMAL or HIDDEN_WORD
   */
  public void setGameType(GameType gameType) {
    this.gameType = gameType;
  }
}
