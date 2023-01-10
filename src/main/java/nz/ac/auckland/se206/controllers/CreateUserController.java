package nz.ac.auckland.se206.controllers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.User;
import nz.ac.auckland.se206.UserManager;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;
import org.json.simple.parser.ParseException;

public class CreateUserController implements Controller {

  @FXML private Button btnCreateUser;

  @FXML private TextField txtFieldUsername;

  /**
   * create a user instance. Check for valid username before creating a new user instance and moving
   * to select user scene
   *
   * @param event click event
   * @throws IOException for users and scenes
   * @throws ParseException for users and scenes
   */
  @FXML
  private void onCreateUser(ActionEvent event) throws IOException, ParseException {

    boolean usernameAlreadyExists = false;

    // check if field is empty
    if (txtFieldUsername.getText().isBlank()) {
      // show alert and clear
      showInvalidUsernameAlert();
      txtFieldUsername.clear();

    } else {
      // Check if username already exists
      for (Entry<Integer, User> user : UserManager.getAllUsers()) {
        if (user.getValue() == null) {
          continue;
        } else {
          // if username exists
          if (txtFieldUsername.getText().equals(user.getValue().getUsername())) {
            // show alert and clear
            showUsernameAlreadyExistsAlert();
            usernameAlreadyExists = true;
            txtFieldUsername.clear();
            break;
          }
        }
      }
      if (!usernameAlreadyExists) { // username is valid
        createNewUser();
        switchToUserSelectScene(event);
      }
    }
  }

  /** display an alert when username is invalid */
  private void showInvalidUsernameAlert() {
    // play sound
    App.playErrorSound();
    // show alert
    Alert emptyUsernameAlert = new Alert(Alert.AlertType.ERROR);
    emptyUsernameAlert.setTitle("Error");
    emptyUsernameAlert.setContentText("Enter a valid username");
    emptyUsernameAlert.showAndWait();
  }

  /** display an alert when username already exists */
  private void showUsernameAlreadyExistsAlert() {
    // play sound
    App.playErrorSound();
    // show alert
    Alert usedUsernameAlert = new Alert(Alert.AlertType.ERROR);
    usedUsernameAlert.setTitle("Error");
    usedUsernameAlert.setContentText("Username already exists");
    usedUsernameAlert.showAndWait();
  }

  /**
   * method that will create a new instance of user. Add user instance to UserMap. save all data to
   * json file
   *
   * @throws IOException if data cannot be saved
   * @throws ParseException if data cannot be saved
   */
  private void createNewUser() throws IOException, ParseException {
    // play sound
    playCreateUserSound();

    // create default last difficulty parameter
    ArrayList<String> lastDiff = new ArrayList<>();
    for (int i = 0; i < 4; i++) {
      lastDiff.add("E");
    }
    // generate achievement array with all false by default.
    ArrayList<Boolean> achievements = new ArrayList<>();
    for (int j = 0; j < 8; j++) { // currently 8 achievements, can be changed
      achievements.add(false);
    }
    ArrayList<Boolean> difficultyCompletion = new ArrayList<>();
    for (int k = 0; k < 3; k++) { // currently 3 category difficulties
      difficultyCompletion.add(false);
    }
    // create user instance and add it to user manager
    User user =
        new User(
            txtFieldUsername.getText().trim(),
            0,
            0,
            new ArrayList<String>(),
            60,
            lastDiff,
            achievements,
            difficultyCompletion);
    // save user info
    UserManager.addUser(user);
    App.saveData();
  }

  /**
   * method that return scene to user select after creating a user
   *
   * @param event click event
   */
  private void switchToUserSelectScene(ActionEvent event) throws IOException, ParseException {

    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();

    // clear input text field
    txtFieldUsername.clear();

    Parent userselectSceneRoot = SceneManager.getUiRoot(AppUi.USER_SELECT);
    sceneOfButton.setRoot(userselectSceneRoot);

    // renew User Manager
    UserManager.clear();
    App.readUsers();
  }

  /**
   * method that return scene to user select when manually using return button
   *
   * @param event click event
   */
  @FXML
  private void onUserSelect(ActionEvent event) {
    App.playButtonClick();
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();

    // clear input text field
    txtFieldUsername.clear();

    // switch to userselect scene
    Parent readySceneRoot = SceneManager.getUiRoot(AppUi.USER_SELECT);
    sceneOfButton.setRoot(readySceneRoot);
  }

  /** method to play create user sound when user is created */
  private void playCreateUserSound() {
    // find sound file
    File createUserSound = new File(App.class.getResource("/sounds/createUser.wav").toString());
    // play file
    MediaPlayer player = new MediaPlayer(new Media(createUserSound.toString().replace("\\", "/")));
    player.play();
  }
}
