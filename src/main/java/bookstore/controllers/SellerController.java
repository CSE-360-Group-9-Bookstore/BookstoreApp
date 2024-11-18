package bookstore.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import bookstore.lib.*;  // Import all classes from bookstore.lib
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import javafx.collections.ObservableList;

public class SellerController {

    Listings listings = new Listings();
    Users users = new Users(); // Add Users instance

    @FXML
    private Label messageLabel;

    @FXML
    private Button randomButton;

    @FXML
    private Label sellerIDLabel; // Changed from TextField to Label

    @FXML
    private ComboBox<String> duration;

    @FXML
    private TextField salesQuantity;

    @FXML
    private TextField unit;

    @FXML
    private TextField salesRevenue;

    @FXML
    private TextField netProfit;

    @FXML
    private ListView<String> currentOfferings;

    @FXML
    private TextField bookTitle;

    @FXML
    private TextField author;

    @FXML
    private TextArea description;

    @FXML
    private TextField isbn10;

    @FXML
    private TextField isbn13;

    @FXML
    private TextField bookType;

    @FXML
    private TextField quantity;

    @FXML
    private TextField condition;

    @FXML
    private TextField price;

    @FXML
    private Button updateListingButton;

    @FXML
    private Button addNewListingButton;

    @FXML
    private void initialize() {
        // Initialize ComboBox with duration options
        duration.setItems(FXCollections.observableArrayList(
                "1 month", "2 months", "3 months", "6 months", "12 months"
        ));

        // Get the current user's UUID from the Session
        User currentUser = Session.getInstance().getUser();
        UUID sellerUUID = currentUser.user_uuid;

        // Set the seller ID label with the username
        sellerIDLabel.setText(users.getUsernameById(sellerUUID));

        // Fetch and display current offerings
        List<String> bookNames = getSellerCurrentOfferings(sellerUUID);
        currentOfferings.setItems(FXCollections.observableArrayList(bookNames));

        // Initialize Statistics and retrieve stats for the seller UUID
        Statistics stats = new Statistics();

        // Display individual seller statistics
        displayStats(sellerUUID, stats, 1);
        displayStats(sellerUUID, stats, 7);
        displayStats(sellerUUID, stats, 30);

        // Display overall statistics for all users
        displayAllStats(stats, 30);

        // Display the bestseller for the past 30 days
        String bestSeller = stats.getBestSeller(30);
        System.out.println("Best-selling book in the past 30 days: " + (bestSeller != null ? bestSeller : "No data"));

        // Display sales stats for all sellers
        displayAllSellerStats(stats, 30);

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
     * Fetch and return the list of book titles for the current offerings of the seller.
     */
    private List<String> getSellerCurrentOfferings(UUID sellerUUID) {
        List<Listings.Listing> sellerListings = listings.getSellerCurrentOfferings(sellerUUID);
        List<String> bookNames = new ArrayList<>();
        for (Listings.Listing listing : sellerListings) {
            bookNames.add(listing.bookTitle);
        }
        return bookNames;
    }

    /**
     * Helper method to display statistics for a given number of days for a specific seller.
     */
    private void displayStats(UUID sellerUUID, Statistics stats, int days) {
        Map<String, Double> salesStats = stats.getSalesStats(sellerUUID, days);
        if (salesStats != null) {
            System.out.println("Past " + days + " days for seller:");
            System.out.println("  Total Sales: " + salesStats.get("totalSales"));
            System.out.println("  Total Cost: " + salesStats.get("totalCost"));
            System.out.println("  Total Profit: " + salesStats.get("totalProfit"));
        } else {
            System.out.println("Error retrieving stats for past " + days + " days.");
        }
    }

    /**
     * Helper method to display overall statistics for all users.
     */
    private void displayAllStats(Statistics stats, int days) {
        Map<String, Double> allStats = stats.getAllStats(days);
        if (allStats != null) {
            System.out.println("Overall stats for the past " + days + " days:");
            System.out.println("  Total Sales: " + allStats.get("totalSales"));
            System.out.println("  Total Cost: " + allStats.get("totalCost"));
            System.out.println("  Total Profit: " + allStats.get("totalProfit"));
        } else {
            System.out.println("Error retrieving overall stats for past " + days + " days.");
        }
    }


    private void displayAllSellerStats(Statistics stats, int days) {
        Map<UUID, Map<String, Double>> allSellerStats = stats.getAllSellerStats(days);
        if (allSellerStats != null) {
            System.out.println("Sales stats for all sellers in the past " + days + " days:");
            for (Map.Entry<UUID, Map<String, Double>> entry : allSellerStats.entrySet()) {
                UUID sellerUUID = entry.getKey();
                Map<String, Double> sellerStats = entry.getValue();
                System.out.println("Seller UUID: " + sellerUUID);
                System.out.println("  Total Sales: " + sellerStats.get("totalSales"));
                System.out.println("  Total Cost: " + sellerStats.get("totalCost"));
                System.out.println("  Total Profit: " + sellerStats.get("totalProfit"));
            }
        } else {
            System.out.println("Error retrieving stats for all sellers for past " + days + " days.");
        }
    }

    @FXML
    private void handleRandomAction() {
        messageLabel.setText("You clicked the button! Something random happened.");
    }
}
