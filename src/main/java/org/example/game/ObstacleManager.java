package org.example.game;

import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.Pane;

import java.util.*;

/**
 * Manages all obstacles and coins.
 *
 * ── Coordinate system ──────────────────────────────────────────────────────
 *   Player.x   = LEFT EDGE   bounds = [x+inset … x+w-inset]
 *   Obstacle.x = CENTRE      bounds centred on x  (sprite at x - w/2)
 *   Coin.x     = CENTRE      same as Obstacle
 *
 * ── Theme schedule (time-based) ────────────────────────────────────────────
 *   0–99 s    CITY    city_small_2, city_medium_2
 *                     city_large_1 (oncoming, scrolls UP)
 *                     city_large_2 (rare oncoming, scrolls UP, long respawn)
 *   100–199 s PARK    park_small_1&2, park_medium_1&2
 *                     park_large_1   (oncoming, scrolls UP)
 *                     park_large_2   (static, does not move)
 *   200 s+    SUBWAY  subway_small_1&2, subway_medium_1&2
 *                     subway_large_1 (static, does not move)
 *
 * ── No overlap ─────────────────────────────────────────────────────────────
 *   Coins check obstacle bounds before spawning — null returned if overlap.
 *   Obstacles use a 200 px gap check on respawn to avoid stacking.
 *
 * ── Coin patterns (every 3.5 s) ────────────────────────────────────────────
 *   LINE / ZIGZAG / DIAMOND / CROSS / ARC / WAVE (rotates)
 *
 * ── Collision detection ────────────────────────────────────────────────────
 *   Manual BoundingBox — NOT getBoundsInParent() — works from frame 1.
 *
 * ── Two constructors ───────────────────────────────────────────────────────
 *   ObstacleManager(Pane)                  production — loads real PNGs
 *   ObstacleManager(Pane, Image, Image)    test — no file I/O needed
 */
public class ObstacleManager {

    private ScoreManager scoreManager;

    // ── Speeds ─────────────────────────────────────────────────────────────
    private double baseScrollSpeed;
    private double scrollSpeed;
    private static final double MAX_SPEED        = 7.5;
    private static final double SPEED_INCREMENT  = 0.25;
    private static final double ONCOMING_SPEED   = 4.0;  // city/park large UP

    // ── Timing ─────────────────────────────────────────────────────────────
    private static final double CITY_END             = 100.0;
    private static final double PARK_END             = 200.0;
    private static final double DIFFICULTY_INTERVAL  = 40.0;
    private static final double COIN_INTERVAL        = 3.5;

    // ── Lanes ──────────────────────────────────────────────────────────────
    private static final double[] LANES = {380, 456, 512, 568, 640};

    // ── Gap constants ──────────────────────────────────────────────────────
    private static final double MIN_GAP     = 100.0; // coin-to-obstacle
    private static final double OBS_MIN_GAP  = 250.0; // obstacle-to-obstacle vertical gap
    private static final double LARGE_X_GAP  = 300.0; // min horizontal gap between large obstacles

    // ── Spawn counts ───────────────────────────────────────────────────────
    private static final int MAX_OBSTACLES = 5;

    // ── State ──────────────────────────────────────────────────────────────
    private final List<Obstacle> obstacles = new ArrayList<>();
    private final List<Coin>     coins     = new ArrayList<>();

    private final Pane   gamePane;
    private final Random random = new Random(1);

    private int    diffLevel      = 0;
    private double elapsedSeconds = 0;
    private double lastCoinDrop   = 0;
    private int    coinPatternIdx = 0;

    private Obstacle.Theme currentTheme = Obstacle.Theme.CITY;

    private final Map<String, Image> spriteMap = new HashMap<>();

    private Image explosionSmall;
    private Image explosionMedium;
    private Image explosionLarge;
    private Image coinImage;

    private CollisionFeedback feedback;

    // tracks variant (1 or 2) for each large obstacle — used by isOncoming/isStatic
    private final Map<Obstacle, Integer> largeVariantMap = new HashMap<>();

    // ── Constructors ───────────────────────────────────────────────────────

    /** Production — loads real PNGs from /resources. */
    public ObstacleManager(Pane gamePane) {
        this.gamePane = gamePane;
        this.baseScrollSpeed = baseScrollSpeed;
        this.scrollSpeed = baseScrollSpeed;
        loadAllSprites();
        Image first = spriteMap.values().stream().findFirst().orElse(blank(48, 60));
        coinImage       = loadRes("/Obstacles/coin.gif", first);
        explosionSmall  = loadRes("/Obstacles/explosion_small.png",  blank(48, 60));
        explosionMedium = loadRes("/Obstacles/explosion_medium.png", blank(74, 103));
        explosionLarge  = loadRes("/Obstacles/explosion_large.png",  blank(140, 200));
    }

