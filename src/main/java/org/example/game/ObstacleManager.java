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

    /**
     * Stub for GameScene compilation.
     * Paakhi... work your logic for spawning objects here.
     *
     * @param x initial X position
     * @param y initial Y position
     * @return a new dummy Obstacle so GameScene can compile
     */
    public Obstacle spawnObstacle(double x, double y) {
        // Using our dummy obstacle image for now
        Image dummyImage = new Image(getClass().getResourceAsStream("/Obstacles/obstacle_dummy.png"));

        Obstacle obstacle = new Obstacle(dummyImage, x, y);

        obstacles.add(obstacle);
        gamePane.getChildren().add(obstacle.getSprite());

        return obstacle; // return so GameScene can keep track
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