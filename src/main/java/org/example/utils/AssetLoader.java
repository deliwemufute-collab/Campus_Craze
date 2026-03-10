package org.example.utils;

import javafx.scene.image.Image;

// This will handle the loading of assets, Sprites, BGM, etc...
// Handled by all of us!
public class AssetLoader {

    // Example placeholders for assets
    private Image playerSprite;
    private Image[] sceneryTiles;
    private Image[] obstacles;

    // Constructor
    public AssetLoader() {
        // Placeholder: assets can be loaded here or via separate load methods
    }

    // Load all assets (skeleton method)
    public void loadAssets() {
        // To be implemented: load images, sounds, etc.
    }

    // Getters for assets
    public Image getPlayerSprite() {
        return playerSprite;
    }
    public Image[] getSceneryTiles() {
        return sceneryTiles;
    }
    public Image[] getObstacles() {
        return obstacles;
    }
}