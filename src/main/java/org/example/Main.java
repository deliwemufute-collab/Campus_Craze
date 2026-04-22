package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.game.GameScene;
import org.example.menu.MenuScene;
import org.example.menu.CreditsScene;
import org.example.menu.GameOverScene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class Main extends Application {

    private MediaPlayer mediaPlayer;

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 1024, 768);

        // load and loop the background music
        try {
            String musicPath = getClass().getResource("/Soundtrack/from_east_to_west.mp3").toExternalForm();
            Media media = new Media(musicPath);
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            mediaPlayer.play();
        } catch (Exception e) {
            System.out.println("Could not load music: " + e.getMessage());
        }

        showMenu(root, scene);

        stage.setTitle("SDII Game Project");
        stage.setScene(scene);
        stage.show();
    }

    // show the main menu
    private void showMenu(Pane root, Scene scene) {
        root.getChildren().clear();
        Pane menuPane = new Pane();
        root.getChildren().add(menuPane);

        MenuScene menuScene = new MenuScene(menuPane);
        menuScene.setupMenu();
        menuScene.setMediaPlayer(mediaPlayer);

        // credits button
        menuScene.setCreditsButtonAction(() -> {
            Pane creditsPane = new Pane();
            root.getChildren().add(creditsPane);
            CreditsScene creditsScene = new CreditsScene(creditsPane);
            creditsScene.setOnBack(() -> root.getChildren().remove(creditsPane));
        });

        // play button starts the game
        menuScene.setStartButtonAction(() -> startGame(root, scene));
    }

    // start a fresh game
    private void startGame(Pane root, Scene scene) {
        root.getChildren().clear();
        GameScene gameScene = new GameScene(root, 1024, 768);
        gameScene.initializeGameObjects();
        gameScene.setupKeyboard(scene);
        gameScene.setMediaPlayer(mediaPlayer);

        // when lives run out show game over
        gameScene.getScoreManager().setOnGameOver(() -> {
            gameScene.stopGameLoop();
            showGameOver(root, scene,
                    gameScene.getScoreManager().getScore(),
                    gameScene.getScoreManager().getCoins());
        });

        gameScene.startGameLoop();
    }

    // show the game over screen
    private void showGameOver(Pane root, Scene scene, int score, int coins) {
        Pane gameOverPane = new Pane();
        root.getChildren().add(gameOverPane);

        GameOverScene gameOverScene = new GameOverScene(gameOverPane);
        gameOverScene.setFinalStats(score, coins);

        // play again starts a fresh game
        gameOverScene.setOnRestart(() -> startGame(root, scene));
    }

    public static void main(String[] args) {
        launch(args);
    }
}