    /** Test — no file I/O. Pass WritableImage stubs. */
    public ObstacleManager(Pane gamePane, Image obstacleImage, Image coinImage) {
        this.gamePane  = gamePane;
        this.coinImage = coinImage;
        this.explosionSmall = this.explosionMedium = this.explosionLarge = blank(48, 60);
        for (String k : ALL_KEYS) spriteMap.put(k, obstacleImage);
    }

    // ── Theme control ──────────────────────────────────────────────────────

    /**
     * Called every frame by GameScene.
     * 0–99 s → CITY, 100–199 s → PARK, 200 s+ → SUBWAY (no loop).
     * Fires onThemeChanged() once when crossing each boundary.
     */
    public void setElapsedSeconds(double seconds) {
        this.elapsedSeconds = seconds;
        Obstacle.Theme newTheme =
                seconds < CITY_END ? Obstacle.Theme.CITY
                        : seconds < PARK_END ? Obstacle.Theme.PARK
                        : Obstacle.Theme.SUBWAY;
        if (newTheme != currentTheme) {
            currentTheme = newTheme;
            System.out.println("[ObstacleManager] theme -> " + currentTheme
                    + " at " + (int) seconds + "s");
            onThemeChanged();
        }
    }

    public void setTheme(Obstacle.Theme theme)   { this.currentTheme = theme; }
    public Obstacle.Theme getCurrentTheme()       { return currentTheme; }
    public void setFeedback(CollisionFeedback fb) { this.feedback = fb; }
    public double getElapsedSeconds()             { return elapsedSeconds; }

    // ── Theme transition ───────────────────────────────────────────────────

    /**
     * Removes large obstacles from the previous theme and spawns the correct
     * large obstacle(s) for the new theme well above screen.
     */
    private void onThemeChanged() {
        List<Obstacle> largeOnes = new ArrayList<>();
        for (Obstacle o : obstacles)
            if (o.getSize() == Obstacle.Size.LARGE) largeOnes.add(o);
        for (Obstacle o : largeOnes) {
            gamePane.getChildren().remove(o.getSprite());
            obstacles.remove(o);
            largeVariantMap.remove(o);
        }

        switch (currentTheme) {
            case CITY -> {
                spawnLarge(380, -400,  1); // city_large_1 — frequent oncoming
                spawnLarge(640, -2500, 2); // city_large_2 — rare oncoming (starts very far up)
            }
            case PARK -> {
                spawnLarge(456, -400, 1); // park_large_1 — oncoming (scrolls UP toward player)
                spawnLarge(640, -800, 2); // park_large_2 — static boulder (scrolls down with road)
            }
            case SUBWAY -> {
                spawnLarge(512, -400, 1); // subway_large_1 — static pillar only
            }
        }
    }

    // ── Spawning ───────────────────────────────────────────────────────────

    /**
     * Spawns a random-size obstacle for the current theme.
     * Size weighted: 50 % small, 30 % medium, 20 % large.
     */
    public Obstacle spawnObstacle(double centreX, double startY) {
        System.out.println("SPAWNING OBSTACLE:");
        System.out.println("  centreX = " + centreX);
        System.out.println("  startY  = " + startY);
        Obstacle.Size size    = randomSize();
        int           variant = random.nextInt(2) + 1;
        String        key     = key(currentTheme, size, variant);
        Image         img     = spriteMap.getOrDefault(key, blank(size));
        Obstacle      obs     = new Obstacle(img, centreX, startY, currentTheme, size);
        obs.setExplosionImage(explosionFor(size), gamePane);
        obstacles.add(obs);
        gamePane.getChildren().add(obs.getSprite());
        return obs;
    }

    /** Spawns a LARGE obstacle with an explicit variant (1 or 2). */
    private Obstacle spawnLarge(double centreX, double startY, int variant) {
        String   k   = key(currentTheme, Obstacle.Size.LARGE, variant);
        Image    img = spriteMap.getOrDefault(k, blank(Obstacle.Size.LARGE));
        Obstacle obs = new Obstacle(img, centreX, startY, currentTheme, Obstacle.Size.LARGE);
        obs.setExplosionImage(explosionLarge, gamePane);
        obstacles.add(obs);
        largeVariantMap.put(obs, variant); // remember which variant this is
        gamePane.getChildren().add(obs.getSprite());
        return obs;
    }

