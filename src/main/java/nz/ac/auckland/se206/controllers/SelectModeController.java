package nz.ac.auckland.se206.controllers;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SketchGame.GameType;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;

public class SelectModeController implements Controller {

  @FXML private Button btnHiddenWordMode;

  @FXML private Button btnNormalMode;

  @FXML private Button btnZenMode;

  @FXML private Button btnBack;

  /**
   * method executed when normal mode button is pressed. Set game type to normal and switch scene to
   * difficulty select
   *
   * @param event click event
   */
  @FXML
  private void onSelectNormalMode(ActionEvent event) {
    // stop music
    App.stopBGM();
    // play sound
    playNormalModeSound();
    Button button = (Button) event.getSource();
    Scene currentScene = button.getScene();

    // set game type to normal
    CanvasController canvas = (CanvasController) SceneManager.getUiRootController(AppUi.CANVAS);
    canvas.getSketchGame().setGameType(GameType.NORMAL);

    // switch to difficultyselection scene
    currentScene.setRoot(SceneManager.getUiRoot(AppUi.DIFFICULTY_SELECT));
  }

  /**
   * method executed when hidden mode button is pressed. Set game type to hidden word and switch
   * scene to difficulty select
   *
   * @param event click event
   */
  @FXML
  private void onSelectHiddenWordMode(ActionEvent event) {
    // stop music
    App.stopBGM();
    // play sound
    playHiddenModeSound();
    Button button = (Button) event.getSource();
    Scene currentScene = button.getScene();

    // set game type to hidden word
    CanvasController canvas = (CanvasController) SceneManager.getUiRootController(AppUi.CANVAS);
    canvas.getSketchGame().setGameType(GameType.HIDDEN_WORD);

    // switch to difficultyselection scene
    currentScene.setRoot(SceneManager.getUiRoot(AppUi.DIFFICULTY_SELECT));
  }

  /**
   * method executed when zen mode is selected. move scene to zen mode scene
   *
   * @param event click event
   */
  @FXML
  private void onSelectZenMode(ActionEvent event) {
    // stop music
    App.stopBGM();
    // play sound
    playZenModeSound();
    Button button = (Button) event.getSource();
    Scene currentScene = button.getScene();

    // switch to zenmode scene
    ZenModeController zenModeController =
        (ZenModeController) SceneManager.getUiRootController(AppUi.ZEN_MODE);
    zenModeController.resetScene();
    currentScene.setRoot(SceneManager.getUiRoot(AppUi.ZEN_MODE));
  }

  /**
   * executed when back button is pressed. moves scene to main menu
   *
   * @param event click event
   */
  @FXML
  private void onSelectBack(ActionEvent event) {
    // play sound
    App.playButtonClick();
    Button button = (Button) event.getSource();
    Scene currentScene = button.getScene();

    // switch back to mainmenu scene
    currentScene.setRoot(SceneManager.getUiRoot(AppUi.MAIN_MENU));
  }

  /** method used to play normal game sound when normal mode is selected */
  private void playNormalModeSound() {
    // find sound file
    File startGameSound = new File(App.class.getResource("/sounds/startGame.wav").toString());
    // play sound
    MediaPlayer player = new MediaPlayer(new Media(startGameSound.toString().replace("\\", "/")));
    player.play();
  }

  /** method used to play Hidden word game sound when Hidden word mode is selected */
  private void playHiddenModeSound() {
    // find sound file
    File sound = new File(App.class.getResource("/sounds/hiddenMode.wav").toString());
    // play sound
    MediaPlayer player = new MediaPlayer(new Media(sound.toString().replace("\\", "/")));
    player.play();
  }

  /** method used to play zen game sound when normal zen is selected */
  private void playZenModeSound() {
    // find sound file
    File sound = new File(App.class.getResource("/sounds/zenMode.wav").toString());
    // play sound
    MediaPlayer player = new MediaPlayer(new Media(sound.toString().replace("\\", "/")));
    player.play();
  }
}
