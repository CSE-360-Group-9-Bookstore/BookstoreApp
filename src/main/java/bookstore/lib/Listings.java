package bookstore.lib;

import java.sql.*;
import java.util.*;

public class Listings {

    private static final String DB_URL = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.jsxtgxrxqaoyeetpmlhd";
    private static final String PASS = "CSE360Group9$";

    Map<String, Double> qualityFactor = new HashMap<>();
    Map<String, Double> typeFactor = new HashMap<>();

    public static class Listing {
        public UUID listingUUID;
        public String bookTitle;
        public String author;
        public String description;
        public long ISBN10;
        public long ISBN13;
        public String genre;
        public String condition;
        public double msrp;
        public double sellPrice;
        public UUID sellerUUID;
        public int quantity;
        public String status;

        public Listing(String bookTitle, String author, String description,
                       long ISBN10, long ISBN13, String genre, String condition, double msrp,
                       UUID sellerUUID, int quantity) {
            this.listingUUID = UUID.randomUUID();
            this.bookTitle = bookTitle;
            this.author = author;
            this.description = description;
            this.ISBN10 = ISBN10;
            this.ISBN13 = ISBN13;
            this.genre = genre;
            this.condition = condition;
            this.msrp = msrp;
            this.sellPrice = 0;
            this.sellerUUID = sellerUUID;
            this.quantity = quantity;
            this.status = "available";
        }

        public Listing(UUID uuid, String bookTitle, String author, String description,
                       long ISBN10, long ISBN13, String genre, String condition, double msrp, double sellPrice,
                       UUID sellerUUID, int quantity) {
            this.listingUUID = uuid;
            this.bookTitle = bookTitle;
            this.author = author;
            this.description = description;
            this.ISBN10 = ISBN10;
            this.ISBN13 = ISBN13;
            this.genre = genre;
            this.condition = condition;
            this.msrp = msrp;
            this.sellPrice = sellPrice;
            this.sellerUUID = sellerUUID;
            this.quantity = quantity;
            this.status = "available";
        }
    }

    // Methods (calculateSellPrice, createListing, etc.) go here

    public double calculateSellPrice(Listing draft) {
        double init = draft.msrp * qualityFactor.get(draft.condition) + typeFactor.get(draft.genre);
        return Math.floor(Math.max(init * 1.025, 9.99) * 100)/100; // (0.025% platform fee)
    }