    /**
     * Like safeLane but also enforces LARGE_X_GAP against other large obstacles.
     * Used when respawning city_large_1 and city_large_2.
     */
    private double safeLaneForLarge(double y) {
        List<Double> shuffled = new ArrayList<>();
        for (double l : LANES) shuffled.add(l);
        Collections.shuffle(shuffled, random);
        for (double lane : shuffled) {
            if (!tooCloseToObstacle(lane, y, OBS_MIN_GAP) && !largeObstacleTooCloseX(lane))
                return lane;
        }
        // fallback: pick whichever lane is furthest from other large obstacles
        double bestLane = shuffled.get(0);
        double bestDist = 0;
        for (double lane : shuffled) {
            double minDist = Double.MAX_VALUE;
            for (Obstacle obs : obstacles) {
                if (obs.getSize() == Obstacle.Size.LARGE)
                    minDist = Math.min(minDist, Math.abs(obs.getX() - lane));
            }
            if (minDist > bestDist) { bestDist = minDist; bestLane = lane; }
        }
        return bestLane;
    }

    /**
     * Spawns a coin only if it doesn't overlap any live obstacle.
     * Returns null (silently skipped) if the position is blocked.
     */
    public Coin spawnCoin(double centreX, double startY) {
        Coin candidate = new Coin(coinImage, centreX, startY);
        for (Obstacle obs : obstacles) {
            if (candidate.getBounds().intersects(obs.getBounds())) return null;
        }
        // also check a manual gap buffer for extra breathing room
        for (Obstacle obs : obstacles) {
            double dx = Math.abs(obs.getX() - centreX);
            double dy = Math.abs(obs.getY() - startY);
            if (dx < obs.getWidth() / 2 + Coin.COIN_W / 2 + MIN_GAP
                    && dy < obs.getHeight() + Coin.COIN_H + MIN_GAP) return null;
        }
        coins.add(candidate);
        gamePane.getChildren().add(candidate.getSprite());
        return candidate;
    }

    /**
     * Spawns the initial obstacles + first coin pattern.
     * Returns all spawned objects for GameScene's gameObjects list.
     */
    public List<GameObject> spawnInitialObjects() {
        List<GameObject> out = new ArrayList<>();

        // small/medium — staggered 600px apart so each appears separately
        out.add(spawnObstacle(512,  -400));   // first one player sees
        out.add(spawnObstacle(380,  -1000));  // second
        out.add(spawnObstacle(640,  -1600));  // third

        // city large — oncoming cars, staggered far above so player eases in
        out.add(spawnLarge(380,  -1600, 1)); // city_large_1 (oncoming)
        out.add(spawnLarge(640,  -3000, 2)); // city_large_2 (rare oncoming, much further)

        // first coin pattern
        out.addAll(spawnCoinPattern(1, 512, -60));

        return out;
    }

    // ── Per-frame update ───────────────────────────────────────────────────

    /** Scroll all objects, respawn offscreen ones, ramp difficulty, drop coins. */
    public void scrollObstacles(double screenHeight) {
        System.out.println("scrolling obstacles: " + obstacles.size()); // Debugging
        applyDifficulty();
        maybeDropCoinPattern();

        List<Obstacle> toRespawn = new ArrayList<>();
        for (Obstacle obs : obstacles) {
            if (isOncoming(obs)) {
                // city/park large — scrolls UP toward player
                obs.scrollUp(ONCOMING_SPEED);
                if (obs.isAboveScreen()) toRespawn.add(obs);
            } else if (isStatic(obs)) {
                // park_large_2 and subway_large — "static" means scrolls DOWN
                // at scenery speed so it looks fixed on the road surface.
                // It does NOT scroll up or appear frozen against the moving background.
                obs.scrollDown(scrollSpeed);
                if (obs.isOffscreen(screenHeight)) toRespawn.add(obs);
            } else {
                // small/medium (all themes) + any other obstacle — scroll DOWN
                obs.scrollDown(scrollSpeed);
                if (obs.isOffscreen(screenHeight)) toRespawn.add(obs);
            }
        }
        for (Obstacle obs : toRespawn) {
            respawn(obs, screenHeight);
            obs.resetCollision();
        }

        List<Coin> toRemove = new ArrayList<>();
        for (Coin c : coins) {
            c.scrollDown(scrollSpeed);
            if (c.isOffscreen(screenHeight)) toRemove.add(c);
        }
        for (Coin c : toRemove) {
            gamePane.getChildren().remove(c.getSprite());
            coins.remove(c);
        }
    }

