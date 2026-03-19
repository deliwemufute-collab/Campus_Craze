package org.example.game;

import javafx.scene.image.Image;

import java.util.Objects;

/**
 * Handles preloading of all game assets at startup.
 * Assets are loaded once into memory and reused throughout the game,
 * avoiding expensive disk reads during gameplay.
 *
 * Scenery variants are expected at:
 *   /assets/scenery/tile_00.png  through  tile_15.png
 *
 * @author Miguel Cofre, Emmanuel Ayobanjo
 */
public class AssetLoader {

    // The 16 preloaded scenery tile images
    private Image[] sceneryVariants;

    // Number of scenery variants required
    private static final int SCENERY_VARIANT_COUNT = 16;

    /**
     * Preloads all 16 scenery tile variants from the assets folder.
     * Call this once before the game loop starts.
     */
    public void loadSceneryAssets() {
        sceneryVariants = new Image[SCENERY_VARIANT_COUNT];

        for (int i = 0; i < SCENERY_VARIANT_COUNT; i++) {
            String path = String.format("/assets/scenery/tile_%02d.png", i);
            try {
                sceneryVariants[i] = new Image(
                        Objects.requireNonNull(
                                getClass().getResourceAsStream(path),
                                "Missing asset: " + path
                        )
                );
                System.out.println("Loaded scenery variant: " + path);
            } catch (NullPointerException e) {
                System.err.println("AssetLoader WARNING: Could not load " + path
                        + " — using fallback placeholder.");
                // Fallback: reuse the first successfully loaded image,
                // or leave null if nothing has loaded yet (handled by SceneryManager)
                sceneryVariants[i] = (i > 0) ? sceneryVariants[0] : null;
            }
        }
    }

    /**
     * Returns the array of all 16 preloaded scenery images.
     * Call loadSceneryAssets() first.
     */
    public Image[] getSceneryVariants() {
        return sceneryVariants;
    }
}