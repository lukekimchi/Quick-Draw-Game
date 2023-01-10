package nz.ac.auckland.se206.controllers;

import ai.djl.ModelException;
import ai.djl.modality.Classifications.Classification;
import ai.djl.translate.TranslateException;
import com.opencsv.exceptions.CsvException;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SketchGame;
import nz.ac.auckland.se206.SketchGame.GameType;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;
import nz.ac.auckland.se206.ml.DoodlePrediction;
import nz.ac.auckland.se206.speech.TextToSpeech;

public class CanvasController implements Controller {

  /* This class contains the runnable task required to make predictions. */
  public class PredictingInBackground implements Runnable {

    @Override
    public void run() {

      // initialize an empty arraylist
      predictionStatements = new ArrayList<String>();

      try {
        // get top 100 predictions from DL model
        predictionResults = model.getPredictions(getCurrentSnapshot(), 100);

        // for each prediction, save them in an arraylist with a particular format as a
        // string. Interested in top 10 predictions only.
        if (!isEmptyCanvas) {
          for (int i = 0; i < 10; i++) {

            Classification prediction = predictionResults.get(i);

            // replace all underscores with whitespace
            String predictionStatement =
                ((i + 1)
                    + ". "
                    + prediction.getClassName().replace("_", " ")
                    + ": "
                    + String.format("%.0f%%", 100 * prediction.getProbability()));

            // add predictions to arraylist
            predictionStatements.add(predictionStatement);
          }
        } else {
          // clear statements to erase predictions
          predictionStatements.clear();
        }

      } catch (TranslateException e) {
        e.printStackTrace();
      }

      // The current top 10 predictions should be cleared and replaced with the next
      // 10 predictions made a second after
      Platform.runLater(
          () -> {
            fpPredict.getChildren().clear();
            fpPredict.setOrientation(Orientation.VERTICAL);
            int listViewCell = 0;
            // display predictions as a flow plane
            for (String predictionStatement : predictionStatements) {
              Label lblPredict = new Label(predictionStatement);
              fpPredict.getChildren().add(lblPredict);
              if (listViewCell < game.getAccuracy()) {
                lblPredict.setStyle("-fx-background-color:limegreen");
              }
              listViewCell++;
            }
          });
    }
  }

  /*
   * This class contains the timer task required to count down from time limit.
   */
  public class Countdown extends TimerTask {

    @Override
    public void run() {
      game.decreaseTime();

      // change the appearance of timer according to the time left
      Platform.runLater(
          () -> {
            DecimalFormat fmt = new DecimalFormat("00");
            // update timer label
            lblTimer.setText(fmt.format(game.getTime()));
            // cancel task if time is up
            if (isTimeUp()) {
              cancel();
            }
          });
    }
  }

  /*
   * This class contains a timer task which repeatedly checks if game should be
   * terminated. Either because time limit has been reached OR if the DL model has
   * correctly predicted the chosen category in its top predictions.
   */
  public class GameOver extends TimerTask {
    @Override
    public void run() {

      if (game.getTime() == 0 || isCategoryPredicted(predictionResults) || game.getIsGameOver()) {

        // if game is exited by quit button
        if (game.getIsGameOver()) {
          cancel();
        }
        // Player has lost if time limit reached, but if there is time left that means
        // they won
        else {
          if (game.getTime() == 0) {
            outcome = "Oops, Time's Up!";
            game.lose();
          } else {
            outcome = "You won! Way to Go!";
            game.win();
          }
          try {
            switchToGameOver();

            // Speech says "You won! Way to Go!" if DL model predicts category in its top
            // three or
            // says "Oops, Time's Up!" if one minute has passed
            tts.speak(outcome);

          } catch (IOException e) {
            e.printStackTrace();
          }
          // Whether time is up or prediction successfully made, cancel this task
          cancel();
        }
      }
    }
  }

  /**
   * task executed when speakCategory button is pressed to speak category. After speaking enable the
   * speakCategory button again. If game type is Hidden word then speak definition instead
   *
   * @author serge
   */
  public class CategoryTextToSpeech implements Runnable {

    @Override
    public void run() {

      // switch speech depending on game type
      if (game.getGameType().equals(GameType.NORMAL)) {
        tts.speak(game.getCategory());
      } else if (game.getGameType().equals(GameType.HIDDEN_WORD)) {
        tts.speak(game.getDefinition());
      }
      Platform.runLater(
          () -> {
            // enable button after tts is complete
            btnSpeakCategory.setDisable(false);
          });
    }
  }

  @FXML private Canvas canvas;

  @FXML private Label lblWord;

  @FXML private Button btnSpeakCategory;

  @FXML private Label lblTimer;

  @FXML private Button btnEraser;

