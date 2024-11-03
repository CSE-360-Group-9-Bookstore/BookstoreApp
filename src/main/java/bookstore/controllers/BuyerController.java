package bookstore.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import bookstore.lib.Listings;
import java.util.Map;
import java.util.UUID;

public class BuyerController {

    Listings listings = new Listings();

    @FXML
    private Label messageLabel;

    @FXML
    private Button randomButton;

    @FXML
    private void initialize() {
        // Fetch all listings and prepare message
        Map<UUID, Listings.Listing> allListings = listings.getAll();
        StringBuilder messageText = new StringBuilder();

        for (Map.Entry<UUID, Listings.Listing> entry : allListings.entrySet()) {
            UUID listingId = entry.getKey();
            String bookTitle = entry.getValue().bookTitle;
            messageText.append(listingId).append(": ").append(bookTitle).append("\n");
        }

        // Set the messageLabel text with listing ID and title pairs
        messageLabel.setText(messageText.toString().trim());  // Remove trailing newline
    }

    @FXML
    private void handleRandomAction() {
        messageLabel.setText("You clicked the button! Something random happened.");
    }
}