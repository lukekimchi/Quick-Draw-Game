package nz.ac.auckland.se206;

import com.opencsv.exceptions.CsvException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map.Entry;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import nz.ac.auckland.se206.controllers.CanvasController;
import nz.ac.auckland.se206.controllers.SceneManager;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;
import nz.ac.auckland.se206.controllers.TitleController;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * This is the entry point of the JavaFX application, while you can change this class, it should
 * remain as the class that runs the JavaFX application.
 */
public class App extends Application {

  private static File bgm;
  private static MediaPlayer bgmPlayer;
  private static File buttonClick;
  private static File errorSound;
  private static File deleteSound;
  private static File winSound;
  private static File lossSound;
  private static boolean isBGMPlaying;

  public static void main(final String[] args) {
    launch();
  }

  /**
   * Returns the node associated to the input file And adds the controller associated with the scene
   * to scene manager map. The method expects that the file is located in "src/main/resources/fxml".
   *
   * @param fxml The name of the FXML file (without extension).
   * @param appUi the ui of scene to be loaded
   * @return The node of the input file.
   * @throws IOException If the file is not found.
   */
  public static Parent loadFxml(final String fxml, AppUi appUi) throws IOException {

    // load the scene requested
    FXMLLoader loader = new FXMLLoader(App.class.getResource("/fxml/" + fxml + ".fxml"));
    Parent root = (Parent) loader.load();

    // save controller of scene in map
    SceneManager.addUiRootController(appUi, loader.getController());

    return root;
  }

  /**
   * method to read user data before loading anything else. Check that user json file exists. Read
   * the json file and extract user array. Loop through json user data and cast them into user
   * instances. Add user instance to UserMap for access later
   *
   * @throws IOException file can't be found
   * @throws ParseException file can't be parsed
   */
  public static void readUsers() throws IOException, ParseException {
    Path profileDirPath = Paths.get("./.profiles");

    // First check if .profiles directory exists
    if (!Files.exists(profileDirPath)) {
      Files.createDirectory(profileDirPath);
    }

    Path usersFilePath = Paths.get("./.profiles/users.json");

    // check if json file exists
    if (Files.exists(usersFilePath)) {
      File usersFile = new File("./.profiles/users.json");

      if (usersFile.length() != 0) {
        FileReader reader = new FileReader("./.profiles/users.json");
        JSONParser parser = new JSONParser();

        // read json file
        Object object = parser.parse(reader);
        JSONArray userArray = (JSONArray) object;

        // extract all data from each user in the json file
        for (int i = 0; i < userArray.size(); i++) {

          JSONObject jsonUser = (JSONObject) userArray.get(i);

          String username = (String) jsonUser.get("username");

          // long values need to be cast to int
          Long lossesLong = (long) jsonUser.get("losses");
          int losses = lossesLong.intValue();

          Long winsLong = (long) jsonUser.get("wins");
          int wins = winsLong.intValue();

          Long fastestTimeLong = (long) jsonUser.get("fastest-time");
          int fastestTime = fastestTimeLong.intValue();

          ArrayList<String> history = (ArrayList<String>) jsonUser.get("history");
          ArrayList<String> lastDiff = (ArrayList<String>) jsonUser.get("lastDiff");
          ArrayList<Boolean> achievements = (ArrayList<Boolean>) jsonUser.get("achievements");
          ArrayList<Boolean> categoryCompletion =
              (ArrayList<Boolean>) jsonUser.get("categoryCompletion");

          // Create a new User instance with all the data extracted from json file
          // associated to that user
          User user =
              new User(
                  username,
                  wins,
                  losses,
                  history,
                  fastestTime,
                  lastDiff,
                  achievements,
                  categoryCompletion);
          UserManager.addUser(user);
          reader.close();
        }
      }

    } else {
      // create users json file
      Files.createFile(usersFilePath);
    }
  }

  /**
   * method to save user data to a json file. Read all users from UserMap and make a json object out
   * of them. add all user json objects into a single json array. Delete previous user jason file
   * and write a new one, to prevent writing to same file
   *
   * @throws IOException file can't be found
   */
  public static void saveData() throws IOException {

    JSONArray jsonUserArray = new JSONArray();

    // cycle through users
    for (Entry<Integer, User> user : UserManager.getAllUsers()) {
      JSONObject jsonUser = new JSONObject();
      User thisUser = user.getValue();

      // turn user fields into json items
      jsonUser.put("username", thisUser.getUsername());
      jsonUser.put("wins", thisUser.getWins());
      jsonUser.put("losses", thisUser.getLosses());
      jsonUser.put("history", thisUser.getHistory());
      jsonUser.put("fastest-time", thisUser.getFastestTime());
      jsonUser.put("lastDiff", thisUser.getLastDifficulty());
      jsonUser.put("achievements", thisUser.getAchievements());
      jsonUser.put("categoryCompletion", thisUser.getCategoryCompletion());

      // add json user into array of users
      jsonUserArray.add(jsonUser);
    }

    // want to overwrite old user info
    File fileOld = new File("./.profiles/users.json");
    fileOld.delete();

    // write to file and close to save data
    FileWriter file = new FileWriter("./.profiles/users.json", true);
    file.write(jsonUserArray.toString());
    file.close();
  }

