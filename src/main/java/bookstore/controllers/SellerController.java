package bookstore.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import bookstore.lib.*;  // Import all classes from bookstore.lib
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;

public class SellerController {

    Listings listings = new Listings();

    @FXML
    private Label messageLabel;

    @FXML
    private Button randomButton;

    @FXML
    private void initialize() {

        // Get the current user's UUID from the Session
        User currentUser = Session.getInstance().getUser();
        UUID sellerUUID = currentUser.user_uuid;

        // Initialize Statistics and retrieve stats for the seller UUID
        Statistics stats = new Statistics();

        // Print statistics for the past 1 day, 7 days, and 30 days using helper method
        System.out.println("Sales statistics for seller " + sellerUUID);
        displayStats(sellerUUID, stats, 1);
        displayStats(sellerUUID, stats, 7);
        displayStats(sellerUUID, stats, 30);

        // Define filter parameters for displaying listings in the messageLabel
        List<String> genres = Arrays.asList("Hardcover"); // Example genres to filter
        List<String> conditions = Arrays.asList("New"); // Example types to filter
        Double minSellPrice = 10.0; // Minimum sell price
        Double maxSellPrice = 50.0; // Maximum sell price
        String search = "Harper"; // Search keyword

        // New parameters for ISBN-10, ISBN-13, and sellerID (set as null for now)
        Long isbn10 = null;
        Long isbn13 = null;
        UUID sellerID = null;

        // Fetch filtered listings and prepare message
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

        StringBuilder messageText = new StringBuilder();

        for (Map.Entry<UUID, Listings.Listing> entry : filteredListings.entrySet()) {
            UUID listingId = entry.getKey();
            String bookTitle = entry.getValue().bookTitle;
            messageText.append(listingId).append(": ").append(bookTitle).append("\n");
        }

        // Set the messageLabel text with filtered listing ID and title pairs
        messageLabel.setText(messageText.toString().trim()); // Remove trailing newline
    }

    /**
     * Helper method to display statistics for a given number of days.
     */
    private void displayStats(UUID sellerUUID, Statistics stats, int days) {
        Map<String, Double> salesStats = stats.getSalesStats(sellerUUID, days);
        if (salesStats != null) {
            System.out.println("Past " + days + " days:");
            System.out.println("  Total Sales: " + salesStats.get("totalSales"));
            System.out.println("  Total Cost: " + salesStats.get("totalCost"));
            System.out.println("  Total Profit: " + salesStats.get("totalProfit"));
        } else {
            System.out.println("Error retrieving stats for past " + days + " days.");
        }
    }

    @FXML
    private void handleRandomAction() {
        messageLabel.setText("You clicked the button! Something random happened.");
    }
}