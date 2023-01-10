package nz.ac.auckland.se206;

import java.util.ArrayList;
import nz.ac.auckland.se206.SketchGame.Difficulty;

/**
 * this class is used to store user data such as username, history of words, wins, loses, fastest
 * time, last difficulty, achievements
 *
 * @author serge
 */
public class User {

  private String username;
  private ArrayList<String> history;
  private int wins;
  private int losses;
  private int fastestTime;
  private ArrayList<String> lastDifficulty;
  private ArrayList<Boolean> achievements;
  private ArrayList<Boolean> categoryCompletion;

  /**
   * constructor for user class
   *
   * @param username String representing name
   * @param wins int number of wins
   * @param losses int number of losses
   * @param history array of strings
   * @param fastestTime int time
   * @param lastDifficulty array of strings that are converted to enums later
   * @param achivements array of booleans
   */
  public User(
      String username,
      int wins,
      int losses,
      ArrayList<String> history,
      int fastestTime,
      ArrayList<String> lastDifficulty,
      ArrayList<Boolean> achievements,
      ArrayList<Boolean> categoryCompletion) {
    // set local fields equal to input values
    this.username = username;
    this.history = history;
    this.wins = wins;
    this.losses = losses;
    this.fastestTime = fastestTime;
    this.lastDifficulty = lastDifficulty;
    this.achievements = achievements;
    this.categoryCompletion = categoryCompletion;
  }

  /**
   * method used to get the username of this user as a String
   *
   * @return String username. unique name of a user
   */
  public String getUsername() {
    return username;
  }

  /**
   * prepends a string representing category into the history list
   *
   * @param String category
   */
  public void addCategoryToHistory(String category) {
    history.add(0, category);
  }

  /**
   * method that checks if input time is smaller than previous time. Replaces fastest time if the
   * result is true
   *
   * @param int timeToWin. Time that was used to win a game
   */
  public void setFastestTime(int timeToWin) {
    if (timeToWin < fastestTime) {
      this.fastestTime = timeToWin;
    }
  }

  /**
   * method to return fastest win time
   *
   * @return int fastestTime. fastest time used to beat a game
   */
  public int getFastestTime() {
    return fastestTime;
  }

  /**
   * method to return history as an array of strings
   *
   * @return ArrayList of strings representing played words
   */
  public ArrayList<String> getHistory() {
    return history;
  }

  /**
   * method to save last played difficulty as an array list
   *
   * @param enum wordDiff Difficulty of words
   * @param enum timeDiff Difficulty of time
   * @param enum accuracyDiff Difficulty of accuracy
   * @param enum confidenceDiff Difficulty of confidence
   */
  public void setLastDifficulty(
      String wordDiff, String timeDiff, String accuracyDiff, String confidenceDiff) {
    // reset previously played category
    lastDifficulty.clear();
    // save last played category
    lastDifficulty.add(wordDiff);
    lastDifficulty.add(timeDiff);
    lastDifficulty.add(accuracyDiff);
    lastDifficulty.add(confidenceDiff);
  }

  /**
   * Method to return last played difficulty of the user
   *
   * @return ArrayList of strings representing enums ordered by
   *     WordDiff,TimeDiff,AccuracyDiff,ConfidenceDiff
   */
  public ArrayList<String> getLastDifficulty() {
    return lastDifficulty;
  }

  /** method to increase number of wins and check for achievement changes */
  public void incrementWins() {
    this.wins++;
    updateAchievements(true);
  }

  /** method to increase number of loses and check for achievement changes */
  public void incrementLosses() {
    this.losses++;
    updateAchievements(false);
  }

  /**
   * method to return wins
   *
   * @return int number of wins
   */
  public int getWins() {
    return wins;
  }

  /**
   * method to return loses
   *
   * @return int number of losses
   */
  public int getLosses() {
    return losses;
  }

  /**
   * method to return array list of booleans representing achievements
   *
   * @return ArrayList of booleans
   */
  public ArrayList<Boolean> getAchievements() {
    return achievements;
  }

  /**
   * update achievements
   *
   * <p>0: first win 1: first loss 2: 10 wins 3: beat hard time difficulty 4: beat master time
   * difficulty 5: play all easy words 6: play all medium words 7: play all hard words
   */
  private void updateAchievements(boolean win) {
    // win once
    if (achievements.get(0).equals(false)) {
      if (win) {
        achievements.set(0, true);
      }
    }
    // lose once
    if (achievements.get(1).equals(false)) {
      if (!win) {
        achievements.set(1, true);
      }
    }
    // win 10 times
    if (achievements.get(2).equals(false)) {
      if (wins > 10) {
        achievements.set(2, true);
      }
    }
    // beat hard time
    if (achievements.get(3).equals(false)) {
      if (win && lastDifficulty.get(1).equals("H")) {
        achievements.set(3, true);
      }
    }
    // beat master time, check last difficulty is master
    if (achievements.get(4).equals(false)) {
      if (win && lastDifficulty.get(1).equals("MASTER")) {
        achievements.set(4, true);
      }
    }
    // play all easy words, check easy words are completed
    if (achievements.get(5).equals(false)) {
      if (categoryCompletion.get(0).equals(true)) {
        achievements.set(5, true);
      }
    }
    // play all medium words
    if (achievements.get(6).equals(false)) {
      if (categoryCompletion.get(1).equals(true)) {
        achievements.set(6, true);
      }
    }
    // play all hard words
    if (achievements.get(7).equals(false)) {
      if (categoryCompletion.get(2).equals(true)) {
        achievements.set(7, true);
      }
    }
  }

  /**
   * method that returns array of booleans representing category difficulty completion index:
   * difficulty 0: easy 1: medium 2: hard
   *
   * @return ArrayList of booleans
   */
  public ArrayList<Boolean> getCategoryCompletion() {
    return categoryCompletion;
  }

  /**
   * update completion when a cycle is detected in category selector
   *
   * @param difficulty of words completed
   */
  public void updateCategoryCompletion(Difficulty difficulty) {
    if (difficulty.equals(Difficulty.E)) {
      categoryCompletion.set(0, true);
    } else if (difficulty.equals(Difficulty.M)) { // set both easy and medium words to true
      categoryCompletion.set(0, true);
      categoryCompletion.set(1, true);
    } else if (difficulty.equals(Difficulty.H)) { // set all words true
      categoryCompletion.set(0, true);
      categoryCompletion.set(1, true);
      categoryCompletion.set(2, true);
    } else { // difficulty is master, set hard words true
      categoryCompletion.set(2, true);
    }
  }
}
