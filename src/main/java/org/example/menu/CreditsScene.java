package org.example.menu;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.scene.text.FontWeight;

/**
 * Displays the Credits screen showing the team names and student numbers.
 * Triggered when the credits button is clicked on the main menu.
 *
 * @author Victoria Adeosun
 */
public class CreditsScene {

    private final Pane creditsPane;
    private Runnable onBack;

    // set up the credits screen with the pane to add everything to
    public CreditsScene(Pane creditsPane) {
        this.creditsPane = creditsPane;
        setupCredits();
    }

    // build all the visual elements for the credits screen
    private void setupCredits() {

        // dark background overlay
        javafx.scene.shape.Rectangle background = new javafx.scene.shape.Rectangle(1024, 768);
        background.setFill(Color.rgb(0, 0, 0, 0.90));

        // credits title
        Label title = new Label("CREDITS");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        title.setTextFill(Color.CYAN);
        title.setLayoutX(0);
        title.setPrefWidth(1024);
        title.setAlignment(javafx.geometry.Pos.CENTER);

        // team members and student numbers
        Label member1 = new Label("Miguel Cofre Arce — Lead Developer");
        Label member2 = new Label("Victoria Adeosun — HUD & Score Manager");
        Label member3 = new Label("Deliwe Mufute — Player");
        Label member4 = new Label("Emmanuel Ayobanjo — Scenery Manager");
        Label member5 = new Label("Paakhi Agrawala — Obstacle Manager");

        // style all the member labels
        Label[] members = {member1, member2, member3, member4, member5};
        double startY = 220;
        for (Label member : members) {
            member.setFont(Font.font("Arial", 28));
            member.setTextFill(Color.WHITE);
            member.setLayoutX(150);
            member.setPrefWidth(724);
            member.setAlignment(javafx.geometry.Pos.CENTER);
            member.setLayoutY(startY);
            startY += 70;
        }

        // back button using the existing exit button asset
        Image backBtnImage = new Image(getClass().getResourceAsStream("/Menu_assets/exit_button.png"));
        ImageView backBtn = new ImageView(backBtnImage);
        backBtn.setFitWidth(200);
        backBtn.setFitHeight(60);
        backBtn.setLayoutX(412);
        backBtn.setLayoutY(650);

        // go back to menu when clicked
        backBtn.setOnMouseClicked(e -> {
            if (onBack != null) onBack.run();
        });

        // make it look clickable on hover
        backBtn.setOnMouseEntered(e -> backBtn.setOpacity(0.8));
        backBtn.setOnMouseExited(e -> backBtn.setOpacity(1.0));

        // add everything to the pane
        creditsPane.getChildren().addAll(background, title, member1, member2, member3, member4, member5, backBtn);
    }

    // lets Main pass in what happens when back is clicked
    public void setOnBack(Runnable onBack) {
        this.onBack = onBack;
    }

    // returns the pane so it can be added to root
    public Pane getCreditsPane() {
        return creditsPane;
    }
}