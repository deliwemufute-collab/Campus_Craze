package org.example.game;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Represents the player character in the Campus Craze game.
 * Extends GameObject: inherits x, y, width, height and must implement update()
 * Implements Collidable: must provide getBounds(), updatePosition(), onCollision()
 * @author Deliwe Mufute
 */

public class Player extends GameObject implements Collidable {

    private long lastObstacleCollisionTime = 0;
    private static final long OBSTACLE_COLLISION_COOLDOWN_MS = 1000;
    // 1 second
    /**
     * The visual representation of the player on screen
     */
    private ImageView sprite;

    /**
     * True while LEFT arrow key is held down
     */
    private boolean movingLeft = false;

    /**
     * True while RIGHT arrow key is held down
     */
    private boolean movingRight = false;
    /**
     * True while UP arrow key is held down — slight forward movement
     */
    private boolean movingUp = false;

    /**
     * True while DOWN arrow key is held down — slight backward movement
     */
    private boolean movingDown = false;

    /**
     * Left road boundary — player cannot go left of this
     */
    private final double minX;

    /**
     * Right road boundary — player cannot go right of this
     */
    private final double maxX;

    /**
     * Reference to ScoreManager — set by GameScene after construction
     */
    private ScoreManager scoreManager;
    /**
     * Current horizontal speed — builds up and slows down smoothly
     */
    private double velocityX = 0;

    /**
     * Current vertical speed — kept small for slight forward/backward movement
     */
    private double velocityY = 0;

    /**
     * How fast velocity builds each frame when key is held
     */
    private static final double ACCELERATION = 1.0;

    /**
     * Friction factor — velocity shrinks by 15% each frame when key released
     */
    private static final double DECELERATION = 0.85;

    /**
     * Maximum horizontal speed
     */
    private static final double MAX_SPEED_X = 6.0;

    /**
     * Maximum vertical speed to cover 3/4 of screen
     */
    private static final double MAX_SPEED_Y = 5.0;

    /**
     * Top boundary — how far forward player can move
     */
    private double minY;

    /**
     * Bottom boundary — how far backward player can move
     */
    private double maxY;

    // Cooldown to prevent collision firing every frame for the same contact
    private long lastBuildingCollisionTime = 0;
    private static final long BUILDING_COLLISION_COOLDOWN_MS = 500; // half a second
    // --- INVULNERABILITY WINDOW ---
    /**
     * True while player is invulnerable after being hit
     */
    private boolean invulnerable = false;

    /**
     * Timestamp when invulnerability started
     */
    private long invulnerabilityStartTime = 0;

    /**
     * How long invulnerability lasts in milliseconds (0.4 seconds)
     */
    private static final long INVULNERABILITY_DURATION_MS = 400;

// --- HIT FLASH ---
    /**
     * True while the hit flash animation is running
     */
    private boolean flashing = false;

    /**
     * Tracks flash cycle for opacity animation
     */
    private int flashFrameCount = 0;

    /**
     * How many frames the flash animation lasts
     */
    private static final int FLASH_DURATION_FRAMES = 20;

    /**
     * Creates a new Player at the given position within the road boundaries.
     *
     * @param spriteImage the image to display as the player character
     * @param startX      starting X position
     * @param startY      starting Y position
     * @param roadLeft    left road boundary
     * @param roadRight   right road boundary
     */
    public Player(Image spriteImage, double startX, double startY,
                  double roadLeft, double roadRight) {
        super(startX, startY, spriteImage.getWidth(), spriteImage.getHeight());
        this.sprite = new ImageView(spriteImage);
        this.sprite.setX(startX);
        this.sprite.setY(startY);
        this.minX = roadLeft;
        this.maxX = roadRight;
        if (this.x < minX) this.x = minX;
        if (this.x + width > maxX) this.x = maxX - width;
        sprite.setX(this.x);
        this.minY = startY - 426;
        this.maxY = startY + 100;
    }

    /**
     * Called when LEFT arrow key is pressed
     */
    public void moveLeft() {
        movingLeft = true;
    }

    /**
     * Called when RIGHT arrow key is pressed
     */
    public void moveRight() {
        movingRight = true;
    }

    /**
     * Called when LEFT arrow key is released
     */
    public void stopLeft() {
        movingLeft = false;
    }

    /**
     * Called when RIGHT arrow key is released
     */
    public void stopRight() {
        movingRight = false;
    }

    /**
     * Called when UP arrow key is pressed
     */
    public void moveUp() {
        movingUp = true;
    }

    /**
     * Called when DOWN arrow key is pressed
     */
    public void moveDown() {
        movingDown = true;
    }

    /**
     * Called when UP arrow key is released
     */
    public void stopUp() {
        movingUp = false;
    }

    /**
     * Called when DOWN arrow key is released
     */
    public void stopDown() {
        movingDown = false;
    }


    /**
     * Called every frame by GameScene polymorphically
     */
    @Override
    public void update() {
        updatePosition();
    }

