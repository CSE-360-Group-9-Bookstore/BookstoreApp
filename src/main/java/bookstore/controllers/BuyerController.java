package bookstore.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import bookstore.lib.Listings;
import java.util.Map;
import java.util.UUID;

public class BuyerController {

    Listings listings = new Listings();
    private Map<UUID, Listings.Listing> allListings;

    @FXML
    private Label messageLabel;

    @FXML
    private ListView<String> listingIdList;

    @FXML
    private void initialize() {
        // Fetch all listings and add book title with sell price to the ListView
        allListings = listings.getAll();
        updateListView();

        // Add a click listener to display all details for the selected listing
        listingIdList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayListingDetails(getSelectedListingUUID());
            }
        });
    }

    // Method to update the ListView after purchase or changes
    private void updateListView() {
        ObservableList<String> listingDisplayItems = FXCollections.observableArrayList();
        for (Map.Entry<UUID, Listings.Listing> entry : allListings.entrySet()) {
            Listings.Listing listing = entry.getValue();
            String displayText = listing.bookTitle + " - $" + listing.sellPrice + " - Stock: " + listing.quantity;
            listingDisplayItems.add(displayText);
        }
        listingIdList.setItems(listingDisplayItems);
    }

    @FXML
    private void handleRandomAction() {
        // Get the selected UUID from the ListView (assumes ListView displays book titles with prices)
        UUID selectedUUID = getSelectedListingUUID();

        if (selectedUUID != null) {
            // Trigger the purchase logic
            String result = listings.purchaseBook(selectedUUID);
            messageLabel.setText(result);

            // Re-fetch the listings and update ListView to reflect changes
            allListings = listings.getAll();
            updateListView();
            // Display details after purchase
            displayListingDetails(selectedUUID);
        } else {
            messageLabel.setText("Error: No book selected.");
        }
    }

    // Helper method to get the selected listing's UUID from ListView (extract UUID based on the title)
    private UUID getSelectedListingUUID() {
        String selectedItem = listingIdList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Extract the book title and match it with the listing in the system
            String title = selectedItem.split(" - ")[0];
            return allListings.values().stream()
                    .filter(listing -> listing.bookTitle.equals(title))
                    .map(listing -> listing.listingUUID)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    // Method to display detailed listing information
    private void displayListingDetails(UUID listingUUID) {
        Listings.Listing selectedListing = allListings.get(listingUUID);
        if (selectedListing != null) {
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
}