  /**
   * This method is invoked when the application starts. It loads and shows the "Canvas" scene.
   *
   * @param stage The primary stage of the application.
   * @throws IOException If "src/main/resources/fxml/canvas.fxml" is not found.
   * @throws URISyntaxException uri file problem
   * @throws CsvException csv problem
   * @throws ParseException file can't be parsed
   */
  @Override
  public void start(final Stage stage)
      throws IOException, CsvException, URISyntaxException, ParseException {

    // Cast all user data to User Map
    readUsers();

    // Create scenes
    SceneManager.addUi(AppUi.TITLE, loadFxml("title", AppUi.TITLE));
    SceneManager.addUi(AppUi.CANVAS, loadFxml("canvas", AppUi.CANVAS));
    SceneManager.addUi(AppUi.USER_SELECT, loadFxml("userselect", AppUi.USER_SELECT));
    SceneManager.addUi(AppUi.MAIN_MENU, App.loadFxml("mainmenu", AppUi.MAIN_MENU));
    SceneManager.addUi(
        AppUi.DIFFICULTY_SELECT, App.loadFxml("difficultyselection", AppUi.DIFFICULTY_SELECT));
    SceneManager.addUi(AppUi.STATS, App.loadFxml("stats", AppUi.STATS));
    SceneManager.addUi(AppUi.USER_CREATE, App.loadFxml("usercreate", AppUi.USER_CREATE));
    SceneManager.addUi(AppUi.MODE_SELECT, App.loadFxml("modeselect", AppUi.MODE_SELECT));

    SceneManager.addUi(AppUi.ZEN_MODE, App.loadFxml("zenmode", AppUi.ZEN_MODE));
    SceneManager.addUi(AppUi.DISPLAY_BADGES, App.loadFxml("displaybadges", AppUi.DISPLAY_BADGES));

    // Load main menu scene
    Scene scene = new Scene(SceneManager.getUiRoot(AppUi.TITLE), 640, 480);
    TitleController titleController =
        (TitleController) SceneManager.getUiRootController(AppUi.TITLE);
    titleController.run();
    stage.setScene(scene);
    stage.show();

    // Exit program when user closes window
    stage.setOnCloseRequest(
        e -> {
          try {
            CanvasController controller =
                (CanvasController) SceneManager.getUiRootController(AppUi.CANVAS);
            SketchGame game = controller.getSketchGame();
            // if game is not over then consider it lost
            if (!game.getIsGameOver()) {
              game.lose();
            }
            // save data before quitting
            saveData();
          } catch (IOException e1) {
            e1.printStackTrace();
          }
          Platform.exit();
          System.exit(0);
        });

    // Load all common sound effect files
    buttonClick = new File(App.class.getResource("/sounds/buttonClick.wav").toString());
    errorSound = new File(App.class.getResource("/sounds/error.wav").toString());
    deleteSound = new File(App.class.getResource("/sounds/deleteUser.wav").toString());
    winSound = new File(App.class.getResource("/sounds/winSound.wav").toString());
    lossSound = new File(App.class.getResource("/sounds/lossSound.wav").toString());
  }

  /**
   * method to play back ground music. set boolean value to true to prevent multiple instances of
   * music playing at once
   */
  public static void playBGM() {
    isBGMPlaying = true;
    // get music file
    bgm = new File(App.class.getResource("/sounds/bgm.mp3").toString());
    // play music file indefinitely
    bgmPlayer = new MediaPlayer(new Media(bgm.toString().replace("\\", "/")));
    bgmPlayer.setCycleCount(MediaPlayer.INDEFINITE);
    bgmPlayer.setVolume(0.6);
    bgmPlayer.play();
  }

  /** method used to stop music from playing */
  public static void stopBGM() {
    // set music status to not playing
    isBGMPlaying = false;
    bgmPlayer.stop();
  }

  /**
   * method that returns status of music, playing(true) or not playing(false)
   *
   * @return
   */
  public static boolean getIsBGMPlaying() {
    return isBGMPlaying;
  }

  /** method to play button click sound */
  public static void playButtonClick() {
    // get sound file
    MediaPlayer player = new MediaPlayer(new Media(buttonClick.toString().replace("\\", "/")));
    player.play();
  }

  /** method to play error sound */
  public static void playErrorSound() {
    // get sound file
    MediaPlayer player = new MediaPlayer(new Media(errorSound.toString().replace("\\", "/")));
    player.play();
  }

  /** method to play delete sound */
  public static void playDeleteSound() {
    // get sound file
    MediaPlayer player = new MediaPlayer(new Media(deleteSound.toString().replace("\\", "/")));
    player.play();
  }

  /** method to play victory sound */
  public static void playWinSound() {
    // get sound file
    MediaPlayer player = new MediaPlayer(new Media(winSound.toString().replace("\\", "/")));
    player.play();
  }

  /** method to play loss/defeat sound */
  public static void playLossSound() {
    // get sound file
    MediaPlayer player = new MediaPlayer(new Media(lossSound.toString().replace("\\", "/")));
    player.play();
  }
}
