package nz.ac.auckland.se206.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.FlowPane;
import nz.ac.auckland.se206.User;
import nz.ac.auckland.se206.UserManager;

public class DisplayBadgesController implements Controller {

  @FXML private Button btnToMenu;

  @FXML private FlowPane fpBadgesPane;

  @FXML
  private void onMoveToMenu(ActionEvent event) {
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();

    // switch to mainmenu scene
    Parent readySceneRoot = SceneManager.getUiRoot(SceneManager.AppUi.MAIN_MENU);
    sceneOfButton.setRoot(readySceneRoot);
  }

  protected void run() {
    fpBadgesPane.getChildren().clear();

    User currentUser = UserManager.getCurrentUser();

    fpBadgesPane.getStylesheets().add("/css/badges.css");

    // first win badge
    Button firstWin = new Button();
    firstWin.setId("firstWin");

    // first loss badge
    Button firstLoss = new Button();
    firstLoss.setId("firstLoss");

    // 10 wins badge
    Button tenWins = new Button();
    tenWins.setId("tenWins");

    // beatHardTime wins badge
    Button beatHardTime = new Button();
    beatHardTime.setId("beatHardTime");

    // beatMasterTime wins badge
    Button beatMasterTime = new Button();
    beatMasterTime.setId("beatMasterTime");

    // allEasyWords badge
    Button allEasyWords = new Button();
    allEasyWords.setId("allEasyWords");

    // allMediumWords badge
    Button allMediumWords = new Button();
    allMediumWords.setId("allMediumWords");

    // allHardWords badge
    Button allHardWords = new Button();
    allHardWords.setId("allHardWords");

    if (currentUser.getAchievements().get(0).equals(true)
        && !fpBadgesPane.getChildren().contains(firstWin)) {
      fpBadgesPane.getChildren().add(firstWin);
    }

    if (currentUser.getAchievements().get(1).equals(true)
        && !fpBadgesPane.getChildren().contains(firstLoss)) {
      fpBadgesPane.getChildren().add(firstLoss);
    }

    if (currentUser.getAchievements().get(2).equals(true)
        && !fpBadgesPane.getChildren().contains(tenWins)) {
      fpBadgesPane.getChildren().add(tenWins);
    }
    if (currentUser.getAchievements().get(3).equals(true)
        && !fpBadgesPane.getChildren().contains(beatHardTime)) {
      fpBadgesPane.getChildren().add(beatHardTime);
    }
    if (currentUser.getAchievements().get(4).equals(true)
        && !fpBadgesPane.getChildren().contains(beatMasterTime)) {
      fpBadgesPane.getChildren().add(beatMasterTime);
    }
    if (currentUser.getAchievements().get(5).equals(true)
        && !fpBadgesPane.getChildren().contains(allEasyWords)) {
      fpBadgesPane.getChildren().add(allEasyWords);
    }
    if (currentUser.getAchievements().get(6).equals(true)
        && !fpBadgesPane.getChildren().contains(allMediumWords)) {
      fpBadgesPane.getChildren().add(allMediumWords);
    }
    if (currentUser.getAchievements().get(7).equals(true)
        && !fpBadgesPane.getChildren().contains(allHardWords)) {
      fpBadgesPane.getChildren().add(allHardWords);
    }
  }
}