    /**
     * Updates the player position each frame using velocity-based movement.
     * 1. Accelerates velocity in direction of pressed keys
     * 2. Applies friction when no key pressed — smooth stop
     * 3. Clamps velocity to max speed
     * 4. Applies velocity to position
     * 5. Clamps position to road and Y boundaries
     * 6. Syncs sprite to updated position
     */
    @Override
    public void updatePosition() {

        // --- HORIZONTAL VELOCITY ---
        if (movingLeft) velocityX -= ACCELERATION;
        if (movingRight) velocityX += ACCELERATION;
        if (!movingLeft && !movingRight) {
            velocityX *= DECELERATION;
            if (Math.abs(velocityX) < 0.1) velocityX = 0;
        }

        // --- VERTICAL VELOCITY (slight forward/backward) ---
        if (movingUp) velocityY -= ACCELERATION;
        if (movingDown) velocityY += ACCELERATION;
        if (!movingUp && !movingDown) {
            velocityY *= DECELERATION;
            if (Math.abs(velocityY) < 0.1) velocityY = 0;
        }

        // --- CLAMP VELOCITY TO MAX SPEED ---
        velocityX = Math.max(-MAX_SPEED_X, Math.min(MAX_SPEED_X, velocityX));
        velocityY = Math.max(-MAX_SPEED_Y, Math.min(MAX_SPEED_Y, velocityY));

        // --- APPLY VELOCITY TO POSITION ---
        x += velocityX;
        y += velocityY;

        // --- CLAMP TO BOUNDARIES ---
        if (x < minX) {
            x = minX;
            velocityX = 0;
        }
        if (x > maxX - width) {
            x = maxX - width;
            velocityX = 0;
        }
        if (y < minY) {
            y = minY;
            velocityY = 0;
        }
        if (y > maxY) {
            y = maxY;
            velocityY = 0;
        }

        // --- SYNC SPRITE ---
        sprite.setX(x);
        sprite.setY(y);

// --- HIT FLASH ANIMATION ---
// Cycles sprite opacity on/off to show player was hit
        if (flashing) {
            flashFrameCount++;
            // Toggle opacity every 3 frames for visible flicker effect
            sprite.setOpacity(flashFrameCount % 6 < 3 ? 0.3 : 1.0);
            if (flashFrameCount >= FLASH_DURATION_FRAMES) {
                // Flash finished — reset to full opacity
                flashing = false;
                flashFrameCount = 0;
                sprite.setOpacity(1.0);
            }
        }

// --- INVULNERABILITY WINDOW CHECK ---
// Check if invulnerability window has expired
        if (invulnerable) {
            long now = System.currentTimeMillis();
            if (now - invulnerabilityStartTime >= INVULNERABILITY_DURATION_MS) {
                invulnerable = false;
            }
        }

    }


    /**
     * Returns a reduced bounding box for collision detection.
     * Milestone 3: shrinks hitbox by 20% on all sides to prevent
     * unfair collisions where the player visually misses but still gets hit.
     * This is the hitbox leeway.
     */
    @Override
    public Bounds getBounds() {
        double leeway = 0.20; // 20% reduction on each side
        double offsetX = width * leeway;
        double offsetY = height * leeway;
        return new BoundingBox(
                x + offsetX,
                y + offsetY,
                width - offsetX * 2,
                height - offsetY * 2
        );

    }

    /**
     * Handles collision response
     */
    @Override
    public void onCollision(Collidable other) {
        if (other == null) return;

        if (other instanceof Obstacle && scoreManager != null) {

            // Skip if player is currently invulnerable
            if (invulnerable) return;

            long now = System.currentTimeMillis();

            if (now - lastObstacleCollisionTime < OBSTACLE_COLLISION_COOLDOWN_MS) {
                return;
            }

            lastObstacleCollisionTime = now;

            System.out.println("PLAYER HIT OBSTACLE → lose life");

            scoreManager.loseLife();

            // Start invulnerability window so player cannot be hit again immediately
            invulnerable = true;
            invulnerabilityStartTime = now;

            // Start hit flash animation
            flashing = true;
            flashFrameCount = 0;
        }

        if (other instanceof BuildingTile) {
            long now = System.currentTimeMillis();
            if (now - lastBuildingCollisionTime < BUILDING_COLLISION_COOLDOWN_MS) return;
            lastBuildingCollisionTime = now;

            System.out.println("Player collided with: " + other.getClass().getSimpleName());

            if (x < minX) {
                x = minX;
            } else if (x + width > maxX) {
                x = maxX - width;
            }
            sprite.setX(x);
        }
    }

    /**
     * Returns the player sprite
     */
    public ImageView getSprite() {
        return sprite;
    }

    /**
     * Returns left road boundary
     */
    public double getRoadMinX() {
        return minX;
    }

    /**
     * Returns right road boundary
     */
    public double getRoadMaxX() {
        return maxX;
    }

    /**
     * Returns current horizontal velocity — used in tests
     */
    public double getVelocityX() {
        return velocityX;
    }

    /**
     * Returns current vertical velocity — used in tests
     */
    public double getVelocityY() {
        return velocityY;
    }

    /**
     * Sets custom Y boundaries for vertical movement.
     *
     * @param minY highest Y the player can reach
     * @param maxY lowest Y the player can reach
     */
    public void setYBoundaries(double minY, double maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    /**
     * Returns true if player is currently invulnerable — used in tests
     */
    public boolean isInvulnerable() {
        return invulnerable;
    }

    /**
     * Returns true if hit flash is currently playing — used in tests
     */
    public boolean isFlashing() {
        return flashing;
    }

    /**
     * Returns current top Y boundary — used in tests
     */
    public double getMinY() {
        return minY;
    }

    /**
     * Returns current bottom Y boundary — used in tests
     */
    public double getMaxY() {
        return maxY;
    }


    /**
     * Injects ScoreManager so Player can trigger loseLife()
     */
    // it lets GameScene pass in the score manager
    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }
}
