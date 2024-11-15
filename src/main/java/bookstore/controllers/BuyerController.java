package bookstore.controllers;

import bookstore.lib.Listings;
import bookstore.lib.Logs;
import bookstore.lib.Session;
import bookstore.lib.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;








public class BuyerController {
    @FXML
    public Label titleLabel;
    @FXML
    public Label authorLabel;
    @FXML
    public Label stockLabel;
    @FXML
    public Label priceLabel;
    @FXML
    public Label isbn10Label;
    @FXML
    public Label isbn13Label;
    @FXML
    public Label genreLabel;
    @FXML
    public Label descriptionLabel;
    @FXML
    public Label conditionLabel;
    private Listings listings = new Listings();
    private ObservableList<String> listingDisplayItems = FXCollections.observableArrayList();
    private Map<UUID, Listings.Listing> allListings;
    private Map<String, UUID> displayTextToUUIDMap = new HashMap<>();

    @FXML
    private Label messageLabel;
    @FXML
    private ListView<String> listingIdList;
    @FXML
    private ComboBox<String> genreCB;
    @FXML
    private ComboBox<String> conCB;
    @FXML
    private Button buyButton;

    @FXML
    private void initialize() {
        // Initial setup
        setupComboBoxes();
        setupListView();
        buyButton.setVisible(false);

        // Fetch and display all listings initially
        allListings = fetchAllListings();
        refreshListingView();
    }

    // Setup ComboBoxes with genres and conditions
    private void setupComboBoxes() {
        ObservableList<String> genres = FXCollections.observableArrayList(
                "", "Natural Science", "Computer Science", "Mathematics", "English Language", "Other");
        genreCB.setItems(genres);

        ObservableList<String> conditions = FXCollections.observableArrayList(
                "", "Used Like New", "Moderately Used", "Heavily Used");
        conCB.setItems(conditions);

        // Add listeners to trigger filtering
        genreCB.setOnAction(e -> applyFilters());
        conCB.setOnAction(e -> applyFilters());
    }

    // Setup ListView with selection listener
    private void setupListView() {
        listingIdList.setItems(listingDisplayItems);
        listingIdList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayListingDetails(displayTextToUUIDMap.get(newValue));
                buyButton.setVisible(true);
            }
        });
    }

    // Apply filters based on selected genre and condition
    private void applyFilters() {
        String selectedGenre = genreCB.getValue();
        String selectedCondition = conCB.getValue();

        listingDisplayItems.clear();
        displayTextToUUIDMap.clear();

        // Filter listings based on selections
        for (Map.Entry<UUID, Listings.Listing> entry : listings.getAll().entrySet()) {
            Listings.Listing listing = entry.getValue();
            boolean genreMatch = (selectedGenre == null || selectedGenre.isEmpty() || selectedGenre.equals(listing.genre));
            boolean conditionMatch = (selectedCondition == null || selectedCondition.isEmpty() || selectedCondition.equals(listing.condition));

            if (genreMatch && conditionMatch) {
                String displayText = formatListingDisplayText(listing);
                listingDisplayItems.add(displayText);
                displayTextToUUIDMap.put(displayText, listing.listingUUID);
            }
        }

        listingIdList.refresh();
    }

    // Fetch all listings
    private Map<UUID, Listings.Listing> fetchAllListings() {
        return listings.getAll();
    }

    // Refresh ListView based on the latest state of allListings
    private void refreshListingView() {
        listingDisplayItems.clear();
        displayTextToUUIDMap.clear();

        for (Map.Entry<UUID, Listings.Listing> entry : allListings.entrySet()) {
            Listings.Listing listing = entry.getValue();
            String displayText = formatListingDisplayText(listing);
            listingDisplayItems.add(displayText);
            displayTextToUUIDMap.put(displayText, listing.listingUUID);
        }

        listingIdList.refresh();
    }

    // Handle purchase action
    @FXML
    private void handlePurchase() {
        UUID selectedUUID = getSelectedListingUUID();

        if (selectedUUID != null) {
            listings.purchaseBook(selectedUUID);
            allListings = fetchAllListings();
            refreshListingView();

            Listings.Listing listing = allListings.get(selectedUUID);
            if (listing != null) {
                messageLabel.setText(String.format("You bought '%s'. Remaining stock: %d.", listing.bookTitle, listing.quantity));
            } else {
                messageLabel.setText("The selected book is now out of stock and has been removed.");
                refreshListingView();
            }

            buyButton.setVisible(false);
            logTransaction(selectedUUID, listing);
        } else {
            messageLabel.setText("Error: No book selected.");
        }
    }

    // Get the selected listing's UUID
    private UUID getSelectedListingUUID() {
        String selectedItem = listingIdList.getSelectionModel().getSelectedItem();
        return displayTextToUUIDMap.get(selectedItem);
    }

    // Display detailed information for the selected listing
    private void displayListingDetails(UUID listingUUID) {
        Listings.Listing selectedListing = allListings.get(listingUUID);
        buyButton.setVisible(true);
        if (selectedListing != null) {
            titleLabel.setText(selectedListing.bookTitle);
            authorLabel.setText(selectedListing.author);
            descriptionLabel.setText(selectedListing.description);
            genreLabel.setText(selectedListing.genre);
            isbn10Label.setText("ISBN-10: " + selectedListing.ISBN10);
            isbn13Label.setText("ISBN-13: " + selectedListing.ISBN13);
            conditionLabel.setText("Condition: \"" + selectedListing.condition+ "\"");
            priceLabel.setText("$" + selectedListing.sellPrice);
            stockLabel.setText(selectedListing.quantity+ " Available");
        }

    }

    // Log the transaction
    private void logTransaction(UUID selectedUUID, Listings.Listing listing) {
        if (listing != null) {
            User currentUser = Session.getInstance().getUser();
            UUID buyerUUID = currentUser.user_uuid;

            Logs.logPurchase(
                    listing.listingUUID,
                    buyerUUID,
                    listing.sellerUUID,
                    listing.sellPrice,
                    listing.msrp,
                    listing.bookTitle
            );
        }
    }

    // Format listing display text for ListView
    private String formatListingDisplayText(Listings.Listing listing) {
        return String.format("%s - $%.2f", listing.bookTitle, listing.sellPrice);
    }

}
