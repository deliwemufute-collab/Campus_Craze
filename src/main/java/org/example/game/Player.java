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

    /** Updates position each frame — basic movement only for now */
    @Override
    public void updatePosition() {
        if (movingLeft)  x -= 5;
        if (movingRight) x += 5;
        if (x < minX)         { x = minX; }
        if (x > maxX - width) { x = maxX - width; }
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

    /** Injects ScoreManager so Player can trigger loseLife() */
    public void setScoreManager(ScoreManager scoreManager) {
        this.scoreManager = scoreManager;
    }
}