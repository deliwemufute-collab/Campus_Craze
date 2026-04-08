package org.example.game;

import javafx.scene.image.Image;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages background and overlay SceneryTile objects to produce a continuous
 * vertical scrolling effect.
 *
 * Milestone 2 changes:
 *  - Added update() as the single per-frame entry point (replaces direct
 *    scrollTiles() calls from GameScene); handles scroll + recycle for both
 *    background tiles and overlay tiles in one call.
 *  - Added overlay tile support: spawnOverlayTile(), getOverlayTiles().
 *    Overlay tiles scroll at the same speed and recycle identically to
 *    background tiles but are kept in a separate list so GameScene can
 *    layer them correctly (overlays render on top of background).
 *  - scrollTiles() is retained for backward-compatibility with existing tests.
 *  - recycleTile() now guards against a null sceneryVariants array.
 *
 * @author Emmanuel Ayobanjo
 */
public class SceneryManager {

    // Background tiles
    private final List<SceneryTile> tiles;

    // Overlay tiles (side buildings, decorations drawn on top of road)
    private final List<SceneryTile> overlayTiles;

    // The pane that tiles are attached to
    private final Pane gamePane;

    // Pixels per frame all tiles scroll downward
    private double scrollSpeed;

    // Preloaded background scenery images
    private Image[] sceneryVariants;

    // Round-robin index for picking the next variant on spawn / recycle
    private int nextVariantIndex;

    // Screen height — tiles at or below this Y are recycled
    private final double screenHeight;

    // Tile width used when positioning spawned tiles
    private final double tileWidth;

    // Current theme mid tiles for normal shuffling
    private Image[] midVariants;

    // Queued transition tiles (top of old theme, start of new theme)
    private final java.util.Queue<Image> transitionQueue = new java.util.LinkedList<>();

    /**
     * Constructs a SceneryManager.
     *
     * @param gamePane     the pane to attach tiles to
     * @param scrollSpeed  pixels per frame tiles scroll downward
     * @param screenHeight height of the game screen (offscreen threshold)
     * @param tileWidth    width each background tile should occupy
     */
    public SceneryManager(Pane gamePane, double scrollSpeed,
                          double screenHeight, double tileWidth) {
        this.gamePane     = gamePane;
        this.scrollSpeed  = scrollSpeed;
        this.screenHeight = screenHeight;
        this.tileWidth    = tileWidth;
        this.tiles        = new ArrayList<>();
        this.overlayTiles = new ArrayList<>();
        this.nextVariantIndex = 0;
    }

    // -----------------------------------------------------------------------
    // Per-frame entry point
    // -----------------------------------------------------------------------

    /**
     * Main per-frame method. Scrolls and recycles all background tiles,
     * then scrolls and recycles all overlay tiles.
     *
     * Call this once per game loop frame from GameScene.
     */
    public void update() {
        scrollAndRecycle(tiles);
        scrollAndRecycle(overlayTiles);
    }

    /**
     * Triggers a theme transition. The next two recycled tiles will show
     * currentTop then nextStart, after which midVariants switches to nextMids.
     *
     * @param currentTop  the "top" tile of the outgoing theme
     * @param nextStart   the "start" tile of the incoming theme
     * @param nextMids    the mid tiles to shuffle after the transition
     */
    public void switchTheme(Image currentTop, Image nextStart, Image[] nextMids) {
        transitionQueue.clear();
        transitionQueue.add(currentTop);
        transitionQueue.add(nextStart);
        this.midVariants = nextMids;
        this.sceneryVariants = nextMids; // fallback for existing recycle logic
        this.nextVariantIndex = 0;
    }

    /**
     * Moves every tile in the list down by scrollSpeed and recycles any
     * that have gone fully offscreen.
     */
    private void scrollAndRecycle(List<SceneryTile> list) {
        for (SceneryTile tile : list) {
            tile.setY(tile.getY() + scrollSpeed);
        }

        for (SceneryTile tile : list) {
            if (tile.getY() >= screenHeight) {
                // Exclude the tile being recycled from the search
                double highestY = list.stream()
                        .filter(t -> t != tile)
                        .mapToDouble(SceneryTile::getY)
                        .min()
                        .orElse(0); // only one tile in list → place at top

                tile.setY(highestY - tile.getHeight());
                assignNextVariant(tile);
            }
        }
    }

