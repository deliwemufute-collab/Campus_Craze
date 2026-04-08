package org.example.utils;

import javafx.scene.image.Image;

import java.io.InputStream;

/**
 * Handles preloading of all game assets at startup.
 * Assets are loaded once into memory and reused throughout the game,
 * avoiding expensive disk reads during gameplay.
 *
 * Milestone 2 changes:
 *  - Added loadThemeAssets(String theme) for loading themed tile sets
 *    (city / park / subway). Each theme has 6 tiles:
 *      <theme>_tile_start, _tile_mid_1..4, _tile_top
 *  - loadSceneryAssets() is retained for backward compatibility.
 *  - Unknown or null themes fall back gracefully to an empty-slot array.
 *
 * Resource paths (must match files in src/main/resources/Tiles/):
 *   /Tiles/city_tile_start.png  ... city_tile_top.png
 *   /Tiles/park_tile_start.png  ... park_tile_top.png
 *   /Tiles/subway_tile_start.png ... subway_tile_top.png
 *
 * @author Miguel Cofre, Emmanuel Ayobanjo
 */
public class AssetLoader {

    // The preloaded scenery tile images (size depends on load method)
    private Image[] sceneryVariants;

    // Fixed count for the legacy generic load
    private static final int SCENERY_VARIANT_COUNT = 16;

    // Tile suffixes for each theme set — must match actual filenames
    private static final String[] THEME_TILE_SUFFIXES = {
            "start",
            "mid_1",
            "mid_2",
            "mid_3",
            "mid_4",
            "top"
    };

    // -----------------------------------------------------------------------
    // Milestone 1 — legacy loader (retained)
    // -----------------------------------------------------------------------

    /**
     * Preloads 16 generic scenery tile variants from /assets/scenery/.
     * Call this once before the game loop if not using themed loading.
     */
    public void loadSceneryAssets() {
        sceneryVariants = new Image[SCENERY_VARIANT_COUNT];

        for (int i = 0; i < SCENERY_VARIANT_COUNT; i++) {
            String path = String.format("/assets/scenery/tile_%02d.png", i);
            sceneryVariants[i] = loadImage(path, i > 0 ? sceneryVariants[0] : null);
        }
    }

    // -----------------------------------------------------------------------
    // Milestone 2 — themed loader
    // -----------------------------------------------------------------------

    /**
     * Preloads 6 tile variants for the given theme.
     * Each slot maps to: start, mid_1, mid_2, mid_3, mid_4, top.
     *
     * Supported themes: "city", "park", "subway".
     * Unknown or null themes produce a 6-slot array of null entries
     * (SceneryManager handles nulls gracefully).
     *
     * @param theme the theme name; case-sensitive, e.g. "city"
     */
    public void loadThemeAssets(String theme) {
        sceneryVariants = new Image[THEME_TILE_SUFFIXES.length];

        if (theme == null || (!theme.equals("city")
                && !theme.equals("park")
                && !theme.equals("subway"))) {
            System.err.println("AssetLoader WARNING: Unknown theme '"
                    + theme + "' — returning empty slot array.");
            // Array slots stay null; SceneryManager will skip null images
            return;
        }

        for (int i = 0; i < THEME_TILE_SUFFIXES.length; i++) {
            String path = "/Tiles/" + theme + "_tile_" + THEME_TILE_SUFFIXES[i] + ".jpg";
            sceneryVariants[i] = loadImage(path, i > 0 ? sceneryVariants[0] : null);
        }
    }

    // -----------------------------------------------------------------------
    // Internal helper
    // -----------------------------------------------------------------------

    /**
     * Tries to load an image from the given classpath resource.
     * Returns fallback if the resource is missing, to avoid null propagation.
     *
     * @param path     classpath-relative path, e.g. "/Tiles/city_tile_start.png"
     * @param fallback image to use when the file is missing (may be null)
     * @return loaded Image, or fallback, or null if both unavailable
     */
    private Image loadImage(String path, Image fallback) {
        InputStream stream = getClass().getResourceAsStream(path);
        if (stream != null) {
            try {
                Image img = new Image(stream);
                System.out.println("AssetLoader: loaded " + path);
                return img;
            } catch (Exception e) {
                System.err.println("AssetLoader WARNING: Error reading " + path
                        + " — " + e.getMessage());
            }
        } else {
            System.err.println("AssetLoader WARNING: Missing " + path
                    + " — using fallback.");
        }
        return fallback;
    }

    // -----------------------------------------------------------------------
    // Getter
    // -----------------------------------------------------------------------

    /**
     * Returns the array of preloaded scenery images.
     * Returns null if neither loadSceneryAssets() nor loadThemeAssets() has
     * been called yet.
     */
    public Image[] getSceneryVariants() {
        return sceneryVariants;
    }
}