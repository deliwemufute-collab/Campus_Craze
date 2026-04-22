package org.example.menu;

import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.MediaPlayer;



/**
 * Handles the start menu screen
 * @author : Victoria Adeosun

 * Accomplishments:
 *  Display title label
 *  Display background placeholder
 *  Add Start button
 *  Attach button event to call startGame()
 *  Hides the menu when play button is  clicked
 */
public class MenuScene {

    private Pane menuPane;

    // UI elements
    private Label titleLabel;
    private Button startButton;
    private Runnable onCredits;
    private MediaPlayer mediaPlayer;


    // Screen dimensions
    private static final double SCREEN_WIDTH  = 1024;
    private static final double SCREEN_HEIGHT = 768;

    // Constructor
    public MenuScene(Pane pane) {
        this.menuPane = pane;

        // Initialize placeholders
        this.titleLabel  = new Label("Campus Craze");
        this.startButton = new Button("Start");
    }

    public void setupMenu() {

        // load and display background image from resources
        Image bgImage = new Image(getClass().getResourceAsStream("/Menu_assets/main_menu_background.jpg"));
        ImageView bgView = new ImageView(bgImage);
        bgView.setFitWidth(SCREEN_WIDTH);
        bgView.setFitHeight(SCREEN_HEIGHT);


        // load play button image from resources
        Image muteImage = new Image(getClass().getResourceAsStream("/Menu_assets/mute_button.png"));
        ImageView muteView = new ImageView(muteImage);
        muteView.setFitWidth(160);
        muteView.setFitHeight(60);
        muteView.setPreserveRatio(false);


        muteView.setLayoutX(SCREEN_WIDTH / 2 - 360);
        muteView.setLayoutY(SCREEN_HEIGHT - 100);
        // toggle music on and off when mute is clicked
        muteView.setOnMouseClicked(e -> {
            if (mediaPlayer != null) {
                if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                    mediaPlayer.pause();
                    muteView.setOpacity(0.5);
                } else {
                    mediaPlayer.play();
                    muteView.setOpacity(1.0);
                }
            }
        });
        muteView.setOnMouseEntered(e -> muteView.setOpacity(0.8));
        muteView.setOnMouseExited(e -> muteView.setOpacity(1.0));

        Image playImage = new Image(getClass().getResourceAsStream("/Menu_assets/play_button.png"));
        ImageView playView = new ImageView(playImage);
        playView.setFitWidth(200);
        playView.setFitHeight(60);
        playView.setLayoutX(SCREEN_WIDTH / 2 - 210);
        playView.setLayoutY(SCREEN_HEIGHT - 100);

        playView.setOnMouseClicked(e -> {
            menuPane.setVisible(false);
            if (startButton.getOnAction() != null) {
                startButton.fire();
            }
        });

        // button gets a little bit transparent when hovered
        playView.setOnMouseEntered(e -> playView.setOpacity(0.8));
        playView.setOnMouseExited(e -> playView.setOpacity(1.0));

        // load exit button
        Image exitImage = new Image(getClass().getResourceAsStream("/Menu_assets/exit_button.png"));
        ImageView exitView = new ImageView(exitImage);
        exitView.setFitWidth(200);
        exitView.setFitHeight(60);
        exitView.setLayoutX(SCREEN_WIDTH / 2 - 10);
        exitView.setLayoutY(SCREEN_HEIGHT - 100);
        exitView.setOnMouseClicked(e -> javafx.application.Platform.exit());
        exitView.setOnMouseEntered(e -> exitView.setOpacity(0.8));
        exitView.setOnMouseExited(e -> exitView.setOpacity(1.0));


        // load credits button image from resources
        Image creditsImage = new Image(getClass().getResourceAsStream("/Menu_assets/credits_button.png"));
        ImageView creditsView = new ImageView(creditsImage);
        creditsView.setFitWidth(200);
        creditsView.setFitHeight(60);
        creditsView.setLayoutX(SCREEN_WIDTH / 2 + 190);
        creditsView.setLayoutY(SCREEN_HEIGHT - 100);
        creditsView.setOnMouseClicked(e -> {
            if (onCredits != null) onCredits.run();
        });

        // button gets a little transparent when hovered
        creditsView.setOnMouseEntered(e -> creditsView.setOpacity(0.8));
        creditsView.setOnMouseExited(e -> creditsView.setOpacity(1.0));

        // background goes first so everything appears on top
        menuPane.getChildren().addAll(bgView, muteView, playView, exitView, creditsView);
    }

    // connects the start button to the startGame action in Main
    public void setStartButtonAction(Runnable action) {
        startButton.setOnAction(e -> {
            menuPane.setVisible(false);
            action.run();
        });
    }

    // lets Main pass in what happens when credits is clicked
    public void setCreditsButtonAction(Runnable action) {
        this.onCredits = action;
    }

    //Setter

    // lets Main pass in the media player so mute button can control it
    public void setMediaPlayer(MediaPlayer player) {
        this.mediaPlayer = player;
    }

    // Getters

    public Pane getMenuPane() {
        return menuPane;
    }

    public Button getStartButton() {
        return startButton;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }
}