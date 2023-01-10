package nz.ac.auckland.se206.controllers;

import java.util.ArrayList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SketchGame;
import nz.ac.auckland.se206.SketchGame.Difficulty;
import nz.ac.auckland.se206.User;
import nz.ac.auckland.se206.UserManager;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;

/**
 * scene used to select difficulty of sketchGame before moving to canvas
 *
 * @author serge
 */
public class DifficultySelectionController implements Controller {
  @FXML private Label lblWordDifficulty;
  @FXML private Label lblTimeDifficulty;
  @FXML private Label lblAccuracyDifficulty;
  @FXML private Label lblConfidenceDifficulty;

  @FXML private Button btnWordCycle;
  @FXML private Button btnTimeCycle;
  @FXML private Button btnAccuracyCycle;
  @FXML private Button btnConfidenceCycle;
  @FXML private Button btnConfirm;
  @FXML private Button btnReturn;

  @FXML private Label wordDescription;
  @FXML private Label timeDescription;
  @FXML private Label accuracyDescription;
  @FXML private Label confidenceDescription;

  /**
   * this method will confirm difficulty selection. Set game difficulty and reset game before moving
   * to canvas scene
   *
   * @param event click event
   */
  @FXML
  private void onConfirm(ActionEvent event) {
    App.playButtonClick();
    CanvasController controller = (CanvasController) SceneManager.getUiRootController(AppUi.CANVAS);

    // get difficulties from labels
    Difficulty wordDif = Difficulty.valueOf(lblWordDifficulty.getText());
    Difficulty timeDif = Difficulty.valueOf(lblTimeDifficulty.getText());
    Difficulty accuracyDif = Difficulty.valueOf(lblAccuracyDifficulty.getText());
    Difficulty confidenceDif = Difficulty.valueOf(lblConfidenceDifficulty.getText());

    SketchGame game = controller.getSketchGame();
    game.setDifficulty(wordDif, accuracyDif, confidenceDif, timeDif);
    game.resetGame();
    controller.resetScene();

    // set last played difficulty
    UserManager.getCurrentUser()
        .setLastDifficulty(
            lblWordDifficulty.getText(),
            lblTimeDifficulty.getText(),
            lblAccuracyDifficulty.getText(),
            lblConfidenceDifficulty.getText());

    // get the scene the clicked button is in
    Button buttonClicked = (Button) event.getSource();
    Scene sceneOfButton = buttonClicked.getScene();
    // switch to canvas scene
    sceneOfButton.setRoot(SceneManager.getUiRoot(AppUi.CANVAS));
  }

  /**
   * method executed when return button is pressed. returns scene to main menu
   *
   * @param event click event
   */
  @FXML
  private void onReturn(ActionEvent event) {
    // play sound
    App.playButtonClick();
    // get the scene the clicked button is in
    Button buttonClicked = (Button) event.getSource();
    Scene sceneOfButton = buttonClicked.getScene();
    // switch to canvas scene
    sceneOfButton.setRoot(SceneManager.getUiRoot(AppUi.MAIN_MENU));
  }

  /** cycling the word difficulty label between E,M,H,Master */
  @FXML
  private void onWordCycle() {
    // play sound
    App.playButtonClick();

    String difficulty = lblWordDifficulty.getText();
    // check difficulty on label
    if (difficulty.equals("E")) {
      lblWordDifficulty.setText("M");

    } else if (difficulty.equals("M")) {
      lblWordDifficulty.setText("H");

    } else if (difficulty.equals("H")) {
      lblWordDifficulty.setText("MASTER");

    } else if (difficulty.equals("MASTER")) {
      lblWordDifficulty.setText("E");

    } else { // unknown difficulty
      System.out.println("Something is wrong with word difficulty");
    }
    updateDifficultyLabels();
  }

  /** cycling the time difficulty label between E,M,H,Master */
  @FXML
  private void onTimeCycle() {
    // play sound
    App.playButtonClick();

    String difficulty = lblTimeDifficulty.getText();
    // check difficulty on label
    if (difficulty.equals("E")) {
      lblTimeDifficulty.setText("M");

    } else if (difficulty.equals("M")) {
      lblTimeDifficulty.setText("H");

    } else if (difficulty.equals("H")) {
      lblTimeDifficulty.setText("MASTER");

    } else if (difficulty.equals("MASTER")) {
      lblTimeDifficulty.setText("E");
    } else { // unknown difficulty
      System.out.println("Something is wrong with time difficulty");
    }
    updateDifficultyLabels();
  }

