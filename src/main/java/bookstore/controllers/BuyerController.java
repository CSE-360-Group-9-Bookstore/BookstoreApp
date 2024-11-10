package bookstore.controllers;

import javafx.fxml.FXML;

import javafx.scene.control.ComboBox;

import javafx.scene.control.Button;

import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import bookstore.lib.Listings;
import bookstore.lib.Logs;
import bookstore.lib.Session; // Import Session
import bookstore.lib.User;    // Import User
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;

public class BuyerController {

    Listings listings = new Listings();
     ObservableList<String> listingDisplayItems = FXCollections.observableArrayList();
    private Map<UUID, Listings.Listing> allListings;
    private Map<String, UUID> displayTextToUUIDMap = new HashMap<>(); // Map for display text to UUID

    @FXML
    private Label messageLabel;

    @FXML
    private ListView<String> listingIdList;

    @FXML
    private ComboBox<String> genreCB;

    @FXML
    private ComboBox<String> conCB;

    private Button buyButton; // Reference to the Buy button


    @FXML
    private void initialize() {
        // Fetch all listings initially and load them into the ListView
        allListings = fetchAllListings();
        refreshListingView();


        for (Map.Entry<UUID, Listings.Listing> entry : allListings.entrySet()) {
            Listings.Listing listing = entry.getValue();
            String displayText = listing.bookTitle + " - $" + listing.sellPrice;
            listingDisplayItems.add(displayText);
        }

        // Populate ListView with book titles and prices
        listingIdList.setItems(listingDisplayItems);

        //genres get put in combo box hopefully.
        ObservableList<String> genres = FXCollections.observableArrayList(
                "Natural Science Books", "Computer Books", "Math Books", "English Language Books", "Other Books"
        );
        //sets the items above into the box hopefullty
        genreCB.setItems(genres);

        ObservableList<String> condition = FXCollections.observableArrayList(
                "Used Like New", "Moderately Used", "Heavily Used"
        );
        conCB.setItems(condition);

        listingIdList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayListingDetails(getSelectedListingUUID());
                buyButton.setVisible(true); // Show the button when a new item is selected
            }
        });
    }

    // Fetch all listings with optional filtering
    private Map<UUID, Listings.Listing> fetchAllListings() {
        // Define default filter parameters (null or empty for no filtering)
        List<String> genres = null;
        List<String> conditions = null;
        Double minSellPrice = null;
        Double maxSellPrice = null;
        String search = null;

        // Additional filter parameters for ISBN-10, ISBN-13, and sellerID
        Long isbn10 = null;
        Long isbn13 = null;
        UUID sellerID = null;

        // Call filterAll with the defined parameters
        Map<UUID, Listings.Listing> filteredListings = listings.filterAll(
                genres,
                conditions,
                minSellPrice,
                maxSellPrice,
                search,
                isbn10,
                isbn13,
                sellerID
        );

        System.out.println("Fetched listings with filters: " + filteredListings); // Debugging output
        return filteredListings;
    }

    // Refresh ListView based on the latest state of allListings
    private void refreshListingView() {
       
        displayTextToUUIDMap.clear(); // Clear the mapping before repopulating

        for (Map.Entry<UUID, Listings.Listing> entry : allListings.entrySet()) {
            Listings.Listing listing = entry.getValue();
            String displayText = listing.bookTitle + " - $" + listing.sellPrice + " - Stock: " + listing.quantity;
            listingDisplayItems.add(displayText);
            displayTextToUUIDMap.put(displayText, listing.listingUUID); // Map display text to UUID
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
                // Get the current user's UUID from the Session
                User currentUser = Session.getInstance().getUser();
                UUID buyerUUID = currentUser.user_uuid;

                Logs.logPurchase(
                        listing.listingUUID,
                        buyerUUID,
                        listing.sellerUUID,
                        listing.sellPrice,
                        listing.buyPrice,
                        listing.bookTitle
                );
            }
        } else {
            messageLabel.setText("Error: No book selected.");
        }
    }

    // Helper method to get the selected listing's UUID from ListView
    private UUID getSelectedListingUUID() {
        String selectedItem = listingIdList.getSelectionModel().getSelectedItem();
        return displayTextToUUIDMap.get(selectedItem); // Retrieve UUID from the map
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