  @FXML private Button btnPen;

  @FXML private Button btnClear;

  @FXML private Button btnPlayAgain;

  @FXML private Button btnSaveDrawing;

  @FXML private FlowPane fpPredict;

  @FXML private Button btnQuit;

  @FXML private ImageView imgQuickDrawTitle;

  @FXML private Label lblWinOrLose;

  @FXML private VBox vboxOutcome;

  @FXML private HBox hboxArtButtons;

  @FXML private HBox hboxGameOverButtons;

  @FXML private ImageView emoji;

  @FXML private ImageView emoji1;

  @FXML private Slider sliderSize;

  private List<String> predictionStatements;

  private List<Classification> predictionResults;

  private GraphicsContext graphic;

  private DoodlePrediction model;

  private SketchGame game;

  private boolean isEmptyCanvas;

  private String outcome;

  private TextToSpeech tts;

  // mouse coordinates
  private double currentX;
  private double currentY;

  private double brushSize;

  private Timer timer;
  private Countdown timerTask;

  private boolean isRunning;

  /**
   * JavaFX calls this method once the GUI elements are loaded. In our case we create a listener for
   * the drawing, and we load the ML model. Create game instance.
   *
   * @throws ModelException If there is an error in reading the input/output of the DL model.
   * @throws IOException If the model cannot be found on the file system.
   * @throws URISyntaxException if csv uri is wrong
   * @throws CsvException if csv encounters a problem
   * @throws InterruptedException something interrupted?
   */
  public void initialize()
      throws ModelException, IOException, CsvException, URISyntaxException, InterruptedException {
    // initialise game
    game = new SketchGame();

    graphic = canvas.getGraphicsContext2D();
    graphic.setFill(Color.WHITE);
    // set canvas status to empty
    isEmptyCanvas = true;

    // set brush size
    brushSize = 6;
    onSetPen();

    // initialise prediction model
    model = new DoodlePrediction();

    // Text to Speech thread
    tts = new TextToSpeech();
  }

  /** This sequence is to be run when user starts drawing. Starts all timers and background tasks */
  public void run() {

    // set status to running so this method won't be triggered again
    isRunning = true;

    // HANDLE PREDICTING TOP 10 PREDICTIONS IN THE BACKGROUND
    PredictingInBackground predictingTask = new PredictingInBackground();

    // HANDLE TIMER COUNTDOWN IN THE BACKGROUND
    timer = new Timer();

    // initialize countdown task
    timerTask = new Countdown();

    // assign countdown task to timer thread created
    timer.scheduleAtFixedRate(timerTask, 1000, 1000);

    // HANDLE CHECKING IF GAME IS OVER
    Timer checkIfGameOver = new Timer();

    // initialize 'checking if game over' task
    GameOver gameOver = new GameOver();

    // assign gameOver task to timer thread created
    checkIfGameOver.scheduleAtFixedRate(gameOver, 1050, 1000);

    // predicting should happen every second
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleAtFixedRate(
        () -> {
          if (game.getIsGameOver()) {
            timer.cancel();
            executor.shutdown();
          }
          if (!executor.isShutdown()) {
            // predict drawing if game is not over
            Platform.runLater(predictingTask);
          }
        },
        950,
        1000,
        TimeUnit.MILLISECONDS);
  }

  /**
   * This method changes some GUI elements once game is over.
   *
   * @throws IOException file can't be found
   */
  private void switchToGameOver() throws IOException {
    // stop music
    App.stopBGM();
    Platform.runLater(
        () -> {

          // Changing the visibility of the following buttons like this makes it look like
          // the new buttons ("Play Again" and "Save Drawing") are replacing the old
          // ("Eraser" and "Clear").
          btnPlayAgain.setVisible(true);
          btnSaveDrawing.setVisible(true);
          btnEraser.setVisible(false);
          btnPen.setVisible(false);
          btnClear.setVisible(false);

          // set category to visible
          lblWord.setVisible(true);

          btnQuit.setDisable(true);
          canvas.setDisable(true);

          imgQuickDrawTitle.setVisible(false);
          vboxOutcome.setVisible(true);
          vboxOutcome.toFront();

          hboxGameOverButtons.setVisible(true);
          hboxArtButtons.setVisible(false);

          // edit appearance
          if (isTimeUp()) { // if game is lost
            App.playLossSound();
            vboxOutcome.setStyle("-fx-background-color: red");
            lblWinOrLose.setStyle("-fx-background-image: url(/images/loseMessage.png)");
            lblWinOrLose.setTextFill(Color.WHITE);

            emoji1.setImage(
                new Image(App.class.getResource("/images/emojis/41to70.png").toString()));

            System.out.println("you lost");

          } else { // game is won
            App.playWinSound();
            vboxOutcome.setStyle("-fx-background-color: greenyellow");
            lblWinOrLose.setStyle("-fx-background-image: url(/images/winMessage.png)");

            emoji1.setImage(
                new Image(App.class.getResource("/images/emojis/winEmoji.png").toString()));

            System.out.println("you won");
          }
        });
  }

