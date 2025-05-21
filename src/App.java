import gui.AppLauncher;

/**
 * Main class to launch the application.
 * @author Ugnius Vaičeskas
 */
public class App {
  public static void main(String[] args) throws Exception {

    //SwingUtilities.invokeLater(new MainGui());
    AppLauncher.launchGUI(args);
  }
}
