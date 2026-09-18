package emma.gui;

import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;

/**
 * One line of the conversation: what was said, next to the speaker's picture.
 */
public class DialogBox extends HBox {

    private static final double PICTURE_SIZE = 44;
    private static final double SPACING = 10;
    private static final double MAX_TEXT_WIDTH = 300;
    private static final Insets TEXT_PADDING = new Insets(8, 12, 8, 12);
    private static final Insets BOX_PADDING = new Insets(6, 10, 6, 10);

    /** An orange bubble for the user's own lines. */
    private static final String USER_STYLE =
            "-fx-background-color: #ea580c; -fx-text-fill: white; -fx-background-radius: 12;";
    /** A quiet grey bubble for a reply Emma carried out. */
    private static final String EMMA_STYLE =
            "-fx-background-color: #e5e7eb; -fx-text-fill: #111827; -fx-background-radius: 12;";
    /**
     * A red bubble, outlined and in bold, for a command Emma refused. It differs from an
     * ordinary reply in colour, border and weight at once, so the odd one out is easy to
     * pick out when scrolling back through the conversation.
     */
    private static final String ERROR_STYLE =
            "-fx-background-color: #fee2e2; -fx-text-fill: #991b1b; -fx-background-radius: 12;"
            + " -fx-border-color: #dc2626; -fx-border-radius: 12; -fx-border-width: 2;"
            + " -fx-font-weight: bold;";
    /** Marks an error in the text too, for anyone who cannot tell the colours apart. */
    private static final String ERROR_PREFIX = "⚠ ";

    private final Label text;
    private final ImageView displayPicture;

    /**
     * Creates a dialog box with the speaker's picture on the right, as Emma's own
     * replies are shown mirrored.
     *
     * @param message what was said.
     * @param picture the speaker's picture.
     * @param style the CSS style for the bubble holding the message.
     */
    private DialogBox(String message, Image picture, String style) {
        text = new Label(message);
        text.setWrapText(true);
        text.setMaxWidth(MAX_TEXT_WIDTH);
        text.setPadding(TEXT_PADDING);
        text.setStyle(style);

        displayPicture = new ImageView(picture);
        displayPicture.setFitWidth(PICTURE_SIZE);
        displayPicture.setFitHeight(PICTURE_SIZE);
        displayPicture.setClip(new Circle(PICTURE_SIZE / 2, PICTURE_SIZE / 2, PICTURE_SIZE / 2));

        this.setSpacing(SPACING);
        this.setPadding(BOX_PADDING);
        this.setAlignment(Pos.TOP_RIGHT);
        this.getChildren().addAll(text, displayPicture);
    }

    /** Turns the box around, so the picture is on the left and the text follows it. */
    private void flip() {
        this.setAlignment(Pos.TOP_LEFT);
        ObservableList<Node> children = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(children);
        this.getChildren().setAll(children);
    }

    /**
     * Creates the box for something the user typed, shown on the right.
     *
     * @param message what the user typed.
     * @param picture the user's picture.
     * @return the dialog box.
     */
    public static DialogBox getUserDialog(String message, Image picture) {
        return new DialogBox(message, picture, USER_STYLE);
    }

    /**
     * Creates the box for one of Emma's replies, shown on the left.
     *
     * @param message what Emma said.
     * @param picture Emma's picture.
     * @return the dialog box, already turned around.
     */
    public static DialogBox getEmmaDialog(String message, Image picture) {
        DialogBox box = new DialogBox(message, picture, EMMA_STYLE);
        box.flip();
        return box;
    }

    /**
     * Creates the box for a command Emma could not carry out, shown on the left like any
     * other reply but styled to stand out.
     *
     * @param message what Emma said about the problem.
     * @param picture Emma's picture.
     * @return the dialog box, already turned around.
     */
    public static DialogBox getEmmaErrorDialog(String message, Image picture) {
        DialogBox box = new DialogBox(ERROR_PREFIX + message, picture, ERROR_STYLE);
        box.flip();
        return box;
    }
}
