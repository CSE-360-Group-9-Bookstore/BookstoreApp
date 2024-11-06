package bookstore.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import bookstore.lib.*;
import java.util.Map;
import java.util.UUID;

public class BuyerController {

    Listings listings = new Listings();
    private Map<UUID, Listings.Listing> allListings;

    @FXML
    private Label messageLabel;

    @FXML
    private Button randomButton;

    @FXML
    private ListView<String> listingIdList;

    @FXML
    private void initialize() {
        // Fetch all listings and add book title with sell price to the ListView
        allListings = listings.getAll();
        updateListView();
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
}
