package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.example.game.GameScene;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();

        Scene scene = new Scene(root, 1024, 768);

        GameScene gameScene = new GameScene(root, 1024, 768);
        gameScene.initializeGameObjects();
        gameScene.setupKeyboard(scene);
        gameScene.startGameLoop();

        stage.setTitle("SDII Game Project");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}