package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("If you guys are seeing this on the JavaFx screen... it means you did everything correctly... CONGRATS!");
        StackPane root = new StackPane(label);

        Scene scene = new Scene(root, 800, 600);

        stage.setTitle("SDII Game Project");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}