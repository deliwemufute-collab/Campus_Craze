package org.example.game;

import javafx.animation.FadeTransition;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * Represents a hazard obstacle the player must avoid.
 *
 * Extends GameObject and implements Collidable.
 *
 * x = CENTRE of the obstacle (lane value).
 * Sprite placed at (x - width/2) so it centres on the lane.
 * setX/setY are overridden to keep sprite in sync.
 *
 * getBounds() uses a manual BoundingBox — NOT getBoundsInParent() which
 * needs a JavaFX layout pass and returns zeros on early frames.
 *
 * Size tiers match actual PNG dimensions:
 *   SMALL 48x60   MEDIUM 74x103   LARGE 140x200
 *
 * On collision with Player, showExplosion() overlays the matching
 * explosion PNG, fades it out, then removes it from the pane.
 *
 * Legacy 3-arg constructor keeps existing ObstacleTest passing unchanged.
 */
public class Obstacle extends GameObject implements Collidable {

    // ── Size tiers ─────────────────────────────────────────────────────────

    public enum Size {
        SMALL (48,  60),
        MEDIUM(74,  103),
        LARGE (140, 200);

        public final double w, h;
        Size(double w, double h) { this.w = w; this.h = h; }
    }

    // ── Theme ──────────────────────────────────────────────────────────────

    public enum Theme { CITY, PARK, SUBWAY }

    // ── Fields ─────────────────────────────────────────────────────────────

    private final ImageView sprite;
    private final Theme     theme;
    private final Size      size;

    private Image explosionImage;
    private Pane  gamePane;

    private boolean collidedWithPlayer = false;

    public void setCollidedWithPlayer(boolean value) {
        this.collidedWithPlayer = value;
    }

    // ── Full constructor ───────────────────────────────────────────────────

    public Obstacle(Image spriteImage, double centreX, double startY,
                    Theme theme, Size size) {
        super(centreX, startY, size.w, size.h);
        this.theme  = theme;
        this.size   = size;
        this.sprite = new ImageView(spriteImage);
        sprite.setFitWidth(size.w);
        sprite.setFitHeight(size.h);
        sprite.setPreserveRatio(false);
        syncSprite();
    }

    /**
     * Legacy 3-arg constructor — keeps existing ObstacleTest passing.
     * Renders at 80x80 to match the original test assertions.
     */
    public Obstacle(Image spriteImage, double centreX, double startY) {
        super(centreX, startY, 80, 80);
        this.theme  = Theme.CITY;
        this.size   = Size.SMALL;
        this.sprite = new ImageView(spriteImage);
        sprite.setFitWidth(80);
        sprite.setFitHeight(80);
        sprite.setPreserveRatio(true);
        syncSprite();
    }

    // ── Explosion injection ────────────────────────────────────────────────

    public void setExplosionImage(Image img, Pane pane) {
        this.explosionImage = img;
        this.gamePane       = pane;
    }

    // ── GameObject ─────────────────────────────────────────────────────────

    @Override
    public void update() {
        scrollDown(3.0);
        updatePosition();
    }
    @Override public void updatePosition()  { syncSprite(); }

    // ── Collidable ─────────────────────────────────────────────────────────

    @Override
    public Bounds getBounds() {
        // x is horizontal centre of the obstacle (lane position); hitbox matches the sprite.
        return new BoundingBox(
                x - width / 2,
                y,
                width,
                height
        );
    }

    @Override
    public void onCollision(Collidable other) {
        if (other instanceof Player) {
            collidedWithPlayer = true;
            showExplosion();
            System.out.println("[Obstacle:" + theme + ":" + size + "] hit Player");
        }
    }

    // ── setX / setY ────────────────────────────────────────────────────────

    @Override public void setX(double x) { super.setX(x); syncSprite(); }
    @Override public void setY(double y) { super.setY(y); syncSprite(); }

    // ── Movement ───────────────────────────────────────────────────────────

    public void scrollDown(double speed) { y += speed; syncSprite(); }
    public void scrollUp(double speed)   { y -= speed; syncSprite(); }

    public boolean isOffscreen(double screenH) { return y > screenH; }
    public boolean isAboveScreen()             { return y + height < 0; }

    // ── Explosion ──────────────────────────────────────────────────────────

    private void showExplosion() {
        if (explosionImage == null || gamePane == null) return;
        ImageView fx = new ImageView(explosionImage);
        fx.setFitWidth(width);
        fx.setFitHeight(height);
        fx.setPreserveRatio(false);
        fx.setX(x - width / 2);
        fx.setY(y);
        fx.setViewOrder(-50);
        gamePane.getChildren().add(fx);
        FadeTransition fade = new FadeTransition(Duration.millis(600), fx);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.setOnFinished(e -> gamePane.getChildren().remove(fx));
        fade.play();
    }

    // ── Helper ─────────────────────────────────────────────────────────────

    private void syncSprite() {
        sprite.setX(x - width / 2);
        sprite.setY(y);
    }

    // ── Getters ────────────────────────────────────────────────────────────

    public boolean   hasCollidedWithPlayer() { return collidedWithPlayer; }
    public void      resetCollision()        { collidedWithPlayer = false; }
    public Theme     getTheme()              { return theme; }
    public Size      getSize()               { return size; }
    public ImageView getSprite()             { return sprite; }
}




