package emma.gui;

import emma.Emma;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Runs Emma as a window instead of a console conversation.
 */
public class Main extends Application {

    private static final String SAVE_PATH = "data/emma.json";

    /**
     * Opens the window and hands it a chatbot to talk to.
     *
     * @param stage the window JavaFX provides.
     */
    @Override
    public void start(Stage stage) {
        MainWindow root = new MainWindow(new Emma(SAVE_PATH));
        stage.setScene(new Scene(root, MainWindow.getWindowWidth(), MainWindow.getWindowHeight()));
        stage.setTitle("Emma");
        stage.setMinWidth(320);
        stage.setMinHeight(400);
        stage.show();
    }
}