    /**
     * Collision detection — manual BoundingBox, works from frame 1.
     * Does NOT use getBoundsInParent() which needs a JavaFX layout pass.
     */
    public void checkCollisions(Player player) {
        Bounds pb = player.getBounds();

        for (Obstacle obs : obstacles) {
            if (obs.getBounds().intersects(pb)) {
                obs.onCollision(player);
                player.onCollision(obs);
                if (obs.hasCollidedWithPlayer() && feedback != null) {
                    feedback.triggerHit();
                }
            }
        }

        for (Coin c : coins) {
            if (!c.isCollected() && c.getBounds().intersects(pb)) {
                c.onCollision(player);
                player.onCollision(c);
                if (c.isCollected() && feedback != null) {
                    feedback.triggerCoin();
                }
            }
        }
    }

    // ── Movement classification ─────────────────────────────────────────────

    /**
     * city_large_1 & _2 = oncoming (scrolls UP)
     * park_large_1      = oncoming (scrolls UP)
     * park_large_2      = static
     * subway_large      = static
     */
    /**
     * Full movement rules per spec:
     *
     *  CITY   small  -> scrolls DOWN  (dodgeable)
     *  CITY   medium -> scrolls DOWN  (dodgeable)
     *  CITY   large  -> scrolls UP    (oncoming car — both variants)
     *
     *  PARK   small  -> scrolls DOWN  (static in the sense: normal scroll)
     *  PARK   medium -> scrolls DOWN
     *  PARK   large  -> scrolls UP    (oncoming animal)
     *
     *  SUBWAY small  -> scrolls DOWN
     *  SUBWAY medium -> scrolls DOWN
     *  SUBWAY large  -> STATIC        (pillar — does not move at all)
     *
     * NOTE: small/medium always scroll down regardless of theme.
     * Only LARGE obstacles have special behaviour.
     */
    private boolean isOncoming(Obstacle obs) {
        if (obs.getSize() != Obstacle.Size.LARGE) return false;
        return switch (obs.getTheme()) {
            case CITY   -> true;  // both city large scroll UP (oncoming cars)
            case PARK   -> largeVariantMap.getOrDefault(obs, 1) == 1; // only park_large_1 moves
            case SUBWAY -> false; // subway large is static
        };
    }

    private boolean isStatic(Obstacle obs) {
        if (obs.getSize() != Obstacle.Size.LARGE) return false;
        return switch (obs.getTheme()) {
            case CITY   -> false; // city large always oncoming
            case PARK   -> largeVariantMap.getOrDefault(obs, 1) == 2; // park_large_2 is static
            case SUBWAY -> true;  // subway large is static
        };
    }

    // ── Difficulty ramp ────────────────────────────────────────────────────

    private void applyDifficulty() {
        int newLevel = (int) (elapsedSeconds / DIFFICULTY_INTERVAL);
        if (newLevel > diffLevel) {
            diffLevel   = newLevel;
            scrollSpeed = Math.min(baseScrollSpeed + diffLevel * SPEED_INCREMENT, MAX_SPEED);
            if (obstacles.size() < MAX_OBSTACLES) {
                double lane = LANES[random.nextInt(LANES.length)];
                double y    = -300 - random.nextDouble() * 500;
                if (!tooCloseToObstacle(lane, y, OBS_MIN_GAP)) spawnObstacle(lane, y);
            }
        }
    }

    // ── Coin patterns ──────────────────────────────────────────────────────

    private void maybeDropCoinPattern() {
        if (elapsedSeconds - lastCoinDrop >= COIN_INTERVAL) {
            lastCoinDrop = elapsedSeconds;
            double lane = LANES[1 + random.nextInt(3)];
            spawnCoinPattern(coinPatternIdx % 6, lane, -Coin.COIN_H - 20);
            coinPatternIdx++;
        }
    }

    private List<GameObject> spawnCoinPattern(int type, double cx, double topY) {
        List<GameObject> out = new ArrayList<>();
        switch (type) {
            case 0 -> patternLine(cx, topY, out);
            case 1 -> patternZigzag(cx, topY, out);
            case 2 -> patternDiamond(cx, topY, out);
            case 3 -> patternCross(cx, topY, out);
            case 4 -> patternArc(cx, topY, out);
            default -> patternWave(cx, topY, out);
        }
        return out;
    }