    private void assignNextVariant(SceneryTile tile) {
        if (!transitionQueue.isEmpty()) {
            Image transition = transitionQueue.poll();
            if (transition != null) tile.setImage(transition);
            return;
        }
        if (sceneryVariants == null || sceneryVariants.length == 0) return;
        Image variant = sceneryVariants[nextVariantIndex % sceneryVariants.length];
        nextVariantIndex++;
        if (variant != null) tile.setImage(variant);
    }

    // -----------------------------------------------------------------------
    // Backward-compatible scroll method (used by Milestone 1 tests)
    // -----------------------------------------------------------------------

    /**
     * Scrolls all background tiles and recycles offscreen ones.
     * Kept for backward compatibility with existing Milestone 1 tests.
     * Prefer update() in production code.
     */
    public void scrollTiles() {
        scrollAndRecycle(tiles);
    }

    // -----------------------------------------------------------------------
    // Spawning
    // -----------------------------------------------------------------------

    /**
     * Spawns a new background tile at the given Y position.
     * The tile image is chosen from sceneryVariants in round-robin order.
     * Falls back to a dummy tile if no variants are loaded.
     *
     * @param startY initial Y position for the tile
     * @return the spawned SceneryTile (added to gameObjects list by GameScene)
     */
    public SceneryTile spawnTile(double startY) {
        Image variant = pickNextVariant();
        SceneryTile tile = new SceneryTile(variant, 0, startY, scrollSpeed);
        tiles.add(tile);
        gamePane.getChildren().add(tile.getSprite());
        return tile;
    }

    /**
     * Spawns a new overlay tile at the given position using the provided image.
     * Overlay tiles scroll at the same speed as background tiles and recycle
     * when they go offscreen. Their images are fixed (not round-robin) because
     * overlays represent specific side assets (e.g. a left-side building strip).
     *
     * @param image  the overlay image to display
     * @param startX initial X position (typically 0 for left, screenWidth - w for right)
     * @param startY initial Y position
     * @return the spawned SceneryTile
     */
    public SceneryTile spawnOverlayTile(Image image, double startX, double startY) {
        SceneryTile tile = new SceneryTile(image, startX, startY, scrollSpeed);
        overlayTiles.add(tile);
        gamePane.getChildren().add(tile.getSprite());
        return tile;
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------

    /**
     * Recycles a tile back above the screen and assigns the next background
     * variant. If sceneryVariants is null or empty, the tile image is left
     * unchanged (safe no-op).
     */
    private void recycleTile(SceneryTile tile) {
        tile.setY(-tile.getHeight());
        assignNextVariant(tile);
    }

    /**
     * Returns the next variant image in round-robin order.
     * Falls back to a dummy asset if no variants are set.
     */
    private Image pickNextVariant() {
        if (sceneryVariants != null && sceneryVariants.length > 0) {
            Image v = sceneryVariants[nextVariantIndex % sceneryVariants.length];
            nextVariantIndex++;
            return v;
        }
        // Fallback so spawnTile() never crashes when variants are not yet loaded
        return new Image(getClass().getResourceAsStream("/Tiles/dummy_tile_start.png"));
    }

    // -----------------------------------------------------------------------
    // Variants / theme
    // -----------------------------------------------------------------------

    /**
     * Sets the pool of background images used when spawning and recycling tiles.
     * Call this to switch themes; the next recycle will pick from the new set.
     *
     * @param variants array of preloaded Image objects (any length); may be null
     */
    public void setSceneryVariants(Image[] variants) {
        this.sceneryVariants = variants;
        this.nextVariantIndex = 0;
    }

    // -----------------------------------------------------------------------
    // Getters / setters
    // -----------------------------------------------------------------------

    public List<SceneryTile> getTiles() {
        return tiles;
    }

    public List<SceneryTile> getOverlayTiles() {
        return overlayTiles;
    }

    public double getScrollSpeed() {
        return scrollSpeed;
    }

    public void setScrollSpeed(double scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }
}