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
import javafx.scene.input.MouseEvent;
import bookstore.lib.Listings.Listing;



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
    private Label  salesQuantityLabel;

    @FXML
    private TextField unit;

    @FXML
    private Label  salesRevenueLabel;

    @FXML
    private Label  netProfitLabel;

    @FXML
    private ListView<String> currentOfferings;

    @FXML
    private TextField bookTitle;

    @FXML
    private TextField author;

    @FXML
    private TextArea description;

    @FXML
    private TextField ISBN10;

    @FXML
    private TextField ISBN13;

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
    private Label sellingPrice;

    @FXML
    private void initialize() {
        // Initialize ComboBox with duration options
        duration.setItems(FXCollections.observableArrayList(
                "1 month", "2 months", "3 months", "6 months", "12 months"
        ));
        duration.setOnAction(event -> updateSalesStats());
        // Get the current user's UUID from the Session
        User currentUser = Session.getInstance().getUser();
        UUID sellerUUID = currentUser.user_uuid;

        // Set the seller ID label with the username
        sellerIDLabel.setText(users.getUsernameById(sellerUUID));

        // Fetch and display current offerings
        List<String> bookNames = getSellerCurrentOfferings(sellerUUID);
        currentOfferings.setItems(FXCollections.observableArrayList(bookNames));

        currentOfferings.setOnMouseClicked(this::handleBookSelection);

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

    private void updateSalesStats() {
        // Get the selected month from the ComboBox
        String selectedMonth = duration.getValue();

        // Check if a valid month is selected
        if (selectedMonth != null && !selectedMonth.isEmpty()) {
            // Convert selected month to the number of days for querying the stats
            int days = convertMonthToDays(selectedMonth);

            // Get the current user's UUID from the Session
            User currentUser = Session.getInstance().getUser();
            UUID sellerUUID = currentUser.user_uuid;

            // Fetch stats for the seller based on the selected month
            Statistics stats = new Statistics();
            Map<String, Double> salesStats = stats.getSalesStats(sellerUUID, days);

            // Update the labels with the fetched stats
            if (salesStats != null) {
                salesQuantityLabel.setText(""+ salesStats.get("totalSales"));
                salesRevenueLabel.setText(""+ salesStats.get("totalCost"));
                netProfitLabel.setText(""+ salesStats.get("totalProfit"));
            }
        }
    }

    private int convertMonthToDays(String month) {
        switch (month) {
            case "1 month":
                return 30;
            case "2 months":
                return 60;
            case "3 months":
                return 90;
            case "6 months":
                return 180;
            case "12 months":
                return 365;
            default:
                return 0;
        }
    }
    @FXML
    private void handleBookSelection(MouseEvent event) {
        String selectedBookTitle = currentOfferings.getSelectionModel().getSelectedItem();
        if (selectedBookTitle != null) {
            Listings.Listing selectedListing = getListingByTitle(selectedBookTitle);
            if (selectedListing != null) {
                populateBookDetails(selectedListing);
            }
        }
    }

    private Listings.Listing getListingByTitle(String bookTitle) {
        UUID sellerUUID = Session.getInstance().getUser().user_uuid;
        List<Listings.Listing> sellerListings = listings.getSellerCurrentOfferings(sellerUUID);
        for (Listings.Listing listing : sellerListings) {
            if (listing.bookTitle.equals(bookTitle)) {
                return listing;
            }
        }
        return null;
    }

    private void populateBookDetails(Listings.Listing listing) {
        bookTitle.setText(listing.bookTitle);
        author.setText(listing.author);
        description.setText(listing.description);
        ISBN10.setText(Long.toString(listing.ISBN10));
        ISBN13.setText(Long.toString(listing.ISBN13));
        bookType.setText(listing.genre);
        quantity.setText(Integer.toString(listing.quantity));
        condition.setText(listing.condition);
        price.setText(Double.toString(listing.sellPrice));
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


    @FXML
    private void handleRandomAction() {
        messageLabel.setText("You clicked the button! Something random happened.");
    }


    @FXML
    private void displaySellingPrice(Listing listing) {
        double calculatedPrice = listings.calculateSellPrice(listing);
        sellingPrice.setText(String.format("%.2f", 26));
    }
}