package bookstore;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public class Main extends Application {

    // Constants for UI customization
    private static final String BUTTON_STYLE = "-fx-background-color: #444; -fx-text-fill: white; -fx-background-radius: 5;";
    private static final String TEXTFIELD_STYLE = "-fx-background-color: #333; -fx-text-fill: white; -fx-prompt-text-fill: #aaa;";
    private static final String BACKGROUND_STYLE = "-fx-background-color: #222;";
    private static final Font FONT = Font.font("Arial", 14);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Add Name to Database");

        // First Name Field
        TextField firstNameField = createStyledTextField("Enter first name");

        // Last Name Field
        TextField lastNameField = createStyledTextField("Enter last name");

        // Add Name Button
        Button addButton = createStyledButton("Add Name");

        // Database connection (assuming you have this class)
        testDB db = new testDB();

        // Event handler for adding the name to the database
        addButton.setOnAction(event -> {
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();

            // Add name to database
            db.addName(firstName, lastName);

            // Clear input fields
            firstNameField.clear();
            lastNameField.clear();
        });

        // Layout and spacing
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(firstNameField, lastNameField, addButton);
        root.setStyle(BACKGROUND_STYLE);

        Scene scene = new Scene(root, 350, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Method to create styled TextField
    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle(TEXTFIELD_STYLE);
        textField.setFont(FONT);
        return textField;
    }

    // Method to create styled Button
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(BUTTON_STYLE);
        button.setFont(FONT);
        button.setMaxWidth(Double.MAX_VALUE);  // Make button expand horizontally
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
