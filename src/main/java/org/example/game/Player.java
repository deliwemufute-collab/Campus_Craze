package org.example.game;

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

    /** The visual representation of the player on screen */
    private ImageView sprite;

    /** True while LEFT arrow key is held down */
    private boolean movingLeft  = false;

    /** True while RIGHT arrow key is held down */
    private boolean movingRight = false;
    /** True while UP arrow key is held down — slight forward movement */
    private boolean movingUp    = false;

    /** True while DOWN arrow key is held down — slight backward movement */
    private boolean movingDown  = false;

    /** Left road boundary — player cannot go left of this */
    private final double minX;

    /** Right road boundary — player cannot go right of this */
    private final double maxX;

    /** Reference to ScoreManager — set by GameScene after construction */
    private ScoreManager scoreManager;
    /** Current horizontal speed — builds up and slows down smoothly */
    private double velocityX = 0;

    /** Current vertical speed — kept small for slight forward/backward movement */
    private double velocityY = 0;

    /** How fast velocity builds each frame when key is held */
    private static final double ACCELERATION = 1.0;

    /** Friction factor — velocity shrinks by 15% each frame when key released */
    private static final double DECELERATION = 0.85;

    /** Maximum horizontal speed */
    private static final double MAX_SPEED_X  = 6.0;

    /** Maximum vertical speed — smaller than X to keep movement slight */
    private static final double MAX_SPEED_Y  = 3.0;

    /** Top boundary — how far forward player can move */
    private double minY;

    /** Bottom boundary — how far backward player can move */
    private double maxY;

    /**
     * Creates a new Player at the given position within the road boundaries.
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
        this.minY = startY - 80;
        this.maxY = startY + 40;
    }

    /** Called when LEFT arrow key is pressed */
    public void moveLeft()  { movingLeft  = true;  }

    /** Called when RIGHT arrow key is pressed */
    public void moveRight() { movingRight = true;  }

    /** Called when LEFT arrow key is released */
    public void stopLeft()  { movingLeft  = false; }

    /** Called when RIGHT arrow key is released */
    public void stopRight() { movingRight = false; }
    /** Called when UP arrow key is pressed */
    public void moveUp()    { movingUp    = true;  }

    /** Called when DOWN arrow key is pressed */
    public void moveDown()  { movingDown  = true;  }

    /** Called when UP arrow key is released */
    public void stopUp()    { movingUp    = false; }

    /** Called when DOWN arrow key is released */
    public void stopDown()  { movingDown  = false; }


    /** Called every frame by GameScene polymorphically */
    @Override
    public void update() { updatePosition(); }

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
        if (movingLeft)  velocityX -= ACCELERATION;
        if (movingRight) velocityX += ACCELERATION;
        if (!movingLeft && !movingRight) {
            velocityX *= DECELERATION;
            if (Math.abs(velocityX) < 0.1) velocityX = 0;
        }

        // --- VERTICAL VELOCITY (slight forward/backward) ---
        if (movingUp)   velocityY -= ACCELERATION;
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
        if (x < minX)         { x = minX;         velocityX = 0; }
        if (x > maxX - width) { x = maxX - width; velocityX = 0; }
        if (y < minY)         { y = minY;          velocityY = 0; }
        if (y > maxY)         { y = maxY;          velocityY = 0; }

        // --- SYNC SPRITE ---
        sprite.setX(x);
        sprite.setY(y);
    }

    /** Returns bounding box for collision detection */
    @Override
    public Bounds getBounds() { return sprite.getBoundsInParent(); }

    /** Handles collision response */
    @Override
    public void onCollision(Collidable other) {
        if (other == null) return;
        System.out.println("Player collided with: " + other.getClass().getSimpleName());
        if (other instanceof Obstacle && scoreManager != null) {
            scoreManager.loseLife();
        }
        if (other instanceof BuildingTile) {
            if (x < minX) { x = minX; }
            else if (x + width > maxX) { x = maxX - width; }
            sprite.setX(x);
        }
    }

    /** Returns the player sprite */
    public ImageView getSprite()  { return sprite; }

    /** Returns left road boundary */
    public double getRoadMinX()   { return minX;   }

    /** Returns right road boundary */
    public double getRoadMaxX()   { return maxX;   }
    /** Returns current horizontal velocity — used in tests */
    public double getVelocityX()  { return velocityX; }

    /** Returns current vertical velocity — used in tests */
    public double getVelocityY()  { return velocityY; }

    /**
     * Sets custom Y boundaries for vertical movement.
     * @param minY highest Y the player can reach
     * @param maxY lowest Y the player can reach
     */
    public void setYBoundaries(double minY, double maxY) {
        this.minY = minY;
        this.maxY = maxY;
    }

    /** Injects ScoreManager so Player can trigger loseLife() */
    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }
}