  /** cycling the accuracy difficulty label between E,M,H */
  @FXML
  private void onAccuracyCycle() {
    // play sound
    App.playButtonClick();

    String difficulty = lblAccuracyDifficulty.getText();
    // check difficulty on label
    if (difficulty.equals("E")) {
      lblAccuracyDifficulty.setText("M");
    } else if (difficulty.equals("M")) {
      lblAccuracyDifficulty.setText("H");
    } else if (difficulty.equals("H")) {
      lblAccuracyDifficulty.setText("E");
    } else { // unknown difficulty
      System.out.println("Something is wrong with accuracy difficulty");
    }
    updateDifficultyLabels();
  }

  /** cycling the confidence difficulty label between E,M,H,Master */
  @FXML
  private void onConfidenceCycle() {
    // play sound
    App.playButtonClick();

    String difficulty = lblConfidenceDifficulty.getText();
    // check difficulty on label
    if (difficulty.equals("E")) {
      lblConfidenceDifficulty.setText("M");

    } else if (difficulty.equals("M")) {
      lblConfidenceDifficulty.setText("H");

    } else if (difficulty.equals("H")) {
      lblConfidenceDifficulty.setText("MASTER");

    } else if (difficulty.equals("MASTER")) {
      lblConfidenceDifficulty.setText("E");

    } else { // unknown difficulty
      System.out.println("Something is wrong with confidence difficulty");
    }
    updateDifficultyLabels();
  }

  /** method used to update label descriptions depending on difficulty on label */
  private void updateDifficultyLabels() {

    // Check confidence
    if (lblConfidenceDifficulty.getText().equals("M")) {
      confidenceDescription.setText("at least 10%");

    } else if (lblConfidenceDifficulty.getText().equals("H")) {
      confidenceDescription.setText("at least 25%");

    } else if (lblConfidenceDifficulty.getText().equals("MASTER")) {
      confidenceDescription.setText("at least 50%");

    } else if (lblConfidenceDifficulty.getText().equals("E")) {
      confidenceDescription.setText("at least 1%");
    }

    // Check accuracy
    if (lblAccuracyDifficulty.getText().equals("M")) {
      accuracyDescription.setText("Top 2 Guesses");

    } else if (lblAccuracyDifficulty.getText().equals("H")) {
      accuracyDescription.setText("Top 1 Guesses");

    } else if (lblAccuracyDifficulty.getText().equals("E")) {
      accuracyDescription.setText("Top 3 Guesses");
    }

    // Check time difficulty
    if (lblTimeDifficulty.getText().equals("M")) {
      timeDescription.setText("45s");

    } else if (lblTimeDifficulty.getText().equals("H")) {
      timeDescription.setText("30s");

    } else if (lblTimeDifficulty.getText().equals("MASTER")) {
      timeDescription.setText("15s");

    } else if (lblTimeDifficulty.getText().equals("E")) {
      timeDescription.setText("60s");
    }

    // Check word difficulty
    if (lblWordDifficulty.getText().equals("M")) {
      wordDescription.setText("E & M words");

    } else if (lblWordDifficulty.getText().equals("H")) {
      wordDescription.setText("E, M & H words");

    } else if (lblWordDifficulty.getText().equals("MASTER")) {
      wordDescription.setText("ONLY Hard words");

    } else if (lblWordDifficulty.getText().equals("E")) {
      wordDescription.setText("ONLY Easy words");
    }
  }

  /**
   * method that presets difficulties according to user's last played difficulty. This method gets
   * called when a new user is selected.
   *
   * @param user instance (current user)
   */
  protected void loadPresetDifficulties(User user) {
    // load last difficulties
    ArrayList<String> lastDiff = user.getLastDifficulty();
    lblWordDifficulty.setText(lastDiff.get(0));
    lblTimeDifficulty.setText(lastDiff.get(1));
    lblAccuracyDifficulty.setText(lastDiff.get(2));
    lblConfidenceDifficulty.setText(lastDiff.get(3));
    // update descriptions
    updateDifficultyLabels();
  }
}
