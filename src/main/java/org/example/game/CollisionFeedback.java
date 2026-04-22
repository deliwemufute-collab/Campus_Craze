package org.example.game;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/**
 * Screen shake + colour flash on obstacle hit or coin collection.
 * Owned by Paakhi. Instantiated in GameScene, injected into ObstacleManager.
 */
public class CollisionFeedback {

    private final Pane      gamePane;
    private final Rectangle hitFlash;
    private final Rectangle coinFlash;
    private boolean         shaking = false;

    public CollisionFeedback(Pane gamePane, double w, double h) {
        this.gamePane = gamePane;
        hitFlash  = makeRect(w, h, Color.rgb(220, 30, 30));
        coinFlash = makeRect(w, h, Color.rgb(255, 215, 0));
        gamePane.getChildren().addAll(hitFlash, coinFlash);
    }

    public void triggerHit() {
        flash(hitFlash, 0.55, 350);
        if (!shaking) shake();
    }

    public void triggerCoin() {
        flash(coinFlash, 0.28, 180);
    }

    private void shake() {
        shaking = true;
        double d = 10.0, s = 50.0;
        Timeline t = new Timeline(
                kf(0, 0), kf(s, +d), kf(s*2, -d),
                kf(s*3, +d*.6), kf(s*4, -d*.6), kf(s*5, +d*.3), kf(s*6, 0));
        t.setOnFinished(e -> { gamePane.setTranslateX(0); shaking = false; });
        t.play();
    }

    private void flash(Rectangle r, double peak, double ms) {
        r.setOpacity(peak);
        new Timeline(
                new KeyFrame(Duration.ZERO,       new KeyValue(r.opacityProperty(), peak)),
                new KeyFrame(Duration.millis(ms), new KeyValue(r.opacityProperty(), 0.0))
        ).play();
    }

    private KeyFrame kf(double ms, double tx) {
        return new KeyFrame(Duration.millis(ms),
                new KeyValue(gamePane.translateXProperty(), tx));
    }

    private Rectangle makeRect(double w, double h, Color c) {
        Rectangle r = new Rectangle(w, h, c);
        r.setOpacity(0);
        r.setMouseTransparent(true);
        r.setViewOrder(-100);
        return r;
    }
}