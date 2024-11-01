package bookstore;

import bookstore.lib.dborm;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;

public class BSController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private Button addButton;
    private dborm db = new dborm();

    @FXML
    private void initialize() {
    }

    @FXML
    private void handleAddName() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();

        db.addName(firstName, lastName);
        firstNameField.clear();
        lastNameField.clear();
    }
}
