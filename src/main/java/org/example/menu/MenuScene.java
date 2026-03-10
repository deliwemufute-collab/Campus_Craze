package org.example.menu;

import javafx.scene.layout.Pane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

// Menu screen handler, VICTORIA ADEOSUN realm!
public class MenuScene {

    private Pane menuPane;

    // UI elements
    private Label titleLabel;
    private Button startButton;

    // Constructor
    public MenuScene(Pane pane) {
        this.menuPane = pane;

        // Initialize placeholders
        this.titleLabel = new Label("Campus Craze"); // Placeholder
        this.startButton = new Button("Start");
    }

    // Setup menu layout
    public void setupMenu() {
        // To be implemented by VICTORIA ADEOSUN: position labels, buttons, add to menuPane
    }

    // Event handlers (skeleton)
    public void setStartButtonAction(Runnable action) {
        // To be implemented by VICTORIA ADEOSUN: bind start button click to action
    }

    // Getters
    public Pane getMenuPane() {
        return menuPane;
    }

    public Button getStartButton() {
        return startButton;
    }

    public Label getTitleLabel() {
        return titleLabel;
    }
}