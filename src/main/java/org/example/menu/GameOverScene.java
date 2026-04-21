package org.example.menu;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

/**
 * Displays the Game Over screen using the game assets.
 * Shows the background image, final score, coins, and a play again button.
 *
 * @author Victoria Adeosun
 */
public class GameOverScene {

    private final Pane gameOverPane;
    private Label scoreLabel;
    private Label coinsLabel;
    private Runnable onRestart;

    // set up the game over screen with the pane to add everything to
    public GameOverScene(Pane gameOverPane) {
        this.gameOverPane = gameOverPane;
        setupGameOver();
    }

    // build all the visual elements for the game over screen
    private void setupGameOver() {

        // load the game over background image we made in Canva
        Image bgImage = new Image(getClass().getResourceAsStream("/Menu_assets/game_over_background.jpg"));
        ImageView background = new ImageView(bgImage);
        background.setFitWidth(1024);
        background.setFitHeight(768);

        // show the final score on top of the background
        scoreLabel = new Label("Score: 0");
        scoreLabel.setFont(Font.font("Arial", 36));
        scoreLabel.setTextFill(Color.WHITE);
        scoreLabel.setLayoutX(412);
        scoreLabel.setLayoutY(310);

        // show the coins collected on top of the background
        coinsLabel = new Label("Coins: 0");
        coinsLabel.setFont(Font.font("Arial", 36));
        coinsLabel.setTextFill(Color.GOLD);
        coinsLabel.setLayoutX(412);
        coinsLabel.setLayoutY(360);

        // reuse the existing play button asset as the play again button
        Image playBtnImage = new Image(getClass().getResourceAsStream("/Menu_assets/play_button.png"));
        ImageView playAgainBtn = new ImageView(playBtnImage);
        playAgainBtn.setFitWidth(200);
        playAgainBtn.setFitHeight(60);
        playAgainBtn.setLayoutX(412);
        playAgainBtn.setLayoutY(450);

        // trigger the restart when the button is clicked
        playAgainBtn.setOnMouseClicked(e -> {
            if (onRestart != null) onRestart.run();
        });

        // make it look clickable by changing opacity on hover
        playAgainBtn.setOnMouseEntered(e -> playAgainBtn.setOpacity(0.8));
        playAgainBtn.setOnMouseExited(e -> playAgainBtn.setOpacity(1.0));

        // add everything to the pane in the right order
        gameOverPane.getChildren().addAll(background, scoreLabel, coinsLabel, playAgainBtn);
    }

    // update the score and coins labels when the game ends
    public void setFinalStats(int score, int coins) {
        scoreLabel.setText("Score: " + score);
        coinsLabel.setText("Coins: " + coins);
    }

    // lets Main.java pass in what happens when play again is clicked
    public void setOnRestart(Runnable onRestart) {
        this.onRestart = onRestart;
    }

    // returns the pane so it can be added to the root
    public Pane getGameOverPane() {
        return gameOverPane;
    }
}
