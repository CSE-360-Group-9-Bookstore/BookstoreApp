package bookstore.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import bookstore.lib.*;  // Import all classes from bookstore.lib
import java.util.Map;
import java.util.UUID;
import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

import javafx.scene.input.MouseEvent;
import bookstore.lib.Listings.Listing;



public class SellerController {

    Listings listings = new Listings();
    Users users = new Users(); // Add Users instance
    private boolean sellPriceCalced = false;
    public Listing draft;
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
    private ComboBox<String> bookGenre;

    @FXML
    private ComboBox<String> condition;

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
    private TextField quantity;


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
        bookGenre.setItems(FXCollections.observableArrayList(
                "Computer Science", "Natural Science", "Mathematics", "Other", "English Language"
        ));
        condition.setItems(FXCollections.observableArrayList(
                "Used Like New", "Moderately Used", "Heavily Used"
        ));

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
        // Define filter parameters for displaying listings in the messageLabel
        List<String> genres = Arrays.asList("Mathematics"); // Example genres to filter
        List<String> conditions = Arrays.asList("Used Like New"); // Example types to filter
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
    private void addInputChangeListeners() {
        List<Control> inputFields = Arrays.asList(
                bookTitle, author, description, ISBN10, ISBN13, price, quantity, bookGenre, condition
        );

        for (Control field : inputFields) {
            if (field instanceof TextField) {
                ((TextField) field).textProperty().addListener((observable, oldValue, newValue) -> resetFormState());
            } else if (field instanceof TextArea) {
                ((TextArea) field).textProperty().addListener((observable, oldValue, newValue) -> resetFormState());
            } else if (field instanceof ComboBox) {
                ((ComboBox<?>) field).valueProperty().addListener((observable, oldValue, newValue) -> resetFormState());
            }
        }
    }

    private void resetFormState() {
        sellPriceCalced = false;
        updateListingButton.setText("Calculate Sell Price");
        sellingPrice.setText("$0.00");
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
                salesQuantityLabel.setText(""+ Math.floor(salesStats.get("totalSales")*100)/100);
                salesRevenueLabel.setText(""+ Math.floor(salesStats.get("totalCost")*100)/100);
                netProfitLabel.setText(""+ Math.floor(salesStats.get("totalProfit")*100)/100);
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
    private void createBookListing(ActionEvent event) {
        try {
            if (bookTitle.getText() == null || bookTitle.getText().isEmpty()) {
                throw new IllegalArgumentException("Book title cannot be empty.");
            }
            if (author.getText() == null || author.getText().isEmpty()) {
                throw new IllegalArgumentException("Author cannot be empty.");
            }
            long isbn10 = Long.parseLong(ISBN10.getText());
            if (String.valueOf(isbn10).length() != 10) {
                throw new IllegalArgumentException("ISBN-10 must be a 10-digit number.");
            }
            long isbn13 = Long.parseLong(ISBN13.getText());
            if (String.valueOf(isbn13).length() != 13) {
                throw new IllegalArgumentException("ISBN-13 must be a 13-digit number.");
            }
            if (bookGenre.getValue() == null || bookGenre.getValue().isEmpty()) {
                throw new IllegalArgumentException("Genre must be selected.");
            }
            if (condition.getValue() == null || condition.getValue().isEmpty()) {
                throw new IllegalArgumentException("Condition must be selected.");
            }
            double priceValue = Double.parseDouble(price.getText());
            if (priceValue <= 0) {
                throw new IllegalArgumentException("Price must be greater than zero.");
            }
            int quantityValue = Integer.parseInt(quantity.getText());
            if (quantityValue <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than zero.");
            }



            if (!sellPriceCalced) {
                draft = new Listing(
                        bookTitle.getText(),
                        author.getText(),
                        description.getText(),
                        isbn10,
                        isbn13,
                        bookGenre.getValue(),
                        condition.getValue(),
                        priceValue,
                        Session.getInstance().getUser().user_uuid,
                        quantityValue
                );
                System.out.println("CALCING PRICE " +draft.sellPrice);
                draft.sellPrice = displaySellingPrice(draft);

                updateListingButton.setText("Submit Listing");
            } else {
                Listings.createListing(draft);
                sellingPrice.setText("$" + Math.floor(draft.sellPrice*100)/100);
                bookTitle.clear();
                author.clear();
                bookGenre.setValue(null);
                condition.setValue(null);
                ISBN10.clear();
                ISBN13.clear();
                price.clear();
                quantity.clear();
                sellingPrice.setText("$0.00");
                currentOfferings.setItems(FXCollections.observableArrayList(
                        getSellerCurrentOfferings(Session.getInstance().getUser().user_uuid)));
                updateListingButton.setText("Calculate Sell Price");
            }

            sellPriceCalced = !sellPriceCalced;
        } catch (NumberFormatException e) {
            showErrorPopup("Invalid number format: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            showErrorPopup(e.getMessage());
        } catch (Exception e) {
            showErrorPopup("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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
    private double displaySellingPrice(Listing listing) {
        try {
            double calculatedPrice = listings.calculateSellPrice(listing);
            sellingPrice.setText("$" + String.format("%.2f", calculatedPrice));
            return calculatedPrice;
        } catch (Exception e) {
            showErrorPopup("Failed to calculate the selling price: " + e.getMessage());
        }
        return -1.11;
    }

}