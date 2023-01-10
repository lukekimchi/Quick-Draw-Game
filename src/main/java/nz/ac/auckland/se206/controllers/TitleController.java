package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;

public class TitleController implements Controller {

  @FXML private Button btnStartGame;

  @FXML private Button btnToggleBGM;

  public void initialize() {
    btnToggleBGM.setStyle("-fx-background-image: url(/images/playBGM.png)");
  }

  /**
   * When "Play" button is pressed the ready scene is loaded
   *
   * @param event click event
   * @throws IOException file not found
   */
  @FXML
  private void onStart(ActionEvent event) throws IOException {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    // play sound
    App.playButtonClick();
    // switch to userselect scene
    Parent readySceneRoot = SceneManager.getUiRoot(AppUi.USER_SELECT);
    sceneOfButton.setRoot(readySceneRoot);
  }

  /** This method allows you to toggle the bgm on and off */
  @FXML
  private void onToggleBGM() {

    // if bgm is playing then show icon with no cross on it to indicate this
    if (App.getIsBGMPlaying()) {
      App.stopBGM();
      btnToggleBGM.setStyle("-fx-background-image: url(/images/muteBGM.png)");

      // if bgm is not playing then show icon with a cross on it to show it is mute
    } else {
      App.playBGM();
      btnToggleBGM.setStyle("-fx-background-image: url(/images/playBGM.png)");
    }
  }

  /** method used to play background music on the title scene */
  public void run() {

    // check music is not playing, if it isn't then play
    if (!App.getIsBGMPlaying()) {
      App.playBGM();
    }

    if (App.getIsBGMPlaying()) {
      btnToggleBGM.setStyle("-fx-background-image: url(/images/playBGM.png)");
    } else {
      btnToggleBGM.setStyle("-fx-background-image: url(/images/muteBGM.png)");
    }
  }
}
