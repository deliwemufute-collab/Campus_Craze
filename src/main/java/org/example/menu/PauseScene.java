package org.example.menu;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * Displays the Pause screen when the player pauses the game.
 * Shows a dark overlay with a resume button.
 *
 * @author Victoria Adeosun
 */
public class PauseScene {

    private final Pane pausePane;
    private Runnable onResume;

    // set up the pause screen
    public PauseScene(Pane pausePane) {
        this.pausePane = pausePane;
        setupPause();
    }

    // build the pause screen elements
    private void setupPause() {

        // dark background overlay
        javafx.scene.shape.Rectangle background = new javafx.scene.shape.Rectangle(1024, 768);
        background.setFill(Color.rgb(0, 0, 0, 0.75));

        // paused title
        Label title = new Label("PAUSED");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        title.setTextFill(Color.WHITE);
        title.setLayoutX(0);
        title.setPrefWidth(1024);
        title.setAlignment(javafx.geometry.Pos.CENTER);
        title.setLayoutY(250);

        // resume button
        Image resumeImage = new Image(getClass().getResourceAsStream("/Menu_assets/resume_button.png"));

        ImageView resumeBtn = new ImageView(resumeImage);
        resumeBtn.setFitWidth(450);
        resumeBtn.setFitHeight(220);
        resumeBtn.setPreserveRatio(true);
        resumeBtn.setLayoutX(420);
        resumeBtn.setLayoutY(310);

        // resume the game when clicked
        resumeBtn.setOnMouseClicked(e -> {
            e.consume();
            if (onResume != null) onResume.run();
        });

        // hover effect
        resumeBtn.setOnMouseEntered(e -> resumeBtn.setOpacity(0.8));
        resumeBtn.setOnMouseExited(e -> resumeBtn.setOpacity(1.0));

        pausePane.getChildren().addAll(background, title, resumeBtn);
        pausePane.setStyle("-fx-background-color: transparent;");
    }

    // lets GameScene pass in what happens when resume is clicked
    public void setOnResume(Runnable onResume) {
        this.onResume = onResume;
    }

    // returns the pane so it can be added to root
    public Pane getPausePane() {
        return pausePane;
    }
}