package bookstore;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.layout.HBox;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.net.URL;
import java.util.*;

public class Main extends Application {

    // Constants for UI customization
    private static final String BUTTON_STYLE = "-fx-background-color: #444; -fx-text-fill: white; -fx-background-radius: 5;";
    private static final String TEXTFIELD_STYLE = "-fx-background-color: #333; -fx-text-fill: white; -fx-prompt-text-fill: #aaa;";
    private static final String BACKGROUND_STYLE = "-fx-background-color: #222;";
    private static final Font FONT = Font.font("Arial", 14);

    private Scene loginScene;
    private Scene mainScene;
    private BorderPane mainLayout;
    private Stage primaryStage; // Keep a reference to the primaryStage

    // Map of page names to FXML files
    private static final Map<String, String> pageMap = new HashMap<>();
    static {
        pageMap.put("testing", "testing.fxml");
        pageMap.put("page2", "page2.fxml");
        // Add new pages here
        // Example: pageMap.put("newPage", "newPage.fxml");
    }

    private List<String> accessiblePages; // List of pages accessible to the logged-in user

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage; // Assign the primaryStage
        primaryStage.setTitle("Login");

        // Create the login scene
        loginScene = createLoginScene();

        // Set the scene to login scene
        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private Scene createLoginScene() {
        // Create username and password fields
        TextField usernameField = createStyledTextField("Enter username");
        PasswordField passwordField = createStyledPasswordField("Enter password");
        usernameField.setText("admin");  // Preload with "admin"
        passwordField.setText("password");  // Preload with "password"

        // Create submit button
        Button submitButton = createStyledButton("Login");

        // Create a label for additional text below the button
        Label additionalText = new Label("For now: Username = admin, Password = password \n \n Filtered Pages: \n         username: page2user password = password \n         username: testing password:password");
        additionalText.setTextFill(Color.WHITE);

        // Add event handler to submit button
        submitButton.setOnAction(event -> {
            String username = usernameField.getText();
            String password = passwordField.getText();

            String[] result = authenticateUser(username, password);
            if (result.length > 0) {
                // Successful login
                accessiblePages = Arrays.asList(result);
                createMainLayout(); // Initialize the main layout
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

    private void createMainLayout() {
        // Create the top bar with buttons
        Node topBar = createTopBar();

        // Create the main layout
        mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setStyle(BACKGROUND_STYLE);

        // Load the first accessible page into the center, or leave blank if none
        if (!accessiblePages.isEmpty()) {
            loadCenterContent(accessiblePages.get(0));
        }

        // Create the main scene
        mainScene = new Scene(mainLayout, 600, 400);
    }

    private Node createTopBar() {
        Button backToLoginButton = createStyledButton("Back to Login");
        backToLoginButton.setOnAction(event -> {
            primaryStage.setScene(loginScene);
        });

        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.getChildren().add(backToLoginButton);
        // Darker background for the top bar
        topBar.setStyle("-fx-background-color: #111;");

        // Create buttons for accessible pages
        for (String pageName : accessiblePages) {
            final String currentPage = pageName; // Capture the pageName
            Button pageButton = createStyledButton(currentPage);
            pageButton.setOnAction(event -> {
                loadCenterContent(currentPage);
            });
            topBar.getChildren().add(pageButton);
        }

        return topBar;
    }

    private void loadCenterContent(String pageName) {
        try {
            String fxmlFile = pageMap.get(pageName);
            if (fxmlFile == null) {
                throw new RuntimeException("No FXML file mapped for page: " + pageName);
            }
            URL resourceURL = getClass().getResource("/" + fxmlFile);
            System.out.println("Loading FXML from: " + resourceURL);
            if (resourceURL == null) {
                throw new RuntimeException("FXML file not found: " + fxmlFile);
            }
            Parent content = FXMLLoader.load(resourceURL);
            mainLayout.setCenter(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return button;
    }

    // AuthenticateUser method
    private String[] authenticateUser(String username, String password) {
        // Hardcoded authentication for testing purposes
        if ("admin".equals(username) && "password".equals(password)) {
            return new String[]{"testing", "page2"};
        } else if ("page2user".equals(username) && "password".equals(password)) {
            return new String[]{"page2"};
        }
        else if ("testing".equals(username) && "password".equals(password)) {
            return new String[]{"testing"};
        }
        else {
            return new String[0]; // empty array
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