    public static String createListing(Listing finalListing) {
        String insertQuery = "INSERT INTO \"Listings\" (book_title, author, description, \"ISBN-10\", \"ISBN-13\", genre, condition, msrp, sell_price, \"sellerUUID\", quantity) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setString(1, finalListing.bookTitle);
            stmt.setString(2, finalListing.author);
            stmt.setString(3, finalListing.description);
            stmt.setLong(4, finalListing.ISBN10);
            stmt.setLong(5, finalListing.ISBN13);
            stmt.setString(6, finalListing.genre);
            stmt.setString(7, finalListing.condition);
            stmt.setDouble(8, finalListing.msrp);
            stmt.setDouble(9, finalListing.sellPrice);
            stmt.setObject(10, finalListing.sellerUUID);
            stmt.setInt(11, finalListing.quantity);
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
                        rs.getString("genre"),
                        rs.getString("condition"),
                        rs.getDouble("msrp"),
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



        public List<Listing> getSellerCurrentOfferings(UUID sellerUUID) {
            String query = "SELECT * FROM \"Listings\" WHERE \"sellerUUID\" = ?";
            List<Listing> sellerListings = new ArrayList<>();

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setObject(1, sellerUUID);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Listing listing = new Listing(
                            UUID.fromString(rs.getString("Listing_UUID")),
                            rs.getString("book_title"),
                            rs.getString("author"),
                            rs.getString("description"),
                            rs.getLong("ISBN-10"),
                            rs.getLong("ISBN-13"),
                            rs.getString("genre"),
                            rs.getString("condition"),
                            rs.getDouble("msrp"),
                            rs.getDouble("sell_price"),
                            UUID.fromString(rs.getString("sellerUUID")),
                            rs.getInt("quantity")
                    );
                    sellerListings.add(listing);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                System.out.println("Error: Unable to retrieve seller's current offerings.");
            }

            return sellerListings;
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
    public String purchaseBook(UUID listingUUID) {
            // Query to update the quantity
            String updateQuery = "UPDATE \"Listings\" SET quantity = quantity - 1 WHERE \"Listing_UUID\" = ? AND quantity > 0";
            // Query to delete the listing if quantity reaches 0
            String deleteQuery = "DELETE FROM \"Listings\" WHERE \"Listing_UUID\" = ? AND quantity <=0";

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {

                try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                    stmt.setObject(1, listingUUID);

                    System.out.println("Executing update: " + stmt.toString());
                    int rowsAffected = stmt.executeUpdate();

                    if (rowsAffected > 0) {
                        System.out.println("Quantity updated successfully.");
                        return "Success: Book purchased. Quantity updated.";
                    } else {
                        // If the quantity is 0, delete the listing
                        System.out.println("No rows affected, attempting to delete listing.");
                        try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                            deleteStmt.setObject(1, listingUUID);
                            deleteStmt.executeUpdate();
                            return "Error: Stock is 0, book listing deleted.";
                        }
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return "Error: Unable to process purchase.";
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
                        rs.getString("genre"),
                        rs.getString("condition"),
                        rs.getDouble("msrp"),
                        rs.getDouble("sell_price"),
                        UUID.fromString(rs.getString("sellerUUID")),
                        rs.getInt("quantity")
                );
                if(listing.quantity>0){
                    listingsMap.put(listingUUID, listing);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to retrieve listings.");
        }

        return listingsMap;
    }

    public Map<UUID, Listing> filterAll(List<String> genres, List<String> conditions, Double minSellPrice, Double maxSellPrice, String search, Long isbn10, Long isbn13, UUID sellerID) {
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM \"Listings\" WHERE 1=1");
        Map<UUID, Listing> listingsMap = new HashMap<>();
        List<Object> parameters = new ArrayList<>();

        if (genres != null && !genres.isEmpty()) {
            queryBuilder.append(" AND genre IN (");
            queryBuilder.append(String.join(", ", "?".repeat(genres.size())));
            queryBuilder.append(")");
            parameters.addAll(genres);
        }

        if (conditions != null && !conditions.isEmpty()) {
            queryBuilder.append(" AND condition IN (");
            queryBuilder.append(String.join(", ", "?".repeat(conditions.size())));
            queryBuilder.append(")");
            parameters.addAll(conditions);
        }

        if (minSellPrice != null) {
            queryBuilder.append(" AND sell_price >= ?");
            parameters.add(minSellPrice);
        }

        if (maxSellPrice != null) {
            queryBuilder.append(" AND sell_price <= ?");
            parameters.add(maxSellPrice);
        }

        // Adding the search keyword filter
        if (search != null && !search.isEmpty()) {
            queryBuilder.append(" AND (book_title ILIKE ? OR author ILIKE ? OR description ILIKE ?)");
            String searchPattern = "%" + search + "%";  // Allows partial matches
            parameters.add(searchPattern);
            parameters.add(searchPattern);
            parameters.add(searchPattern);
        }

        // Adding exact match for ISBN-10
        if (isbn10 != null) {
            queryBuilder.append(" AND \"ISBN-10\" = ?");
            parameters.add(isbn10);
        }

        // Adding exact match for ISBN-13
        if (isbn13 != null) {
            queryBuilder.append(" AND \"ISBN-13\" = ?");
            parameters.add(isbn13);
        }

        // Adding exact match for sellerID
        if (sellerID != null) {
            queryBuilder.append(" AND sellerUUID = ?");
            parameters.add(sellerID);
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString())) {

            // Set parameters for the prepared statement
            for (int i = 0; i < parameters.size(); i++) {
                stmt.setObject(i + 1, parameters.get(i));
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UUID listingUUID = UUID.fromString(rs.getString("Listing_UUID"));
                Listing listing = new Listing(
                        listingUUID,
                        rs.getString("book_title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getLong("ISBN-10"),
                        rs.getLong("ISBN-13"),
                        rs.getString("genre"),
                        rs.getString("condition"),
                        rs.getDouble("msrp"),
                        rs.getDouble("sell_price"),
                        UUID.fromString(rs.getString("sellerUUID")),
                        rs.getInt("quantity")
                );
                listingsMap.put(listingUUID, listing);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to retrieve filtered listings.");
        }

        return listingsMap;
    }
}
