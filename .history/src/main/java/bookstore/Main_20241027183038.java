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
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import java.net.URL;
import java.util.*;

public class Main extends Application {

    // Font for UI components
    private static final Font FONT = Font.font("Arial", 14);
    private static final Font WELCOME_FONT = Font.font("Arial", 20);

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
    private Users users; // Declare Users instance at class level
    private String loggedInUsername; // Store the logged-in username

    @Override
    public void start(Stage primaryStage) {
        users = new Users(); // Initialize Users instance

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
        Label additionalText = new Label("Enter your username and password.");
        additionalText.setTextFill(ColorConfig.ADDITIONAL_TEXT);

        // Add event handler to submit button
        submitButton.setOnAction(event -> {
            // Change the button text to indicate logging in
            submitButton.setText("Logging you in...");
            submitButton.setDisable(true); // Optionally disable the button to prevent multiple clicks

            String username = usernameField.getText();
            String password = passwordField.getText();

            // Call the authenticateUser method from Users class
            String[] result = users.authenticateUser(username, password);

            // Revert the button text after authentication
            submitButton.setText("Login");
            submitButton.setDisable(false); // Re-enable the button

            if ("error".equals(result[0])) {
                // Failed login, show error message
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText(result[1]); // Display the error message from Users class
                alert.showAndWait();
            } else {
                // Successful login
                loggedInUsername = username; // Store the logged-in username
                accessiblePages = Arrays.asList(result);
                createMainLayout(); // Initialize the main layout
                primaryStage.setScene(mainScene);
            }
        });

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.getChildren().addAll(usernameField, passwordField, submitButton, additionalText);
        root.setStyle(ColorConfig.getBackgroundStyle());

        return new Scene(root, 900, 550);
    }

    private void createMainLayout() {
        Node topBar = createTopBar();

        mainLayout = new BorderPane();
        mainLayout.setTop(topBar);
        mainLayout.setStyle(ColorConfig.getBackgroundStyle());

        if (!accessiblePages.isEmpty()) {
            loadCenterContent(accessiblePages.get(0));
        }
        mainScene = new Scene(mainLayout, 900 * 1.38, 550 * 1.38);
    }

    private Node createTopBar() {
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setStyle(ColorConfig.getTopBarStyle());

        for (String pageName : accessiblePages) {
            final String currentPage = pageName; // Capture the pageName
            Button pageButton = createStyledButton(currentPage);
            pageButton.setOnAction(event -> {
                loadCenterContent(currentPage);
            });
            topBar.getChildren().add(pageButton);
        }

        Region leftSpacer = new Region();
        Region rightSpacer = new Region();
        HBox.setHgrow(leftSpacer, Priority.ALWAYS);
        HBox.setHgrow(rightSpacer, Priority.ALWAYS);
        topBar.getChildren().add(leftSpacer);

        Label welcomeLabel = new Label("Welcome, " + loggedInUsername + "!");
        welcomeLabel.setFont(WELCOME_FONT);
        welcomeLabel.setStyle("-fx-font-weight: bold;");
        welcomeLabel.setTextFill(Color.web("#FFFFFF"));
        topBar.getChildren().add(welcomeLabel);
        topBar.getChildren().add(rightSpacer);

        Button backToLoginButton = createStyledButton("Change User (Current: " + loggedInUsername + ")");
        backToLoginButton.setOnAction(event -> {
            primaryStage.setScene(loginScene);
        });
        topBar.getChildren().add(backToLoginButton);

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


    private TextField createStyledTextField(String promptText) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setStyle(ColorConfig.getTextFieldStyle());
        textField.setFont(FONT);
        return textField;
    }


    private PasswordField createStyledPasswordField(String promptText) {
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText(promptText);
        passwordField.setStyle(ColorConfig.getTextFieldStyle());
        passwordField.setFont(FONT);
        return passwordField;
    }


    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setStyle(ColorConfig.getButtonStyle());
        button.setFont(FONT);
        return button;
    }

    public static void main(String[] args) {
        launch(args);
    }
}