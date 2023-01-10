package nz.ac.auckland.se206.controllers;

import java.util.HashMap;
import javafx.scene.Parent;

public class SceneManager {

  public enum AppUi {
    MAIN_MENU,
    CANVAS,
    READY,
    DIFFICULTY_SELECT,
    TITLE,
    USER_SELECT,
    STATS,
    USER_CREATE,
    MODE_SELECT,
    ZEN_MODE,

    DISPLAY_BADGES
  }

  // scene map
  private static HashMap<AppUi, Parent> sceneMap = new HashMap<AppUi, Parent>();

  // controller map
  private static HashMap<AppUi, Controller> controllerMap = new HashMap<AppUi, Controller>();

  public static void addUi(AppUi appUi, Parent uiRoot) {
    sceneMap.put(appUi, uiRoot);
  }

  public static Parent getUiRoot(AppUi appUi) {
    return sceneMap.get(appUi);
  }

  public static void addUiRootController(AppUi appUi, Controller uiRoot) {
    controllerMap.put(appUi, uiRoot);
  }

  public static Controller getUiRootController(AppUi appUi) {
    return controllerMap.get(appUi);
  }
}
