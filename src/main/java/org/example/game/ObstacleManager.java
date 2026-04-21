package org.example.game;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ObstacleManager {

    private enum MovingKind {
        CITY_LARGE,
        PARK_LARGE_1
    }

    private record SpawnConfig(Image sprite, Obstacle.Size size, MovingKind movingKind) {}

    private final Pane gamePane;

    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Coin> coins = new ArrayList<>();

    private final Map<Obstacle, MovingKind> movingObstacles = new IdentityHashMap<>();
    private final Map<Obstacle, Double> movingVerticalOffsets = new IdentityHashMap<>();

    private ScoreManager scoreManager;
    private CollisionFeedback feedback;
    private Player trackedPlayer;

    private double scrollSpeed = 3.0;
    private double elapsedSeconds = 0.0;
    private double lastScreenHeight = 768.0;

    private Obstacle.Theme currentTheme = Obstacle.Theme.CITY;

    private final Random rng = new Random();

    private static final double[] LANES = {380, 480, 580};
    // Theme timing (seconds)
    private static final double THEME_CITY_END_SECONDS = 108.0;
    private static final double THEME_PARK_END_SECONDS = 208.0;

    // Moving obstacles should stop SPAWNING 10s before the theme switch,
    // but any already spawned should keep moving until they naturally leave screen.
    private static final double CITY_LARGE_SPAWN_END_SECONDS = THEME_CITY_END_SECONDS - 10.0;      // 98s
    private static final double PARK_LARGE_1_SPAWN_END_SECONDS = THEME_PARK_END_SECONDS - 10.0;   // 198s

    private static final double SPAWN_INTERVAL = 1.2;
    private static final double OBJECT_SPAWN_PADDING = 18.0;
    private static final double MOVING_TRACKING_BLEND = 0.20;
    private static final double MIN_MOVING_Y = 40.0;

    // Theme assets
    private Image[] citySmall;
    private Image[] cityMedium;
    private Image[] cityLarge;

    private Image[] parkSmall;
    private Image[] parkMedium;
    private Image parkLarge1;
    private Image parkLarge2;

    private Image[] subwaySmall;
    private Image[] subwayMedium;
    private Image[] subwayLarge;

    private Image coinImage;
    private Image explosionSmall;
    private Image explosionMedium;
    private Image explosionLarge;

    // Spawn control
    private double spawnTimer = 0;

    public ObstacleManager(Pane pane) {
        this.gamePane = pane;
        loadAssets();
    }

    // Test constructor
    public ObstacleManager(Pane pane, Image obstacleImage, Image coinImage) {
        this.gamePane = pane;
        this.citySmall = new Image[]{obstacleImage};
        this.cityMedium = new Image[]{obstacleImage};
        this.cityLarge = new Image[]{obstacleImage};
        this.parkSmall = new Image[]{obstacleImage};
        this.parkMedium = new Image[]{obstacleImage};
        this.parkLarge1 = obstacleImage;
        this.parkLarge2 = obstacleImage;
        this.subwaySmall = new Image[]{obstacleImage};
        this.subwayMedium = new Image[]{obstacleImage};
        this.subwayLarge = new Image[]{obstacleImage};
        this.coinImage = coinImage;
        this.explosionSmall = obstacleImage;
        this.explosionMedium = obstacleImage;
        this.explosionLarge = obstacleImage;
    }

    private void loadAssets() {
        citySmall = new Image[]{
                new Image(getClass().getResourceAsStream("/Obstacles/city_small_obstacle_1.png")),
                new Image(getClass().getResourceAsStream("/Obstacles/city_small_obstacle_2.png"))
        };
        cityMedium = new Image[]{
                new Image(getClass().getResourceAsStream("/Obstacles/city_medium_obstacle_1.png")),
                new Image(getClass().getResourceAsStream("/Obstacles/city_medium_obstacle_2.png"))
        };
        cityLarge = new Image[]{
                new Image(getClass().getResourceAsStream("/Obstacles/city_large_obstacle_1.png")),
                new Image(getClass().getResourceAsStream("/Obstacles/city_large_obstacle_2.png"))
        };

        parkSmall = new Image[]{
                new Image(getClass().getResourceAsStream("/Obstacles/park_small_obstacle_1.png")),
                new Image(getClass().getResourceAsStream("/Obstacles/park_small_obstacle_2.png"))
        };
        parkMedium = new Image[]{
                new Image(getClass().getResourceAsStream("/Obstacles/park_medium_obstacle_1.png")),
                new Image(getClass().getResourceAsStream("/Obstacles/park_medium_obstacle_2.png"))
        };
        parkLarge1 = new Image(getClass().getResourceAsStream("/Obstacles/park_large_obstacle_1.png"));
        parkLarge2 = new Image(getClass().getResourceAsStream("/Obstacles/park_large_obstacle_2.png"));

        subwaySmall = new Image[]{
                new Image(getClass().getResourceAsStream("/Obstacles/subway_small_obstacle_1.png")),
                new Image(getClass().getResourceAsStream("/Obstacles/subway_small_obstacle_2.png"))
        };
        subwayMedium = new Image[]{
                new Image(getClass().getResourceAsStream("/Obstacles/subway_medium_obstacle_1.png")),
                new Image(getClass().getResourceAsStream("/Obstacles/subway_medium_obstacle_2.png"))
        };
        subwayLarge = new Image[]{
                new Image(getClass().getResourceAsStream("/Obstacles/subway_large_obstacle_1.png")),
                new Image(getClass().getResourceAsStream("/Obstacles/subway_large_obstacle_2.png"))
        };

        coinImage = new Image(getClass().getResourceAsStream("/Obstacles/coin.gif"));
        explosionSmall = new Image(getClass().getResourceAsStream("/Obstacles/explosion_small.png"));
        explosionMedium = new Image(getClass().getResourceAsStream("/Obstacles/explosion_medium.png"));
        explosionLarge = new Image(getClass().getResourceAsStream("/Obstacles/explosion_large.png"));
    }

    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }

    public void setFeedback(CollisionFeedback feedback) {
        this.feedback = feedback;
    }

    public void setPlayer(Player player) {
        this.trackedPlayer = player;
    }

    public void setElapsedSeconds(double seconds) {
        Obstacle.Theme previousTheme = currentTheme;
        this.elapsedSeconds = seconds;

        if (seconds < THEME_CITY_END_SECONDS) {
            currentTheme = Obstacle.Theme.CITY;
        } else if (seconds < THEME_PARK_END_SECONDS) {
            currentTheme = Obstacle.Theme.PARK;
        } else {
            currentTheme = Obstacle.Theme.SUBWAY;
        }

        if (previousTheme != currentTheme) {
            spawnTimer = 0;
        }
    }

    public Obstacle.Theme getCurrentTheme() {
        return currentTheme;
    }

    public List<GameObject> spawnInitialObjects() {
        List<GameObject> list = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            Obstacle obstacle = spawnObstacle(LANES[i], -200 - i * 120);
            if (obstacle != null) {
                list.add(obstacle);
            }
        }

        Coin coin = spawnCoin(LANES[1], -150);
        if (coin != null) {
            list.add(coin);
        }

        return list;
    }

    public void updateSpawning(double deltaTime) {
        spawnTimer += deltaTime;

        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0;
            spawnObstacleInFreeLane();
            spawnCoinInFreeLane();
        }
    }

    public Obstacle spawnObstacle(double centreX, double startY) {
        List<SpawnConfig> spawnPool = buildSpawnPool();
        if (spawnPool.isEmpty()) {
            return null;
        }

        SpawnConfig config = spawnPool.get(rng.nextInt(spawnPool.size()));
        Obstacle obstacle = new Obstacle(
                config.sprite(),
                centreX,
                startY,
                currentTheme,
                config.size()
        );
        obstacle.setExplosionImage(selectExplosionImage(config.size()), gamePane);

        if (config.movingKind() != null) {
            initializeMovingObstacle(obstacle, config.movingKind());
        }

        if (!canPlaceObject(obstacle.getX(), obstacle.getY(), obstacle.getWidth(), obstacle.getHeight())) {
            return null;
        }

        obstacles.add(obstacle);
        gamePane.getChildren().add(obstacle.getSprite());
        return obstacle;
    }

    public Coin spawnCoin(double centreX, double startY) {
        if (!canPlaceObject(centreX, startY, Coin.COIN_W, Coin.COIN_H)) {
            return null;
        }

        Coin coin = new Coin(coinImage, centreX, startY);
        coins.add(coin);
        gamePane.getChildren().add(coin.getSprite());
        return coin;
    }

    public void scrollObstacles(double screenHeight, Player player) {
        lastScreenHeight = screenHeight;
        if (player != null) {
            trackedPlayer = player;
        }

        Iterator<Obstacle> obstacleIterator = obstacles.iterator();
        while (obstacleIterator.hasNext()) {
            Obstacle obstacle = obstacleIterator.next();
            MovingKind kind = movingObstacles.get(obstacle);

            if (kind != null && !isMovingObstacleStillAllowed(kind)) {
                removeObstacle(obstacleIterator, obstacle);
                continue;
            }

            // Moving "vehicle" obstacles drive past from behind (bottom -> top).
            if (kind == MovingKind.CITY_LARGE || kind == MovingKind.PARK_LARGE_1) {
                obstacle.scrollUp(scrollSpeed);
                continue;
            }

            // Everything else scrolls downward (standard runner-style scroll).
            obstacle.scrollDown(scrollSpeed);
        }

        Iterator<Coin> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Coin coin = coinIterator.next();
            coin.scrollDown(scrollSpeed);
            if (!coin.isOffscreen(screenHeight)) {
                continue;
            }
            coinIterator.remove();
            gamePane.getChildren().remove(coin.getSprite());
        }

        obstacleIterator = obstacles.iterator();
        while (obstacleIterator.hasNext()) {
            Obstacle obstacle = obstacleIterator.next();
            MovingKind kind = movingObstacles.get(obstacle);
            if (kind == MovingKind.CITY_LARGE || kind == MovingKind.PARK_LARGE_1) {
                if (!obstacle.isAboveScreen()) {
                    continue;
                }
                removeObstacle(obstacleIterator, obstacle);
                continue;
            }
            if (!obstacle.isOffscreen(screenHeight)) {
                continue;
            }
            removeObstacle(obstacleIterator, obstacle);
        }
    }

    public void checkCollisions(Player player) {
        if (player == null) {
            return;
        }

        Bounds playerBounds = player.getBounds();

        for (Obstacle obstacle : obstacles) {
            if (!obstacle.getBounds().intersects(playerBounds)) {
                continue;
            }

            if (player.canTakeObstacleHit()) {
                obstacle.onCollision(player);
                if (feedback != null) {
                    feedback.triggerHit();
                }
            }
            player.onCollision(obstacle);
        }

        Iterator<Coin> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            Coin coin = coinIterator.next();
            if (!coin.isCollected() &&
                    coin.getBounds().intersects(playerBounds)) {

                coin.onCollision(player);

                if (scoreManager != null) {
                    scoreManager.addCoin();
                }
                coinIterator.remove();
                gamePane.getChildren().remove(coin.getSprite());
            }
        }
    }

    private void initializeMovingObstacle(Obstacle obstacle, MovingKind kind) {
        movingObstacles.put(obstacle, kind);
        double offset;
        if (kind == MovingKind.CITY_LARGE) {
            offset = 180 + rng.nextDouble() * 70;
        } else {
            offset = 150 + rng.nextDouble() * 65;
        }
        movingVerticalOffsets.put(obstacle, offset);

        if (kind == MovingKind.CITY_LARGE || kind == MovingKind.PARK_LARGE_1) {
            // Start just below the screen so it drives upward past the player.
            obstacle.setY(lastScreenHeight + obstacle.getHeight() + 20);
        }
    }

    private boolean isMovingObstacleStillAllowed(MovingKind kind) {
        if (kind == MovingKind.CITY_LARGE) {
            return elapsedSeconds < CITY_LARGE_SPAWN_END_SECONDS;
        }
        return elapsedSeconds >= THEME_CITY_END_SECONDS
                && elapsedSeconds < PARK_LARGE_1_SPAWN_END_SECONDS;
    }

    private void removeObstacle(Iterator<Obstacle> iterator, Obstacle obstacle) {
        iterator.remove();
        movingObstacles.remove(obstacle);
        movingVerticalOffsets.remove(obstacle);
        gamePane.getChildren().remove(obstacle.getSprite());
    }

    private List<SpawnConfig> buildSpawnPool() {
        List<SpawnConfig> pool = new ArrayList<>();

        if (elapsedSeconds < THEME_CITY_END_SECONDS) {
            for (Image image : citySmall) {
                pool.add(new SpawnConfig(image, Obstacle.Size.SMALL, null));
            }
            for (Image image : cityMedium) {
                pool.add(new SpawnConfig(image, Obstacle.Size.MEDIUM, null));
            }
            // Reduce CITY_LARGE frequency by giving it a low chance to enter the pool.
            // (It already stops spawning 10s before the theme switch.)
            if (elapsedSeconds < CITY_LARGE_SPAWN_END_SECONDS && rng.nextInt(5) == 0) { // ~20% chance
                for (Image image : cityLarge) {
                    pool.add(new SpawnConfig(image, Obstacle.Size.LARGE, MovingKind.CITY_LARGE));
                }
            }
            return pool;
        }

        if (elapsedSeconds < THEME_PARK_END_SECONDS) {
            for (Image image : parkSmall) {
                pool.add(new SpawnConfig(image, Obstacle.Size.SMALL, null));
            }
            for (Image image : parkMedium) {
                pool.add(new SpawnConfig(image, Obstacle.Size.MEDIUM, null));
            }
            pool.add(new SpawnConfig(parkLarge2, Obstacle.Size.LARGE, null));
            if (elapsedSeconds < PARK_LARGE_1_SPAWN_END_SECONDS) {
                pool.add(new SpawnConfig(parkLarge1, Obstacle.Size.LARGE, MovingKind.PARK_LARGE_1));
            }
            return pool;
        }

        for (Image image : subwaySmall) {
            pool.add(new SpawnConfig(image, Obstacle.Size.SMALL, null));
        }
        for (Image image : subwayMedium) {
            pool.add(new SpawnConfig(image, Obstacle.Size.MEDIUM, null));
        }
        for (Image image : subwayLarge) {
            pool.add(new SpawnConfig(image, Obstacle.Size.LARGE, null));
        }
        return pool;
    }

    private void spawnObstacleInFreeLane() {
        List<Double> shuffledLanes = new ArrayList<>();
        for (double lane : LANES) {
            shuffledLanes.add(lane);
        }
        Collections.shuffle(shuffledLanes, rng);

        for (double lane : shuffledLanes) {
            Obstacle spawned = spawnObstacle(lane, -120);
            if (spawned != null) {
                return;
            }
        }
    }

    private void spawnCoinInFreeLane() {
        if (!rng.nextBoolean()) {
            return;
        }

        List<Double> shuffledLanes = new ArrayList<>();
        for (double lane : LANES) {
            shuffledLanes.add(lane);
        }
        Collections.shuffle(shuffledLanes, rng);

        for (double lane : shuffledLanes) {
            Coin coin = spawnCoin(lane, -180);
            if (coin != null) {
                return;
            }
        }
    }

    private boolean canPlaceObject(double centerX, double startY, double objectWidth, double objectHeight) {
        Bounds candidate = new BoundingBox(
                centerX - objectWidth / 2.0,
                startY,
                objectWidth,
                objectHeight
        );

        for (Obstacle obstacle : obstacles) {
            if (intersectsWithPadding(candidate, obstacle.getBounds(), OBJECT_SPAWN_PADDING)) {
                return false;
            }
        }

        for (Coin coin : coins) {
            if (intersectsWithPadding(candidate, coin.getBounds(), OBJECT_SPAWN_PADDING)) {
                return false;
            }
        }

        return true;
    }

    private boolean intersectsWithPadding(Bounds a, Bounds b, double padding) {
        BoundingBox expanded = new BoundingBox(
                b.getMinX() - padding,
                b.getMinY() - padding,
                b.getWidth() + 2 * padding,
                b.getHeight() + 2 * padding
        );
        return expanded.intersects(a);
    }

    private Image selectExplosionImage(Obstacle.Size size) {
        return switch (size) {
            case SMALL -> explosionSmall;
            case LARGE -> explosionLarge;
            default -> explosionMedium;
        };
    }

    public List<Obstacle> getObstacles() {
        return obstacles;
    }

    public List<Coin> getCoins() {
        return coins;
    }

    public double getScrollSpeed() {
        return scrollSpeed;
    }
}
