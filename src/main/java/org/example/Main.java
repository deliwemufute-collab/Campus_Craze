package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.game.GameScene;
import org.example.menu.MenuScene;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();

        Scene scene = new Scene(root, 1024, 768);

        // show menu first
        Pane menuPane = new Pane();
        root.getChildren().add(menuPane);

        MenuScene menuScene = new MenuScene(menuPane);
        menuScene.setupMenu();

        // when play button is clicked then hides the menu and starts the game
        menuScene.setStartButtonAction(() -> {
            root.getChildren().remove(menuPane);
            GameScene gameScene = new GameScene(root, 1024, 768);
            gameScene.initializeGameObjects();
            gameScene.setupKeyboard(scene);
            gameScene.startGameLoop();
        });

        stage.setTitle("SDII Game Project");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}