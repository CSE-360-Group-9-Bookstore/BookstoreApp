package bookstore.lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Statistics {

    private static final String DB_URL = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.jsxtgxrxqaoyeetpmlhd";
    private static final String PASS = "CSE360Group9$";

    /**
     * Method to get sales statistics over the past 'days' for a specified user.
     *
     * @param userUUID The UUID of the user to calculate statistics for.
     * @param days The number of days back to query the sales logs.
     * @return A map containing total sales, total cost, and total profit information.
     */
    public Map<String, Double> getSalesStats(UUID userUUID, int days) {
        String query = "SELECT sell_price, buy_price FROM public.purchase_logs " +
                "WHERE seller_uuid = ? AND created_at >= (CURRENT_TIMESTAMP - (? || ' days')::INTERVAL)";

        double totalSales = 0.0;
        double totalCost = 0.0;
        double totalProfit = 0.0;

        System.out.println("Executing SQL Query: " + query.replaceFirst("\\?", "'" + userUUID.toString() + "'")
                .replaceFirst("\\?", String.valueOf(days)));

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, userUUID);
            stmt.setInt(2, days);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double sellPrice = rs.getDouble("sell_price");
                double buyPrice = rs.getDouble("buy_price");

                totalSales += sellPrice;
                totalCost += buyPrice;
                totalProfit += (sellPrice - buyPrice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        Map<String, Double> totalsMap = new HashMap<>();
        totalsMap.put("totalSales", totalSales);
        totalsMap.put("totalCost", totalCost);
        totalsMap.put("totalProfit", totalProfit);

        return totalsMap;
    }

    public Map<String, Double> getAllStats(int days) {
        String query = "SELECT sell_price, buy_price FROM public.purchase_logs " +
                "WHERE created_at >= (CURRENT_TIMESTAMP - (? || ' days')::INTERVAL)";

        double totalSales = 0.0;
        double totalCost = 0.0;
        double totalProfit = 0.0;

        System.out.println("Executing SQL Query: " + query.replaceFirst("\\?", String.valueOf(days)));

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, days);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                double sellPrice = rs.getDouble("sell_price");
                double buyPrice = rs.getDouble("buy_price");

                totalSales += sellPrice;
                totalCost += buyPrice;
                totalProfit += (sellPrice - buyPrice);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        Map<String, Double> totalsMap = new HashMap<>();
        totalsMap.put("totalSales", totalSales);
        totalsMap.put("totalCost", totalCost);
        totalsMap.put("totalProfit", totalProfit);

        return totalsMap;
    }

    public String getBestSeller(int days) {
        String query = "SELECT book_title, COUNT(book_title) as count FROM public.purchase_logs " +
                "WHERE created_at >= (CURRENT_TIMESTAMP - (? || ' days')::INTERVAL) " +
                "GROUP BY book_title ORDER BY count DESC LIMIT 1";

        String bestSeller = null;

        System.out.println("Executing SQL Query: " + query.replaceFirst("\\?", String.valueOf(days)));

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, days);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                bestSeller = rs.getString("book_title");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return bestSeller;
    }

    public Map<UUID, Map<String, Double>> getAllSellerStats(int days) {
        String query = "SELECT DISTINCT seller_uuid FROM public.purchase_logs";

        Map<UUID, Map<String, Double>> allSellerStats = new HashMap<>();

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                UUID sellerUUID = (UUID) rs.getObject("seller_uuid");
                Map<String, Double> sellerStats = getSalesStats(sellerUUID, days);
                allSellerStats.put(sellerUUID, sellerStats);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return allSellerStats;
    }
}