  /**
   * This method returns true if time ran out, false if not.
   *
   * @return boolean value
   */
  private boolean isTimeUp() {
    return (game.getTime() == 0);
  }

  /**
   * This method determines whether the top accuracy of 100 predictions made contains the chosen
   * category.
   *
   * @param predictionStatements the list of 100 predictions made
   * @return boolean value, true if chosen category is in the top accurate predictions
   */
  private boolean isCategoryPredicted(List<Classification> predictionResults) {

    if (predictionResults == null) {
      return false;
    }
    int i = findCategory(predictionResults);
    // if prediction was not found in top 100
    if (i == -1) {
      Image em = new Image(App.class.getResource("/images/emojis/over100.png").toString());
      emoji.setImage(em);
      return false;
    }

    // Segregating based on ranges
    if (i < game.getAccuracy()) {
      String formatConf = String.format("%.0f", 100 * predictionResults.get(i).getProbability());
      int confidence = Integer.valueOf(formatConf);
      // compare confidence with winning confidence
      if (confidence >= game.getConfidence()) {
        Image winEmoji = new Image(App.class.getResource("/images/emojis/winEmoji.png").toString());
        emoji.setImage(winEmoji);

        return true;
      }
    } else if (i < 20) {
      // Implement changing to 'quite happy avatar'
      Image em = new Image(App.class.getResource("/images/emojis/11to20.png").toString());
      emoji.setImage(em);
    } else if (i < 40) {
      // Implement changing to 'mid avatar'
      Image em = new Image(App.class.getResource("/images/emojis/21to40.png").toString());
      emoji.setImage(em);
    } else if (i < 70) {
      // Implement changing to 'bit confused avatar'
      Image em = new Image(App.class.getResource("/images/emojis/41to70.png").toString());
      emoji.setImage(em);
    } else {
      // Implement changing to 'very confused avatar'
      Image em = new Image(App.class.getResource("/images/emojis/71to100.png").toString());
      emoji.setImage(em);
    }

    return false;
  }

  /**
   * method used to find category in prediction results. Return index position of category
   *
   * @param predictionResults List of top 100 predicted classifications
   * @return i index of category among the predictionResults
   */
  private int findCategory(List<Classification> predictionResults) {
    int i = 0;
    String category = game.getCategory();

    // check classifications for category
    for (Classification prediction : predictionResults) {
      // need to format classification like a category
      if (prediction.getClassName().replace("_", " ").equals(category)) {
        return i;
      }
      i++;
    }
    // return -1 index if category is not found
    return -1;
  }

  /**
   * method executed when speakCategory button is pressed. Create new thread and run speakCategory
   * task. disable the button so that there is no chance of looping tts
   */
  @FXML
  private void onSpeakCategory() {
    // disable button to prevent overlap
    btnSpeakCategory.setDisable(true);

    // create and execute new thread
    CategoryTextToSpeech task = new CategoryTextToSpeech();
    Thread ttsCategory = new Thread(task);
    ttsCategory.start();
  }

  /** method executed when slider is moved to change size */
  @FXML
  private void onChangeSize() {
    // when slider is released save its value
    sliderSize.setOnMouseReleased(
        event -> {
          brushSize = sliderSize.getValue();
        });
  }

  /** This method sets drawing tool to pen */
  @FXML
  private void onSetPen() {
    if (isRunning) {
      App.playButtonClick();
    }

    canvas.setOnMousePressed(
        e -> {
          currentX = e.getX();
          currentY = e.getY();
        });

    canvas.setOnMouseDragged(
        e -> {
          if (!isRunning) {
            run();
          }
          // Brush size (you can change this, it should not be too small or too large).
          final double size = brushSize;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.setFill(Color.BLACK);
          graphic.setLineWidth(size);
          isEmptyCanvas = false;

          // Create a line that goes from the point (currentX, currentY) and (x,y)
          graphic.strokeLine(currentX, currentY, x, y);

          // update the coordinates
          currentX = x;
          currentY = y;
        });
  }

  /** method executed when clear button is pressed. clears the canvas, switches tool to pen */
  @FXML
  private void onClear() {
    // if game is running
    if (isRunning) {
      App.playDeleteSound();
    }
    graphic.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

    // set tool to pen
    onSetPen();
    // set canvas status to empty
    isEmptyCanvas = true;
  }

