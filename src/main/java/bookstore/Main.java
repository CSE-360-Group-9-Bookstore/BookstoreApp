package bookstore;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;


public class Main extends Application {

    // Constants for UI customization
    private static final String BUTTON_STYLE = "-fx-background-color: #444; -fx-text-fill: white; -fx-background-radius: 5;";
    private static final String TEXTFIELD_STYLE = "-fx-background-color: #333; -fx-text-fill: white; -fx-prompt-text-fill: #aaa;";
    private static final String BACKGROUND_STYLE = "-fx-background-color: #222;";
    private static final Font FONT = Font.font("Arial", 14);

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Login");

        // Create the login scene
        Scene loginScene = createLoginScene(primaryStage);

        // Set the scene to login scene
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private Scene createLoginScene(Stage primaryStage) {
        // Create username and password fields
        TextField usernameField = createStyledTextField("Enter username");
        PasswordField passwordField = createStyledPasswordField("Enter password");

        // Create submit button
        Button submitButton = createStyledButton("Login");

        // Create a label for additional text below the button
        // (e.g., "Forgot your password?")
        Label additionalText = new Label("For now: Username = admin, Password = password");
        additionalText.setTextFill(Color.WHITE);


        // Add event handler to submit button
        submitButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            String[] result = authenticateUser(username, password);
            if (result.length > 0) {
                // Successful login
                Scene mainScene = createMainScene(primaryStage);
                primaryStage.setScene(mainScene);
            } else {
                // Failed login, stay on login page
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText("Invalid username or password.");
                alert.showAndWait();
            }
        });

        // Layout
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(usernameField, passwordField, submitButton, additionalText);
        root.setStyle(BACKGROUND_STYLE);

        return new Scene(root, 350, 250);  // Adjusted height to accommodate new text
    }


    private Scene createMainScene(Stage primaryStage) {
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

        // Back to Login button
        Button backButton = createStyledButton("Back to Login");
        backButton.setOnAction(event -> {
            // Go back to login scene
            Scene loginScene = createLoginScene(primaryStage);
            primaryStage.setScene(loginScene);
        });

        // Layout and spacing
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(firstNameField, lastNameField, addButton, backButton);
        root.setStyle(BACKGROUND_STYLE);

        return new Scene(root, 350, 250);
    }

    // Method to create styled TextField
    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle(TEXTFIELD_STYLE);
        textField.setFont(FONT);
        return textField;
    }

    // Method to create styled PasswordField
    private PasswordField createStyledPasswordField(String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setStyle(TEXTFIELD_STYLE);
        passwordField.setFont(FONT);
        return passwordField;
    }

    // Method to create styled Button
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(BUTTON_STYLE);
        button.setFont(FONT);
        button.setMaxWidth(Double.MAX_VALUE);  // Make button expand horizontally
        return button;
    }

    // AuthenticateUser method
    private String[] authenticateUser(String username, String password) {

        // Hardcoded authentication for testing purposes
        System.out.println("Username: " + username);
        System.out.println("Password: " + password);
        if ("admin".equals(username) && "password".equals(password)) {
            return new String[]{"hello", "world"};
        } else {
            return new String[0]; // empty array
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
