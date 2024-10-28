package bookstore;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class Page2Controller {

    @FXML
    private Label messageLabel;

    @FXML
    private Button randomButton;

    @FXML
    private void initialize() {
        // Set initial text
        messageLabel.setText("Welcome to Page 2!");
    }

    @FXML
    private void handleRandomAction() {
        // Do something random, like changing the text
        messageLabel.setText("You clicked the button! Something random happened.");
    }
}