    private void patternLine(double cx, double y, List<GameObject> out) {
        for (int i = 0; i < 4; i++) tryAddCoin(cx, y - i * 50, out);
    }

    private void patternZigzag(double cx, double y, List<GameObject> out) {
        for (int i = 0; i < 6; i++)
            tryAddCoin(i % 2 == 0 ? cx - 56 : cx + 56, y - i * 50, out);
    }

    private void patternDiamond(double cx, double y, List<GameObject> out) {
        tryAddCoin(cx,      y,       out);
        tryAddCoin(cx - 56, y - 50,  out);
        tryAddCoin(cx + 56, y - 50,  out);
        tryAddCoin(cx,      y - 100, out);
    }

    private void patternCross(double cx, double y, List<GameObject> out) {
        tryAddCoin(cx - 56, y, out);
        tryAddCoin(cx,      y, out);
        tryAddCoin(cx + 56, y, out);
    }

    private void patternArc(double cx, double y, List<GameObject> out) {
        tryAddCoin(cx - 112, y,       out);
        tryAddCoin(cx -  56, y -  30, out);
        tryAddCoin(cx,       y -  50, out);
        tryAddCoin(cx +  56, y -  30, out);
        tryAddCoin(cx + 112, y,       out);
    }

    private void patternWave(double cx, double y, List<GameObject> out) {
        double[] xs = {cx - 56, cx, cx + 56, cx, cx - 56, cx, cx + 56, cx};
        for (int i = 0; i < xs.length; i++) tryAddCoin(xs[i], y - i * 45, out);
    }

    private void tryAddCoin(double cx, double y, List<GameObject> out) {
        Coin c = spawnCoin(cx, y);
        if (c != null) out.add(c);
    }

    // ── Respawn ────────────────────────────────────────────────────────────

    private void respawn(Obstacle obs, double screenHeight) {
        // refresh sprite to current theme (important when theme just changed)
        if (obs.getSize() != Obstacle.Size.LARGE) {
            int variant = random.nextInt(2) + 1;
            String k = key(currentTheme, obs.getSize(), variant);
            Image img = spriteMap.getOrDefault(k, blank(obs.getSize()));
            obs.getSprite().setImage(img);
            obs.getSprite().setFitWidth(obs.getSize().w);
            obs.getSprite().setFitHeight(obs.getSize().h);
        }

        // city_large_2 gets a very long respawn gap so it's rare
        boolean rareCar = obs.getTheme() == Obstacle.Theme.CITY
                && obs.getSize() == Obstacle.Size.LARGE
                && obs.getSprite().getImage() == spriteMap.getOrDefault("CITY_LARGE_2", null);

        if (isOncoming(obs)) {
            double baseY = rareCar
                    ? screenHeight + 1200 + random.nextDouble() * 800
                    : screenHeight + 100  + random.nextDouble() * 400;
            // use safeLaneForLarge so two large obstacles are never side by side
            double safeX = (obs.getSize() == Obstacle.Size.LARGE)
                    ? safeLaneForLarge(baseY)
                    : safeLane(baseY, true);
            obs.setX(safeX);
            obs.setY(baseY + random.nextDouble() * 200);
        } else {
            double baseY = -obs.getSize().h - 200 - random.nextDouble() * 400;
            double safeX = safeLane(baseY, false);
            obs.setX(safeX);
            obs.setY(baseY);
        }
    }

    /** Returns a lane x that has at least OBS_MIN_GAP from other obstacles near y. */
    private double safeLane(double y, boolean wide) {
        List<Double> shuffled = new ArrayList<>();
        for (double l : LANES) shuffled.add(l);
        Collections.shuffle(shuffled, random);
        for (double lane : shuffled) {
            if (!tooCloseToObstacle(lane, y, OBS_MIN_GAP)) return lane;
        }
        return shuffled.get(0); // fallback — no perfect lane found
    }

    private boolean tooCloseToObstacle(double cx, double cy, double gap) {
        for (Obstacle obs : obstacles) {
            double dx = Math.abs(obs.getX() - cx);
            double dy = Math.abs(obs.getY() - cy);
            if (dx < obs.getWidth() / 2 + gap && dy < obs.getHeight() + gap) return true;
        }
        return false;
    }

