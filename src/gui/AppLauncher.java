package gui;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class to launch the GUI application.
 * Only {@link #launchGUI} method should be used to start the application.
 */
public class AppLauncher extends Application {

  /**
   * Main method to launch the application.
   * @param args Command line arguments.
   */
  public static void launchGUI(String[] args) {
    launch(args);
  }

  @Override
  public void start(Stage stage) {
    LibraryApp libraryApp = new LibraryApp(stage);
    libraryApp.start();
  }
}