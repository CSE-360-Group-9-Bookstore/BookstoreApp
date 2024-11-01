package bookstore;

import bookstore.lib.*;
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
    private static final Font FONT = Font.font("Arial", 14);
    private static final Font WELCOME_FONT = Font.font("Arial", 20);

    private Scene loginScene;
    private Scene mainScene;
    private BorderPane mainLayout;
    private Stage primaryStage;
    private static final Map<String, String> pageMap = new HashMap<>();
    static {
        pageMap.put("buyer", "buyerLanding.fxml");
        pageMap.put("seller", "sellerLanding.fxml");
        pageMap.put("admin", "adminLanding.fxml");
    }
   
    private Users users;
    private User client;

    @Override
    public void start(Stage primaryStage) {
        users = new Users();
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Login");

        loginScene = createLoginScene();

        primaryStage.setScene(loginScene);
        primaryStage.show();
    }

    private Scene createLoginScene() {

        TextField usernameField = createStyledTextField("Enter username");
        PasswordField passwordField = createStyledPasswordField("Enter password");
        usernameField.setText("admin");
        passwordField.setText("password");
        Button submitButton = createStyledButton("Login");
        Label additionalText = new Label("Enter your username and password." +
                "\n Testing Account: " +
                "\n Username: rohan (case sensitive)" +
                "\n Password: password (case sensitive)");
        additionalText.setTextFill(ColorConfig.ADDITIONAL_TEXT);
        submitButton.setOnAction(event -> {
            submitButton.setText("Logging you in...");
            submitButton.setDisable(true);
            String username = usernameField.getText();
            String password = passwordField.getText();
            String[] result = users.authenticateUser(username, password);
            submitButton.setText("Login");
            submitButton.setDisable(false);

            if ("error".equals(result[0])) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Login Failed");
                alert.setHeaderText(null);
                alert.setContentText(result[1]);
                alert.showAndWait();
            } else {
                client.username = username;

                createMainLayout();
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
            final String currentPage = pageName;
            Button pageButton = createStyledButton(currentPage);
            pageButton.setOnAction(event -> {
                loadCenterContent(currentPage);
            });
            topBar.getChildren().add(pageButton);
        }*/

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