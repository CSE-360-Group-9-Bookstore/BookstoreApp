package bookstore.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private ListView<String> listingIdList;

    @FXML
    private void initialize() {
        // Fetch all listings and add book title with sell price to the ListView
        Map<UUID, Listings.Listing> allListings = listings.getAll();
        ObservableList<String> listingDisplayItems = FXCollections.observableArrayList();

        for (Map.Entry<UUID, Listings.Listing> entry : allListings.entrySet()) {
            Listings.Listing listing = entry.getValue();
            String displayText = listing.bookTitle + " - $" + listing.sellPrice;
            listingDisplayItems.add(displayText);
        }

        // Populate ListView with book titles and prices
        listingIdList.setItems(listingDisplayItems);

        // Add a click listener to display all details for the selected listing
        listingIdList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Find the corresponding listing by matching the title and price
                Listings.Listing selectedListing = allListings.values().stream()
                        .filter(listing -> (listing.bookTitle + " - $" + listing.sellPrice).equals(newValue))
                        .findFirst()
                        .orElse(null);

                if (selectedListing != null) {
                    // Display all details of the selected listing
                    String listingDetails = "Book Title: " + selectedListing.bookTitle + "\n" +
                            "Author: " + selectedListing.author + "\n" +
                            "Description: " + selectedListing.description + "\n" +
                            "ISBN-10: " + selectedListing.ISBN10 + "\n" +
                            "ISBN-13: " + selectedListing.ISBN13 + "\n" +
                            "Genre: " + selectedListing.genre + "\n" +
                            "Condition: " + selectedListing.condition + "\n" +
                            "Buy Price: $" + selectedListing.buyPrice + "\n" +
                            "Sell Price: $" + selectedListing.sellPrice + "\n" +
                            "Seller ID: " + selectedListing.sellerUUID + "\n" +
                            "Quantity: " + selectedListing.quantity + "\n" +
                            "Status: " + selectedListing.status;
                    messageLabel.setText(listingDetails);
                }
            }
        });
    }

    @FXML
    private void handleRandomAction() {
        messageLabel.setText("You clicked the button! Something random happened.");
    }
}