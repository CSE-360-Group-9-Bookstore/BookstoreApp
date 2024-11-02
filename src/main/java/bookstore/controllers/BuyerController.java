package bookstore.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

import bookstore.lib.*;
import java.util.UUID;

public class BuyerController {

    Listings listings = new Listings();

    // Use Listings.Listing to refer to the inner class Listing
    Listings.Listing firstBook = listings.getListingInfo(UUID.fromString("2321ec8a-0b23-4740-87e3-02f2255b3789"));

    String bookTitle = firstBook.bookTitle;

    @FXML
    private Label messageLabel;

    @FXML
    private Button randomButton;

    @FXML
    private void initialize() {
        messageLabel.setText("Welcome to Page 2!, the name of the first book in the database is " + bookTitle);
    }

    @FXML
    private void handleRandomAction() {
        messageLabel.setText("You clicked the button! Something random happened.");
    }
}