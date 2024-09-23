package bookstore;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class TestingController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Button addButton;

    // Database connection (assuming you have this class)
    private testDB db = new testDB();

    @FXML
    private void initialize() {
        // Any initialization code can go here
    }

    @FXML
    private void handleAddName() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        // Add name to database
        db.addName(firstName, lastName);

        // Clear input fields
        firstNameField.clear();
        lastNameField.clear();
    }
}
