package bookstore.lib;
import java.util.HashMap;
import java.util.Map;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Listings {

    private static final String DB_URL = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.jsxtgxrxqaoyeetpmlhd";
    private static final String PASS = "CSE360Group9$";

    // Inner class representing a single listing
    public class Listing {
        public UUID listingUUID;
        public String bookTitle;
        public String author;
        public String description;
        public long ISBN10;
        public long ISBN13;
        public String type;
        public String condition;
        public double buyPrice;
        public double sellPrice;
        public UUID sellerUUID;
        public int quantity;
        public String status;

        public Listing(UUID listingUUID, String bookTitle, String author, String description,
                       long ISBN10, long ISBN13, String type, String condition, double buyPrice,
                       double sellPrice, UUID sellerUUID, int quantity) {
            this.listingUUID = listingUUID;
            this.bookTitle = bookTitle;
            this.author = author;
            this.description = description;
            this.ISBN10 = ISBN10;
            this.ISBN13 = ISBN13;
            this.type = type;
            this.condition = condition;
            this.buyPrice = buyPrice;
            this.sellPrice = sellPrice;
            this.sellerUUID = sellerUUID;
            this.quantity = quantity;
            this.status = "available";  // Default status is "available"
        }
    }

    // Method to create a new listing
    public String createListing(String bookTitle, String author, String description,
                                long ISBN10, long ISBN13, String type, String condition,
                                double buyPrice, double sellPrice, UUID sellerUUID, int quantity) {

        String insertQuery = "INSERT INTO \"Listings\" (book_title, author, description, \"ISBN-10\", \"ISBN-13\", type, condition, buy_price, sell_price, \"sellerUUID\", quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, bookTitle);
            stmt.setString(2, author);
            stmt.setString(3, description);
            stmt.setLong(4, ISBN10);
            stmt.setLong(5, ISBN13);
            stmt.setString(6, type);
            stmt.setString(7, condition);
            stmt.setDouble(8, buyPrice);
            stmt.setDouble(9, sellPrice);
            stmt.setObject(10, sellerUUID);
            stmt.setInt(11, quantity);
            stmt.executeUpdate();

            return "Success: Listing created.";

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Unable to create listing.";
        }
    }

    // Method to retrieve listing information by UUID
    public Listing getListingInfo(UUID listingUUID) {
        String query = "SELECT * FROM \"Listings\" WHERE \"Listing_UUID\" = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, listingUUID);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Listing(
                        UUID.fromString(rs.getString("Listing_UUID")),
                        rs.getString("book_title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getLong("ISBN-10"),
                        rs.getLong("ISBN-13"),
                        rs.getString("type"),
                        rs.getString("condition"),
                        rs.getDouble("buy_price"),
                        rs.getDouble("sell_price"),
                        UUID.fromString(rs.getString("sellerUUID")),
                        rs.getInt("quantity")
                );
            } else {
                System.out.println("Error: Listing not found.");
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Method to delete a listing by UUID
    public String deleteListing(UUID listingUUID) {
        String deleteQuery = "DELETE FROM \"Listings\" WHERE \"Listing_UUID\" = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

            stmt.setObject(1, listingUUID);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                return "Success: Listing deleted.";
            } else {
                return "Error: Listing not found.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Unable to delete listing.";
        }
    }

    public Map<UUID, Listing> getAll() {
        String query = "SELECT * FROM \"Listings\"";
        Map<UUID, Listing> listingsMap = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                UUID listingUUID = UUID.fromString(rs.getString("Listing_UUID"));
                Listing listing = new Listing(
                        listingUUID,
                        rs.getString("book_title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getLong("ISBN-10"),
                        rs.getLong("ISBN-13"),
                        rs.getString("type"),
                        rs.getString("condition"),
                        rs.getDouble("buy_price"),
                        rs.getDouble("sell_price"),
                        UUID.fromString(rs.getString("sellerUUID")),
                        rs.getInt("quantity")
                );
                listingsMap.put(listingUUID, listing);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to retrieve listings.");
        }

        return listingsMap;
    }
}