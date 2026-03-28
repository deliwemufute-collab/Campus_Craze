package org.example.utils;

import com.sun.javafx.application.PlatformImpl;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class AssetLoaderTest {

    private AssetLoader assetLoader;

    // Boot the JavaFX runtime before tests run, as JUnit does not start it automatically
    @BeforeAll
    static void initToolkit() throws Exception {
        PlatformImpl.startup(() -> {});
    }

    @BeforeEach
    void setUp() {
        assetLoader = new AssetLoader();
    }

    @Test
    void testGetSceneryVariantsNullBeforeLoad() {
        // Before loadSceneryAssets() is called, array should be null
        assertNull(assetLoader.getSceneryVariants());
    }

    @Test
    void testLoadSceneryAssetsCreatesArray() {
        assetLoader.loadSceneryAssets();
        assertNotNull(assetLoader.getSceneryVariants());
    }

    @Test
    void testLoadSceneryAssetsReturns16Slots() {
        // Array must always have exactly 16 slots regardless of missing files
        assetLoader.loadSceneryAssets();
        assertEquals(16, assetLoader.getSceneryVariants().length);
    }

    @Test
    void testNoSlotThrowsException() {
        // loadSceneryAssets() handles missing files internally — should never throw
        assertDoesNotThrow(() -> assetLoader.loadSceneryAssets());
    }
}