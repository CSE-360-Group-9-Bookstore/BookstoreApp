package bookstore.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import bookstore.lib.Listings;
import bookstore.lib.Logs;
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
    private Button buyButton; // Reference to the Buy button

    @FXML
    private void initialize() {
        // Fetch all listings initially and load them into the ListView
        allListings = fetchAllListings();
        refreshListingView();

        // Listener to display details when a listing is selected and show the Buy button
        listingIdList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayListingDetails(getSelectedListingUUID());
                buyButton.setVisible(true); // Show the button when a new item is selected
            }
        });
    }

    // Fetch all listings from the database
    private Map<UUID, Listings.Listing> fetchAllListings() {
        Map<UUID, Listings.Listing> updatedListings = listings.getAll();
        System.out.println("Fetched listings: " + updatedListings); // Debugging output
        return updatedListings;
    }

    // Refresh ListView based on the latest state of allListings
    private void refreshListingView() {
        ObservableList<String> listingDisplayItems = FXCollections.observableArrayList();
        for (Map.Entry<UUID, Listings.Listing> entry : allListings.entrySet()) {
            Listings.Listing listing = entry.getValue();
            String displayText = listing.bookTitle + " - $" + listing.sellPrice + " - Stock: " + listing.quantity;
            listingDisplayItems.add(displayText);
        }
        System.out.println("Updated ListView items: " + listingDisplayItems); // Debug output
        listingIdList.setItems(listingDisplayItems);
        listingIdList.refresh(); // Force refresh to ensure UI updates
    }

    @FXML
    private void handlePurchase() {
        // Get the selected UUID from the ListView
        UUID selectedUUID = getSelectedListingUUID();

        if (selectedUUID != null) {
            // Attempt the purchase action
            listings.purchaseBook(selectedUUID);

            // Re-fetch the listings to reflect changes and update the ListView
            allListings = fetchAllListings(); // Fetch updated listings
            refreshListingView(); // Refresh the ListView

            // Check if the listing still exists after re-fetching
            Listings.Listing listing = allListings.get(selectedUUID);
            if (listing != null) {
                // Display confirmation with book name and remaining stock
                String confirmationMessage = String.format("You bought '%s'. Remaining stock: %d.", listing.bookTitle, listing.quantity);
                messageLabel.setText(confirmationMessage);
            } else {
                // If listing was removed due to zero stock
                messageLabel.setText("The selected book is now out of stock and has been removed.");
            }

            // Hide the Buy button after a purchase
            buyButton.setVisible(false);

            // Log the transaction if the listing still exists
            if (listing != null) {
                Logs.logPurchase(listing.listingUUID, listing.sellerUUID, listing.sellPrice, listing.buyPrice, listing.bookTitle);
            }
        } else {
            messageLabel.setText("Error: No book selected.");
        }
    }

    // Helper method to get the selected listing's UUID from ListView
    private UUID getSelectedListingUUID() {
        String selectedItem = listingIdList.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String title = selectedItem.split(" - ")[0];
            return allListings.values().stream()
                    .filter(listing -> listing.bookTitle.equals(title))
                    .map(listing -> listing.listingUUID)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    // Display detailed information for the selected listing
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