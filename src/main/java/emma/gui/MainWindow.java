package emma.gui;

import emma.Emma;
import emma.Ui;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * The window the user talks to Emma in: a scrolling conversation above a text box
 * and a send button.
 */
public class MainWindow extends AnchorPane {

    private static final double WIDTH = 420;
    private static final double HEIGHT = 620;
    private static final double INPUT_HEIGHT = 40;
    private static final Duration CLOSE_DELAY = Duration.seconds(1);

    private final ScrollPane scrollPane = new ScrollPane();
    private final VBox dialogContainer = new VBox();
    private final TextField userInput = new TextField();
    private final Button sendButton = new Button("Send");

    private final Emma emma;
    private final Image userImage;
    private final Image emmaImage;

    /**
     * Builds the window and shows Emma's greeting.
     *
     * @param emma the chatbot the window talks to.
     */
    public MainWindow(Emma emma) {
        this.emma = emma;
        this.userImage = loadImage("/images/DaUser.png");
        this.emmaImage = loadImage("/images/DaEmma.png");

        layOutControls();
        wireUpInput();

        String loadMessage = emma.loadTasks();
        if (!loadMessage.isEmpty()) {
            addEmmaMessage(loadMessage);
        }
        addEmmaMessage(Ui.getGreeting());
    }

    /** Arranges the scrolling conversation above the text box and the send button. */
    private void layOutControls() {
        this.setPrefSize(WIDTH, HEIGHT);
        this.setStyle("-fx-background-color: white;");

        dialogContainer.setFillWidth(true);
        dialogContainer.setPadding(new Insets(6));
        scrollPane.setContent(dialogContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        // Keep the newest message in view as the conversation grows.
        dialogContainer.heightProperty().addListener(observable -> scrollPane.setVvalue(1.0));

        userInput.setPromptText("Type a command, then press Enter");
        userInput.setPrefHeight(INPUT_HEIGHT);
        sendButton.setPrefSize(70, INPUT_HEIGHT);

        AnchorPane.setTopAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setBottomAnchor(scrollPane, INPUT_HEIGHT);

        AnchorPane.setBottomAnchor(userInput, 0.0);
        AnchorPane.setLeftAnchor(userInput, 0.0);
        AnchorPane.setRightAnchor(userInput, sendButton.getPrefWidth());

        AnchorPane.setBottomAnchor(sendButton, 0.0);
        AnchorPane.setRightAnchor(sendButton, 0.0);

        this.getChildren().addAll(scrollPane, userInput, sendButton);
    }

    /** Sends the typed line when the button is pressed or Enter is hit. */
    private void wireUpInput() {
        sendButton.setOnAction(event -> handleUserInput());
        userInput.setOnAction(event -> handleUserInput());
    }

    /** Shows what the user typed, then Emma's reply, and closes the window after "bye". */
    private void handleUserInput() {
        String input = userInput.getText();
        if (input.isBlank()) {
            return;
        }
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));
        addEmmaMessage(emma.getResponse(input));
        userInput.clear();

        if (emma.isExit()) {
            userInput.setDisable(true);
            sendButton.setDisable(true);
            PauseTransition wait = new PauseTransition(CLOSE_DELAY);
            wait.setOnFinished(event -> Platform.exit());
            wait.play();
        }
    }

    /**
     * Adds one of Emma's messages to the conversation.
     *
     * @param message what Emma has to say.
     */
    private void addEmmaMessage(String message) {
        dialogContainer.getChildren().add(DialogBox.getEmmaDialog(message, emmaImage));
    }

    /**
     * Reads a picture that is packaged with the program.
     *
     * @param resourcePath the path of the image inside the jar.
     * @return the picture.
     */
    private Image loadImage(String resourcePath) {
        return new Image(MainWindow.class.getResourceAsStream(resourcePath));
    }

    /**
     * Returns how wide the window should open.
     *
     * @return the preferred width.
     */
    public static double getWindowWidth() {
        return WIDTH;
    }

    /**
     * Returns how tall the window should open.
     *
     * @return the preferred height.
     */
    public static double getWindowHeight() {
        return HEIGHT;
    }
}
