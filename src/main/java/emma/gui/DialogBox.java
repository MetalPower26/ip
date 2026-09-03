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
        text.setMaxWidth(300);
        text.setPadding(new Insets(8, 12, 8, 12));
        text.setStyle(style);

        displayPicture = new ImageView(picture);
        displayPicture.setFitWidth(PICTURE_SIZE);
        displayPicture.setFitHeight(PICTURE_SIZE);
        displayPicture.setClip(new Circle(PICTURE_SIZE / 2, PICTURE_SIZE / 2, PICTURE_SIZE / 2));

        this.setSpacing(SPACING);
        this.setPadding(new Insets(6, 10, 6, 10));
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
        return new DialogBox(message, picture,
                "-fx-background-color: #ea580c; -fx-text-fill: white; -fx-background-radius: 12;");
    }

    /**
     * Creates the box for one of Emma's replies, shown on the left.
     *
     * @param message what Emma said.
     * @param picture Emma's picture.
     * @return the dialog box, already turned around.
     */
    public static DialogBox getEmmaDialog(String message, Image picture) {
        DialogBox box = new DialogBox(message, picture,
                "-fx-background-color: #e5e7eb; -fx-text-fill: #111827; -fx-background-radius: 12;");
        box.flip();
        return box;
    }
}
