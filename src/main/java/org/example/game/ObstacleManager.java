package org.example.game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ObstacleManager {

    private final List<Obstacle> obstacles = new ArrayList<>();
    private final Pane gamePane;
    private final Image obstacleImage;
    private final double scrollSpeed = 3.0;
    private final Random random = new Random();

    // lane positions across the road
    private final double[] lanes = {380, 512, 640};

    public ObstacleManager(Pane gamePane) {
        this.gamePane = gamePane;

        obstacleImage = new Image(
                getClass().getResourceAsStream("/Obstacles/obstacle_dummy.png")
        );
    }

    public void spawnObstacle(double x, double y) {
        Obstacle obstacle = new Obstacle(obstacleImage, x, y);
        obstacles.add(obstacle);
        gamePane.getChildren().add(obstacle.getSprite());
    }

    public void scrollObstacles(double screenHeight) {
        for (Obstacle obstacle : obstacles) {
            obstacle.scrollDown(scrollSpeed);

            // if obstacle leaves screen, respawn randomly
            if (obstacle.isOffscreen(screenHeight)) {
                double randomX = lanes[random.nextInt(lanes.length)];
                double randomY = -100 - random.nextInt(500); // somewhere above screen

                obstacle.setX(randomX);
                obstacle.setY(randomY);
            }
        }
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }
}