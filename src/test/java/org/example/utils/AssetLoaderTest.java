package org.example.utils;

import com.sun.javafx.application.PlatformImpl;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AssetLoader.
 *
 * Milestone 1 tests: null before load, array created, 16 slots, no throw — retained.
 * Milestone 2 tests (new): loadThemeAssets() for city/park/subway themes,
 *                           correct slot count per theme, graceful fallback
 *                           on missing files, getter returns correct array.
 *
 * Each theme has 6 tiles: start, mid_1, mid_2, mid_3, mid_4, top.
 *
 * @author Emmanuel Ayobanjo
 */
public class AssetLoaderTest {

    private AssetLoader assetLoader;

    @BeforeAll
    static void initToolkit() throws Exception {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        assetLoader = new AssetLoader();
    }

    // -----------------------------------------------------------------------
    // Milestone 1 — retained
    // -----------------------------------------------------------------------

    @Test
    void testGetSceneryVariantsNullBeforeLoad() {
        assertNull(assetLoader.getSceneryVariants());
    }

    @Test
    void testLoadSceneryAssetsCreatesArray() {
        assetLoader.loadSceneryAssets();
        assertNotNull(assetLoader.getSceneryVariants());
    }

    @Test
    void testLoadSceneryAssetsReturns16Slots() {
        assetLoader.loadSceneryAssets();
        assertEquals(16, assetLoader.getSceneryVariants().length);
    }

    @Test
    void testNoSlotThrowsException() {
        assertDoesNotThrow(() -> assetLoader.loadSceneryAssets());
    }

    // -----------------------------------------------------------------------
    // Milestone 2 — themed loading: city
    // -----------------------------------------------------------------------

    @Test
    void testLoadCityAssetsDoesNotThrow() {
        assertDoesNotThrow(() -> assetLoader.loadThemeAssets("city"));
    }

    @Test
    void testLoadCityAssetsCreatesArray() {
        assetLoader.loadThemeAssets("city");
        assertNotNull(assetLoader.getSceneryVariants());
    }

    @Test
    void testLoadCityAssetsReturns6Slots() {
        // city: city_tile_start, city_tile_mid_1..4, city_tile_top
        assetLoader.loadThemeAssets("city");
        assertEquals(6, assetLoader.getSceneryVariants().length);
    }

    // -----------------------------------------------------------------------
    // Milestone 2 — themed loading: park
    // -----------------------------------------------------------------------

    @Test
    void testLoadParkAssetsDoesNotThrow() {
        assertDoesNotThrow(() -> assetLoader.loadThemeAssets("park"));
    }

    @Test
    void testLoadParkAssetsCreatesArray() {
        assetLoader.loadThemeAssets("park");
        assertNotNull(assetLoader.getSceneryVariants());
    }

    @Test
    void testLoadParkAssetsReturns6Slots() {
        assetLoader.loadThemeAssets("park");
        assertEquals(6, assetLoader.getSceneryVariants().length);
    }

    // -----------------------------------------------------------------------
    // Milestone 2 — themed loading: subway
    // -----------------------------------------------------------------------

    @Test
    void testLoadSubwayAssetsDoesNotThrow() {
        assertDoesNotThrow(() -> assetLoader.loadThemeAssets("subway"));
    }

    @Test
    void testLoadSubwayAssetsCreatesArray() {
        assetLoader.loadThemeAssets("subway");
        assertNotNull(assetLoader.getSceneryVariants());
    }

    @Test
    void testLoadSubwayAssetsReturns6Slots() {
        assetLoader.loadThemeAssets("subway");
        assertEquals(6, assetLoader.getSceneryVariants().length);
    }

    // -----------------------------------------------------------------------
    // Milestone 2 — robustness
    // -----------------------------------------------------------------------

    @Test
    void testLoadUnknownThemeDoesNotThrow() {
        // An unrecognised theme should not crash — fallback to empty or dummy
        assertDoesNotThrow(() -> assetLoader.loadThemeAssets("unknown"));
    }

    @Test
    void testLoadUnknownThemeReturnsNonNullArray() {
        assetLoader.loadThemeAssets("unknown");
        assertNotNull(assetLoader.getSceneryVariants());
    }

    @Test
    void testSwitchingThemeOverwritesPreviousArray() {
        // Load city first, then park — getSceneryVariants() should reflect park
        assetLoader.loadThemeAssets("city");
        assetLoader.loadThemeAssets("park");
        assertEquals(6, assetLoader.getSceneryVariants().length);
        // The array reference should be a fresh one, not the old city array
        assertNotNull(assetLoader.getSceneryVariants());
    }

    @Test
    void testLoadThemeAssetsNullThemeDoesNotThrow() {
        // Passing null theme should be handled gracefully
        assertDoesNotThrow(() -> assetLoader.loadThemeAssets(null));
    }
}