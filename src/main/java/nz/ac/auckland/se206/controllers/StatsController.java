package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.UserManager;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;

public class StatsController implements Controller {

  @FXML private Button btnToMenu;

  @FXML private Button toHome;

  @FXML private Label lblStatsTitle;

  @FXML private FlowPane flowPaneStats;

  @FXML private ListView listWordHistory;

  @FXML private BorderPane borderPane;

  private List<String> stats;

  /** initialise the scene by reading current user fields and creating flow panes */
  @FXML
  private void initialize() {

    borderPane.setOnMouseEntered(
        new EventHandler<MouseEvent>() {

          @Override
          public void handle(MouseEvent event) {

            flowPaneStats.getChildren().clear();

            listWordHistory.getItems().clear();
            // update title label
            lblStatsTitle.setText(UserManager.getCurrentUser().getUsername() + "'s Stats");
            stats = new ArrayList<String>();

            // retrieve user data
            int wins = UserManager.getCurrentUser().getWins();
            int losses = UserManager.getCurrentUser().getLosses();
            int fastestTime = UserManager.getCurrentUser().getFastestTime();
            ArrayList<String> history = UserManager.getCurrentUser().getHistory();

            // add user data to local scene field
            stats.add(wins + "\nWins");
            stats.add(losses + "\nLosses");
            stats.add(fastestTime + "s\nrecord time");

            // add played words to local scene field
            listWordHistory.getItems().addAll(history);

            // create flow panes for stats
            for (int i = 0; i < stats.size(); i++) {
              Label lblStat = new Label(stats.get(i));

              lblStat.getStylesheets().add("/css/statsStyle.css");
              lblStat.setPrefSize(125, 200);
              flowPaneStats.getChildren().add(lblStat);
            }
          }
        });
  }

  /**
   * executed when to menu button is pressed. moves scene to main menu
   *
   * @param event click event
   * @throws IOException file not found
   */
  @FXML
  private void onMoveToMenu(ActionEvent event) throws IOException {
    // play sound
    App.playButtonClick();
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();

    // switch to mainmenu scene
    Parent readySceneRoot = SceneManager.getUiRoot(AppUi.MAIN_MENU);
    sceneOfButton.setRoot(readySceneRoot);
  }

  /**
   * executed when home button is pressed. moves scene to title scene.
   *
   * @param event click event
   * @throws IOException file not found
   */
  @FXML
  private void onMoveToHome(ActionEvent event) throws IOException {
    // play sound
    App.playButtonClick();
    // move to title scene
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();

    Parent readySceneRoot = SceneManager.getUiRoot(AppUi.TITLE);
    sceneOfButton.setRoot(readySceneRoot);
  }

  /**
   * executed when badges button is pressed. moves scene to display badges scene
   *
   * @param event click event
   */
  @FXML
  private void onDisplayBadges(ActionEvent event) {
    // move to display badges scene
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();
    DisplayBadgesController controller =
        (DisplayBadgesController) SceneManager.getUiRootController(AppUi.DISPLAY_BADGES);
    controller.run();
    Parent readySceneRoot = SceneManager.getUiRoot(AppUi.DISPLAY_BADGES);
    sceneOfButton.setRoot(readySceneRoot);
  }
}
