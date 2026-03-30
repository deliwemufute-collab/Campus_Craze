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
        Image playImage = new Image(getClass().getResourceAsStream("/Menu_assets/play_button.png"));
        ImageView playView = new ImageView(playImage);
        playView.setFitWidth(200);
        playView.setFitHeight(60);
        playView.setLayoutX(SCREEN_WIDTH / 2 - 100);
        playView.setLayoutY(SCREEN_HEIGHT / 2 - 30);

        // when player clicks the play button, hide menu and start game
        playView.setOnMouseClicked(e -> {
            menuPane.setVisible(false);

            if (startButton.getOnAction() != null) {
                startButton.fire();
            }
        });

        // button gets a little bit transparent when hovered
        playView.setOnMouseEntered(e -> playView.setOpacity(0.8));
        playView.setOnMouseExited(e -> playView.setOpacity(1.0));


        // the background goes first so everything else appears on top
        menuPane.getChildren().addAll(bgView, playView);
    }

    // connects the start button to the startGame action in Main
    public void setStartButtonAction(Runnable action) {
        startButton.setOnAction(e -> {
            menuPane.setVisible(false);
            action.run();
        });
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