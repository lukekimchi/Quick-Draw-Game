package nz.ac.auckland.se206.controllers;

import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;

public class MainMenuController implements Initializable, Controller {

  @FXML private Button btnPlay;

  @FXML private Button btnUserSelect;
  @FXML private Button btnStats;

  @Override
  public void initialize(URL location, ResourceBundle resources) {}

  /**
   * When "Play" button is pressed the difficulty select scene is loaded
   *
   * @param event click event
   * @throws IOException file not found
   * @throws URISyntaxException uri problem
   * @throws CsvException csv problem
   */
  @FXML
  private void onPlay(ActionEvent event) throws IOException, CsvException, URISyntaxException {
    // play sound
    playStartGameSound();
    // switch to difficulty selection scene
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();

    sceneOfButton.setRoot(SceneManager.getUiRoot(AppUi.MODE_SELECT));
  }

  /**
   * executed when stats button is pressed. moves scene to stats scene
   *
   * @param event click event
   * @throws IOException file not found
   */
  @FXML
  private void onStat(ActionEvent event) throws IOException {
    // play sound
    App.playButtonClick();
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();

    // switch to stats scene
    Parent readySceneRoot = SceneManager.getUiRoot(AppUi.STATS);
    sceneOfButton.setRoot(readySceneRoot);
  }

  /**
   * executed when user select button is pressed. moves scene to user select scene
   *
   * @param event click event
   * @throws IOException file not found
   */
  @FXML
  private void onUserSelect(ActionEvent event) throws IOException {
    // play sound
    App.playButtonClick();
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();

    // switch to userselect scene
    Parent readySceneRoot = SceneManager.getUiRoot(AppUi.USER_SELECT);
    sceneOfButton.setRoot(readySceneRoot);
  }

  /** method used to play start game sound when a new game is started */
  private void playStartGameSound() {
    // find sound file
    File startGameSound = new File(App.class.getResource("/sounds/startGame.wav").toString());
    // play sound
    MediaPlayer player = new MediaPlayer(new Media(startGameSound.toString().replace("\\", "/")));
    player.play();
  }
}
