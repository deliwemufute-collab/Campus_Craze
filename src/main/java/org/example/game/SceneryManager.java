package org.example.game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages a list of SceneryTile objects to produce a continuous
 * vertical scrolling background effect.
 *
 * Responsibilities:
 *  - Maintains the active tile list
 *  - Scrolls all tiles downward each frame via scrollTiles()
 *  - Spawns new tiles at the top via spawnTile()
 *
 * @author Emmanuel Ayobanjo
 */
public class SceneryManager {

    // Active tiles currently on screen (or just off the top/bottom)
    private List<SceneryTile> tiles;

    // The pane that tiles are added to / removed from
    private Pane gamePane;

    // Pixels per frame that tiles move downward
    private double scrollSpeed;

    // All 16 preloaded scenery images (loaded once, reused forever)
    private Image[] sceneryVariants;

    // Tracks which variant to use next when spawning
    private int nextVariantIndex;

    // Screen height — tiles below this are considered offscreen
    private double screenHeight;

    // Tile width, used for positioning spawned tiles
    private double tileWidth;

    /**
     * Constructs a SceneryManager.
     *
     * @param gamePane      the pane to attach tiles to
     * @param scrollSpeed   how many pixels per frame tiles scroll down
     * @param screenHeight  height of the game screen (for offscreen detection)
     * @param tileWidth     width each tile should occupy
     */
    public SceneryManager(Pane gamePane, double scrollSpeed,
                          double screenHeight, double tileWidth) {
        this.gamePane   = gamePane;
        this.scrollSpeed = scrollSpeed;
        this.screenHeight = screenHeight;
        this.tileWidth  = tileWidth;
        this.tiles      = new ArrayList<>();
        this.nextVariantIndex = 0;
    }

    /**
     * Stores the 16 preloaded scenery images.
     * Must be called before spawnTile() is used.
     *
     * @param variants array of exactly 16 preloaded Image objects
     */
    public void setSceneryVariants(Image[] variants) {
        this.sceneryVariants = variants;
    }

    /**
     * Scrolls all active tiles downward by scrollSpeed pixels each frame.
     * Tiles that move fully offscreen (below screenHeight) are recycled
     * back to the top via recycleTile().
     *
     * Called once per game loop frame.
     */
    public void scrollTiles() {
        for (SceneryTile tile : tiles) {
            tile.setY(tile.getY() + scrollSpeed);

            // If the tile has gone entirely below the screen, recycle it
            if (tile.getY() >= screenHeight) {
                recycleTile(tile);
            }
        }
    }

    /**
     * Spawns a new SceneryTile at the top of the screen using the next
     * available scenery variant (cycles through all 16).
     * The new tile is added to the pane and tracked in the tiles list.
     *
     * @param startY the Y position to spawn the tile at (typically 0 or
     *               just above 0 so it scrolls in smoothly)
     */
    public void spawnTile(double startY) {
        if (sceneryVariants == null || sceneryVariants.length == 0) {
            System.err.println("SceneryManager: no scenery variants loaded.");
            return;
        }

        Image variant = sceneryVariants[nextVariantIndex % sceneryVariants.length];
        nextVariantIndex++;

        SceneryTile tile = new SceneryTile(variant, 0, startY);
        tiles.add(tile);
        gamePane.getChildren().add(tile.getSprite());
    }

    /**
     * Recycles an offscreen tile by moving it back to the top of the screen
     * and assigning it the next scenery variant.
     * This avoids creating and destroying objects every frame.
     *
     * @param tile the tile to recycle
     */
    private void recycleTile(SceneryTile tile) {
        // Move tile back above the screen (just off the top)
        tile.setY(-tile.getHeight());

        // Assign next variant so the scenery keeps changing
        Image variant = sceneryVariants[nextVariantIndex % sceneryVariants.length];
        nextVariantIndex++;
        tile.setImage(variant);
    }

    // Getters
    public List<SceneryTile> getTiles() {
        return tiles;
    }

    public double getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(double scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }
}