  /** this method sets current tool to eraser */
  @FXML
  private void onSetEraser() {
    // play button click sound
    App.playButtonClick();
    canvas.setOnMouseDragged(
        e -> {
          // Brush size (you can change this, it should not be too small or too large).
          final double size = brushSize;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          graphic.clearRect(x, y, size, size);
        });
  }

  /**
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  private BufferedImage getCurrentSnapshot() {
    final Image snapshot = canvas.snapshot(null, null);
    final BufferedImage image = SwingFXUtils.fromFXImage(snapshot, null);

    // Convert into a binary image.
    final BufferedImage imageBinary =
        new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

    final Graphics2D graphics = imageBinary.createGraphics();

    graphics.drawImage(image, 0, 0, null);

    // To release memory we dispose.
    graphics.dispose();

    return imageBinary;
  }

  /** Method used to reset controller state, including canvas, buttons, labels */
  protected void resetScene() {

    // set execution status to not running
    isRunning = false;

    vboxOutcome.setVisible(false);
    imgQuickDrawTitle.setVisible(true);
    hboxGameOverButtons.setVisible(false);
    hboxArtButtons.setVisible(true);

    // reset emoji
    Image em = new Image(App.class.getResource("/images/emojis/over100.png").toString());
    emoji.setImage(em);

    // reset canvas
    onClear();
    canvas.setDisable(false);

    // reset brush size
    sliderSize.setValue(6);
    brushSize = 6;

    // update button visibility
    btnQuit.setDisable(false);

    btnPlayAgain.setVisible(false);
    btnSaveDrawing.setVisible(false);

    btnPen.setVisible(true);
    btnEraser.setVisible(true);
    btnClear.setVisible(true);

    if (game.getGameType().equals(GameType.NORMAL)) {
      // update category above canvas
      lblWord.setText(game.getCategory());
      lblWord.setVisible(true);
    } else if (game.getGameType().equals(GameType.HIDDEN_WORD)) {
      // update category label with definition
      lblWord.setText(game.getCategory());
      lblWord.setVisible(false);
      System.out.println(game.getCategory());
    }
    // update timer
    DecimalFormat fmt = new DecimalFormat("00");
    lblTimer.setText(fmt.format(game.getTime()));

    // reset predictions
    fpPredict.getChildren().clear();
  }

  /**
   * method executed when the play again button is pressed. moves to main menu
   *
   * @param event click event
   * @throws IOException data can't be saved
   */
  @FXML
  private void onPlayAgain(ActionEvent event) throws IOException {
    // play a sound
    playPlayAgainSound();

    Button buttonClicked = (Button) event.getSource();
    Scene sceneOfButtonClicked = buttonClicked.getScene();

    // Switch to mainmenu scene
    Parent mainMenuRoot = SceneManager.getUiRoot(AppUi.MAIN_MENU);
    sceneOfButtonClicked.setRoot(mainMenuRoot);

    App.saveData();
  }

  /**
   * method executed when save drawing button is pressed. Save drawing in location specified by user
   *
   * @throws IOException image can't be saved
   */
  @FXML
  private void onSaveDrawing() throws IOException {
    // play button click sound
    App.playButtonClick();
    Window stage = canvas.getScene().getWindow();

    FileChooser fileChooser = new FileChooser();

    // select save file location and name
    fileChooser.getExtensionFilters().addAll();
    fileChooser.setInitialFileName(game.getCategory() + ".bmp");
    File file = fileChooser.showSaveDialog(stage);

    // if file is valid then save
    if (file == null) {
      System.out.println("No file was selected");
    } else {
      ImageIO.write(getCurrentSnapshot(), "png", file); // save the drawing to the chosen location
    }
  }

  /**
   * leave scene and move to main menu. set game to be over to stop predictions
   *
   * @param event click event
   * @throws IOException data can't be saved
   */
  @FXML
  private void onQuit(ActionEvent event) throws IOException {
    // play button click sound
    App.playButtonClick();
    game.lose();
    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();

    // switch to mainmenu scene
    Parent readySceneRoot = SceneManager.getUiRoot(AppUi.MAIN_MENU);
    sceneOfButton.setRoot(readySceneRoot);
    // if timer is initiated then cancel it
    if (timerTask != null) {
      timerTask.cancel();
    }

    App.saveData();
  }

  /** method used to play the play again sound when clicking on play again button */
  private void playPlayAgainSound() {
    // find sound file
    File startGameSound = new File(App.class.getResource("/sounds/startGame.wav").toString());
    // play sound file
    MediaPlayer player = new MediaPlayer(new Media(startGameSound.toString().replace("\\", "/")));
    player.play();
  }

  /**
   * method to return game instance so we can share instance across scenes
   *
   * @return sketchGame instance
   */
  public SketchGame getSketchGame() {
    return game;
  }
}