    /**
     * Returns true if placing a LARGE obstacle at cx would be horizontally too
     * close to any other LARGE obstacle, regardless of y distance.
     * Prevents city_large_1 and city_large_2 appearing side by side.
     */
    private boolean largeObstacleTooCloseX(double cx) {
        for (Obstacle obs : obstacles) {
            if (obs.getSize() != Obstacle.Size.LARGE) continue;
            double dx = Math.abs(obs.getX() - cx);
            if (dx < LARGE_X_GAP) return true;
        }
        return false;
    }

    // ── Sprite loading ─────────────────────────────────────────────────────

    private static final String[] ALL_KEYS = {
            "CITY_SMALL_1","CITY_SMALL_2","CITY_MEDIUM_1","CITY_MEDIUM_2","CITY_LARGE_1","CITY_LARGE_2",
            "PARK_SMALL_1","PARK_SMALL_2","PARK_MEDIUM_1","PARK_MEDIUM_2","PARK_LARGE_1","PARK_LARGE_2",
            "SUBWAY_SMALL_1","SUBWAY_SMALL_2","SUBWAY_MEDIUM_1","SUBWAY_MEDIUM_2","SUBWAY_LARGE_1","SUBWAY_LARGE_2"
    };

    private void loadAllSprites() {
        loadK("CITY_SMALL_1",    "/Obstacles/city_small_obstacle_1.png");
        loadK("CITY_SMALL_2",    "/Obstacles/city_small_obstacle_2.png");
        loadK("CITY_MEDIUM_1",   "/Obstacles/city_medium_obstacle_1.png");
        loadK("CITY_MEDIUM_2",   "/Obstacles/city_medium_obstacle_2.png");
        loadK("CITY_LARGE_1",    "/Obstacles/city_large_obstacle_1.png");
        loadK("CITY_LARGE_2",    "/Obstacles/city_large_obstacle_2.png");
        loadK("PARK_SMALL_1",    "/Obstacles/park_small_obstacle_1.png");
        loadK("PARK_SMALL_2",    "/Obstacles/park_small_obstacle_2.png");
        loadK("PARK_MEDIUM_1",   "/Obstacles/park_medium_obstacle_1.png");
        loadK("PARK_MEDIUM_2",   "/Obstacles/park_medium_obstacle_2.png");
        loadK("PARK_LARGE_1",    "/Obstacles/park_large_obstacle_1.png");
        loadK("PARK_LARGE_2",    "/Obstacles/park_large_obstacle_2.png");
        loadK("SUBWAY_SMALL_1",  "/Obstacles/subway_small_obstacle_1.png");
        loadK("SUBWAY_SMALL_2",  "/Obstacles/subway_small_obstacle_2.png");
        loadK("SUBWAY_MEDIUM_1", "/Obstacles/subway_medium_obstacle_1.png");
        loadK("SUBWAY_MEDIUM_2", "/Obstacles/subway_medium_obstacle_2.png");
        loadK("SUBWAY_LARGE_1",  "/Obstacles/subway_large_obstacle_1.png");
        loadK("SUBWAY_LARGE_2",  "/Obstacles/subway_large_obstacle_2.png");
    }

    private void loadK(String key, String path) {
        Image img = loadRes(path, null);
        if (img != null) spriteMap.put(key, img);
        else System.err.println("[ObstacleManager] missing: " + path);
    }

    private Image loadRes(String path, Image fallback) {
        try {
            var s = getClass().getResourceAsStream(path);
            if (s != null) return new Image(s);
        } catch (Exception ignored) {}
        return fallback;
    }

    private String key(Obstacle.Theme theme, Obstacle.Size size, int variant) {
        return theme.name() + "_" + size.name() + "_" + variant;
    }

    private Image explosionFor(Obstacle.Size size) {
        return switch (size) {
            case SMALL  -> explosionSmall;
            case MEDIUM -> explosionMedium;
            case LARGE  -> explosionLarge;
        };
    }

    /** Small/medium only — large obstacles are always spawned via spawnLarge(). */
    private Obstacle.Size randomSize() {
        return random.nextBoolean() ? Obstacle.Size.SMALL : Obstacle.Size.MEDIUM;
    }

    private static Image blank(Obstacle.Size s) { return new WritableImage((int)s.w, (int)s.h); }
    private static Image blank(int w, int h)    { return new WritableImage(w, h); }

    // ── Getters ────────────────────────────────────────────────────────────

    public List<Obstacle> getObstacles()       { return obstacles; }
    public List<Coin>     getCoins()           { return coins; }
    public double         getScrollSpeed()     { return scrollSpeed; }
    public int            getDifficultyLevel() { return diffLevel; }

    // ── Setters ────────────────────────────────────────────────────────────
    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }
}