package bookstore.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Logs {

    private static final String DB_URL = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.jsxtgxrxqaoyeetpmlhd";
    private static final String PASS = "CSE360Group9$";

    // Method to log purchase in the database
    public static void logPurchase(UUID listingUUID, UUID userUUID, UUID sellerUUID, double sellPrice, double buyPrice, String bookTitle) {
        // Adjusted query to match the table schema, omitting created_at since it should be auto-generated
        String insertQuery = "INSERT INTO public.purchase_logs (listing_uuid, buyer_uuid, seller_uuid, sell_price, buy_price, book_title) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(insertQuery)) {

            stmt.setObject(1, listingUUID);
            stmt.setObject(2, userUUID);
            stmt.setObject(3, sellerUUID);
            stmt.setDouble(4, sellPrice);
            stmt.setDouble(5, buyPrice);
            stmt.setString(6, bookTitle);

            stmt.executeUpdate();

            System.out.println("NEW PURCHASE LOGGED: Listing UUID: " + listingUUID  +
                    ", Buyer UUID: " + userUUID +
                    ", Seller UUID: " + sellerUUID +
                    ", Sell Price: " + sellPrice +
                    ", Buy Price: " + buyPrice +
                    ", Book Title: " + bookTitle);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error: Unable to log purchase to the database.");
        }
    }
}