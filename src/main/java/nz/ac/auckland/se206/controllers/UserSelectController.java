package nz.ac.auckland.se206.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.User;
import nz.ac.auckland.se206.UserManager;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;

public class UserSelectController implements Controller {

  @FXML private BorderPane borderPane;

  @FXML private Button btnUserSelect;

  @FXML private Button btnCreateUser;

  @FXML private Button btnDeleteUser;

  @FXML private Button btnHome;

  @FXML private ListView<String> listViewUsers;

  @FXML private FlowPane flowPane;

  private String userSelected;

  private Button selectedButton;

  private Button selectButton;

  private Boolean isDeleting = false;

  private ArrayList<Button> btn;

  /** initialize scene. display users that were already saved in the data */
  public void initialize() {

    // display pre-existing usernames from last time game was opened
    displayUsernames();
    // update the list every time this scene is shown
    borderPane.setOnMouseEntered(
        new EventHandler<MouseEvent>() {

          @Override
          public void handle(MouseEvent event) {
            // rewrite usenames
            flowPane.getChildren().clear();
            displayUsernames();
          }
        });
  }

  /** method used to create buttons that display usernames of users in user map */
  private void displayUsernames() {
    ArrayList<String> usernames = new ArrayList<String>();
    btn = new ArrayList<Button>();
    onNotDelete();
    try {
      // create a button for each user in user map
      for (Entry<Integer, User> user : UserManager.getAllUsers()) {
        usernames.add(user.getValue().getUsername());
        Button button = new Button(user.getValue().getUsername());
        button.setPrefSize(100, 100);
        button.getStylesheets().add("/css/userButton.css");
        button.setOnMouseClicked(
            new EventHandler<MouseEvent>() {
              @Override
              public void handle(MouseEvent event) {
                try {
                  whenSelect(event, button);
                } catch (IOException e) {
                  throw new RuntimeException(e);
                }
              }
            });

        button.setOnAction(
            new EventHandler<ActionEvent>() {
              @Override
              public void handle(ActionEvent event) {
                userSelected = button.getText();
                selectedButton = button;
              }
            });
        // add button to array
        btn.add(button);
      }

    } catch (NullPointerException e) {
      System.out.print("bad");
    }
    // add a create user button
    selectButton = btnCreateUser;
    selectButton.getStylesheets().add("/css/createButton.css");
    selectButton.setPrefSize(100, 100);
    flowPane.getChildren().addAll(btn);
    flowPane.getChildren().add(selectButton);

    // remove create user button if flowpane size is 10
    if (flowPane.getChildren().size() == 10) {
      flowPane.getChildren().remove(selectButton);
    }
  }

  /**
   * When "Play" button is pressed the menu scene is loaded Check if a user is selected before
   * loading menu scene.
   *
   * @param event click event
   * @throws IOException file not found
   */
  private void whenSelect(MouseEvent event, Button button) throws IOException {
    // play sound
    App.playButtonClick();

    // load last difficulties to difficulty selection scene
    UserManager.setCurrentUser(button.getText());
    DifficultySelectionController diff =
        (DifficultySelectionController) SceneManager.getUiRootController(AppUi.DIFFICULTY_SELECT);
    diff.loadPresetDifficulties(UserManager.getCurrentUser());

    Button newButton = (Button) event.getSource();
    Scene sceneOfButton = newButton.getScene();

    // switch to mainmenu scene
    Parent readySceneRoot = SceneManager.getUiRoot(AppUi.MAIN_MENU);
    sceneOfButton.setRoot(readySceneRoot);
  }

  /**
   * method executed when create new user button is pressed. check if user number doesn't exceed 9.
   * move scene to user create scene
   *
   * @param event click event
   * @throws IOException file not found
   */
  @FXML
  private void onCreate(ActionEvent event) throws IOException {
    // play sound
    App.playButtonClick();

    // check number of users doesn't exceed 9
    if (flowPane.getChildren().size() <= 9) {
      // switch to usercreate scene
      Button button = (Button) event.getSource();
      Scene sceneOfButton = button.getScene();

      Parent usercreateSceneRoot = SceneManager.getUiRoot(AppUi.USER_CREATE);
      sceneOfButton.setRoot(usercreateSceneRoot);
    }
  }

  /**
   * method used when deleting a button from button list
   *
   * @throws IOException file not found
   */
  @FXML
  private void onDelete() throws IOException {
    // check if not already deleting and there are buttons to delete
    if (!isDeleting && UserManager.getSize() != 0) {
      isDeleting = true;
      btnDeleteUser.setStyle("-fx-background-image: url(\"/images/openBin.png\")");
      for (int i = 0; i < flowPane.getChildren().size(); i++) {
        selectedButton = (Button) flowPane.getChildren().get(i);
        flowPane
            .getChildren()
            .get(i)
            .setOnMouseClicked(
                new EventHandler<MouseEvent>() {
                  // when button representing a user is clicked
                  @Override
                  public void handle(MouseEvent event) {
                    flowPane.getChildren().remove(selectedButton);
                    // play sound
                    playDeleteUserSound();

                    try {
                      // delete user from user map
                      UserManager.removeUser(userSelected);

                      userSelected = null;

                      // if size is 8 add a create user button
                      App.saveData();
                      if (flowPane.getChildren().size() == 8) {
                        flowPane.getChildren().add(selectButton);
                      } // stop deleting if no more buttons left
                      if (UserManager.getSize() == 0) {
                        onNotDelete();
                      }

                    } catch (IOException | IllegalArgumentException e) {
                      System.out.println("bad");
                    }
                  }
                });
      }
    } else { // can't delete
      onNotDelete();
    }
  }

  /** method used to select users when pressing on a user button */
  private void onNotDelete() {
    // turn deleting boolean false
    isDeleting = false;
    // update image
    btnDeleteUser.setStyle("-fx-background-image: url(\"/images/closeBin.png\")");
    for (int i = 0; i < flowPane.getChildren().size() - 1; i++) {
      selectedButton = (Button) flowPane.getChildren().get(i);
      int finalI = i;
      flowPane
          .getChildren()
          .get(i)
          .setOnMouseClicked(
              new EventHandler<MouseEvent>() {
                // when button representing a user is clicked
                @Override
                public void handle(MouseEvent event) {
                  try { // set current user and move to main menu
                    whenSelect(event, (Button) flowPane.getChildren().get(finalI));
                  } catch (IOException | NullPointerException e) {
                    throw new RuntimeException(e);
                  }
                }
              });
    }
  }

  /**
   * executed when to home button is pressed. moves scene to title scene.
   *
   * @param event click event
   * @throws IOException file not found
   */
  @FXML
  private void onMoveToHome(ActionEvent event) throws IOException {
    // play sound
    App.playButtonClick();

    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();

    Parent readySceneRoot = SceneManager.getUiRoot(AppUi.TITLE);
    // switch to title scene
    TitleController titleController =
        (TitleController) SceneManager.getUiRootController(AppUi.TITLE);
    titleController.run();

    sceneOfButton.setRoot(readySceneRoot);
  }

  /** method used to play delete sound when a user is deleted */
  private void playDeleteUserSound() {
    // check that we are deleting
    if (isDeleting) {
      App.playDeleteSound();
    }
  }
}
