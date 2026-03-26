package org.example.menu;

import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

/**
 * Handles the start menu screen for Campus Craze.
 *
 * @author : Victoria Adeosun
 * Branch: feature/ui-manager-victoriaAdeosun
 *
 * Acomplishments:
 *  Display title label
 *  Display background placeholder (images have to be added later)
 *  Add Start button
 *  Attach button event to call startGame()
 *  Hide menu when Start is clicked
 */
public class MenuScene {

    private Pane menuPane;

    // UI elements
    private Label titleLabel;
    private Button startButton;

    // Background placeholder rectangle (replaces image until asset is ready)
    private Rectangle backgroundPlaceholder;

    // Screen dimensions
    private static final double SCREEN_WIDTH  = 1024;
    private static final double SCREEN_HEIGHT = 768;

    // Constructor
    public MenuScene(Pane pane) {
        this.menuPane = pane;

        // Initialize placeholders
        this.titleLabel  = new Label("Campus Craze");
        this.startButton = new Button("Start");
    }

    /**
     * setupMenu()
     * Positions all UI elements and adds them to the menuPane.
     * Called once before the menu is shown.
     *
     * Implementation: Victoria Adeosun
     */
    public void setupMenu() {

        // ── BACKGROUND PLACEHOLDER ────────────────────────────
        // A dark rectangle that fills the whole screen.
        // Replace with: new ImageView(backgroundImage) when asset is ready.
        backgroundPlaceholder = new Rectangle(SCREEN_WIDTH, SCREEN_HEIGHT);
        backgroundPlaceholder.setFill(Color.web("#1a1a2e")); // dark navy
        backgroundPlaceholder.setX(0);
        backgroundPlaceholder.setY(0);

        // ── TITLE LABEL ───────────────────────────────────────
        titleLabel.setText("🎓 Campus Craze");
        titleLabel.setFont(Font.font("Arial Black", FontWeight.EXTRA_BOLD, 52));
        titleLabel.setTextFill(Color.web("#f5c518")); // bold yellow
        titleLabel.setTextAlignment(TextAlignment.CENTER);

        // Position title in the centre top area of the screen
        titleLabel.setLayoutX(SCREEN_WIDTH / 2 - 220);
        titleLabel.setLayoutY(SCREEN_HEIGHT / 2 - 150);

        // ── SUBTITLE LABEL ────────────────────────────────────
        Label subtitleLabel = new Label("Rush to class. Dodge everything. Survive.");
        subtitleLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 18));
        subtitleLabel.setTextFill(Color.web("#cccccc"));
        subtitleLabel.setLayoutX(SCREEN_WIDTH / 2 - 200);
        subtitleLabel.setLayoutY(SCREEN_HEIGHT / 2 - 70);

        // ── IMAGE PLACEHOLDER NOTE ────────────────────────────
        // TODO: When background image asset is ready, replace
        // backgroundPlaceholder with:
        //
        //   Image bgImage = new Image(getClass()
        //       .getResourceAsStream("/Menu/menu_background.png"));
        //   ImageView bgView = new ImageView(bgImage);
        //   bgView.setFitWidth(SCREEN_WIDTH);
        //   bgView.setFitHeight(SCREEN_HEIGHT);
        //   menuPane.getChildren().add(0, bgView);

        // ── START BUTTON ──────────────────────────────────────
        startButton.setText("START GAME");
        startButton.setPrefWidth(200);
        startButton.setPrefHeight(50);
        startButton.setLayoutX(SCREEN_WIDTH / 2 - 100);
        startButton.setLayoutY(SCREEN_HEIGHT / 2);
        startButton.setStyle(
                "-fx-background-color: #f5c518;" +
                        "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        // Hover effect
        startButton.setOnMouseEntered(e -> startButton.setStyle(
                "-fx-background-color: #ffd700;" +
                        "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));
        startButton.setOnMouseExited(e -> startButton.setStyle(
                "-fx-background-color: #f5c518;" +
                        "-fx-text-fill: #1a1a2e;" +
                        "-fx-font-size: 18px;" +
                        "-fx-font-weight: bold;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        // ── ADD EVERYTHING TO PANE ────────────────────────────
        // Background must be added FIRST so it appears behind everything
        menuPane.getChildren().addAll(
                backgroundPlaceholder,
                titleLabel,
                subtitleLabel,
                startButton
        );
    }

    /**
     * setStartButtonAction()
     * Binds the start button click to the given action (startGame()).
     * Also hides the menu when clicked.
     *
     * Implementation: Victoria Adeosun
     */
    public void setStartButtonAction(Runnable action) {
        startButton.setOnAction(e -> {
            // Hide the menu pane when start is clicked
            menuPane.setVisible(false);

            // Call startGame() from Main
            action.run();
        });
    }

    // ── Getters ───────────────────────────────────────────────

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