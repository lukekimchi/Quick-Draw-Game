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
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javax.imageio.ImageIO;
import nz.ac.auckland.se206.App;
import nz.ac.auckland.se206.SketchGame;
import nz.ac.auckland.se206.controllers.SceneManager.AppUi;
import nz.ac.auckland.se206.ml.DoodlePrediction;

public class ZenModeController implements Controller {

  /**
   * Task used to predict drawn word and display top 10 predictions
   *
   * @author serge
   */
  public class PredictingInBackground implements Runnable {

    @Override
    public void run() {
      isRunning = true;
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
          // clear predictions
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
            // listTenPredictions.getItems().clear();
            int listViewCell = 0;

            for (String predictionStatement : predictionStatements) {
              // listTenPredictions.getItems().add(predictionStatement);
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

  @FXML private Canvas canvas;

  @FXML private Button btnEraser;

  @FXML private Button btnPen;

  @FXML private Button btnClear;

  @FXML private Button btnSaveDrawing;

  @FXML private Button btnQuit;

  @FXML private FlowPane fpPredict;

  @FXML private ColorPicker colourPicker;

  @FXML private Slider sliderSize;

  private List<String> predictionStatements;

  private List<Classification> predictionResults;

  private GraphicsContext graphic;

  private DoodlePrediction model;

  private SketchGame game;

  private boolean isEmptyCanvas;
  private boolean isRunning;
  private Color currentPenColour;

  // mouse coordinates
  private double currentX;
  private double currentY;
  private double brushSize;

  /**
   * Create zen mode controller with a new game instance and a doodle prediction instance. Set
   * current pen colour to black. Set canvas status to empty
   *
   * @throws ModelException model problem
   * @throws IOException file can't be found
   * @throws CsvException csv problem
   * @throws URISyntaxException uri problem
   * @throws InterruptedException interruption problem
   */
  public void initialize()
      throws ModelException, IOException, CsvException, URISyntaxException, InterruptedException {
    // initialise game
    game = new SketchGame();

    graphic = canvas.getGraphicsContext2D();
    graphic.setFill(Color.WHITE);
    // set canvas status to empty
    isEmptyCanvas = true;
    currentPenColour = Color.BLACK;

    // set brush size
    brushSize = 6;
    onSetPen();

    // initialise prediction model
    model = new DoodlePrediction();
  }

  /** method used to start game predictions every second and play music. */
  public void run() {
    // play back ground music
    isRunning = true;

    // keep predicting image word until scene is left
    PredictingInBackground predictingTask = new PredictingInBackground();
    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    executor.scheduleAtFixedRate(
        () -> {
          if (game.getIsGameOver()) {
            // stop if user left scene
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

  /** method executed when slider is moved to change size */
  @FXML
  private void onChangeSize() {
    // when slider is released save its value
    sliderSize.setOnMouseReleased(
        event -> {
          brushSize = sliderSize.getValue();
        });
  }

  /**
   * method used to draw on canvas. Starts the prediction/run sequence when drawing for the first
   * time
   */
  @FXML
  private void onSetPen() {
    if (isRunning) {
      // play button click sound
      App.playButtonClick();
    }
    canvas.setOnMousePressed(
        e -> {
          currentX = e.getX();
          currentY = e.getY();
        });

    canvas.setOnMouseDragged(
        e -> {
          // if first time drawing then start run sequence
          if (!isRunning) {
            run();
          }
          // Brush size (you can change this, it should not be too small or too large).
          final double size = brushSize;

          final double x = e.getX() - size / 2;
          final double y = e.getY() - size / 2;

          // This is the colour of the brush.
          graphic.setStroke(currentPenColour);
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
   * method executed when save drawing button is pressed. Save drawing in location specified by user
   *
   * @throws IOException file can't be found
   */
  @FXML
  private void onSaveDrawing() throws IOException {
    // play button click sound
    App.playButtonClick();

    Window stage = canvas.getScene().getWindow();

    FileChooser fileChooser = new FileChooser();

    // select save file location and name
    fileChooser.getExtensionFilters().addAll();
    fileChooser.setInitialFileName("awesome-drawing.bmp");
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
   * @throws IOException file not found
   */
  @FXML
  private void onQuit(ActionEvent event) throws IOException {
    // play button click sound
    App.playButtonClick();
    // end game
    game.setIsGameOver(true);

    Button button = (Button) event.getSource();
    Scene sceneOfButton = button.getScene();

    // switch to mainmenu scene
    Parent readySceneRoot = SceneManager.getUiRoot(AppUi.MAIN_MENU);
    sceneOfButton.setRoot(readySceneRoot);
  }

  /**
   * method executed when colour wheel is pressed. Change colour of pen to what was selected in
   * colour picker
   */
  @FXML
  private void onChangeColour() {
    // pick colour
    currentPenColour = colourPicker.getValue();
    // set colour
    onSetPen();
  }

  /**
   * Get the current snapshot of the canvas.
   *
   * @return The BufferedImage corresponding to the current canvas content.
   */
  private BufferedImage getCurrentSnapshot() {
    // get canvas snapshot
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

  /**
   * play colour picking sound file when colour picker is interacted with
   *
   * @param event click event
   */
  @FXML
  private void onClickingColourPicker(MouseEvent event) {
    // find sound file
    File sound = new File(App.class.getResource("/sounds/colourPicker.wav").toString());
    // play sound file
    MediaPlayer player = new MediaPlayer(new Media(sound.toString().replace("\\", "/")));
    player.setVolume(0.5);
    player.play();
  }

  /**
   * reset scene. Set pen colour to black, reset game status, set run status to false, clear canvas,
   * clear predictions.
   */
  protected void resetScene() {

    colourPicker.setValue(Color.BLACK);
    currentPenColour = Color.BLACK;

    // reset brush size
    sliderSize.setValue(6);
    brushSize = 6;

    game.setIsGameOver(false);

    // set execution status to not running
    isRunning = false;

    // reset canvas
    onClear();

    // reset predictions
    fpPredict.getChildren().clear();
  }
}
