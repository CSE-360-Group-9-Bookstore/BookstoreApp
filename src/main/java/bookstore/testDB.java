package bookstore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class testDB {

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.jsxtgxrxqaoyeetpmlhd";
    private static final String PASS = "CSE360Group9$";

    /**
     * Retrieves all records from the specified table.
     *
     * @param tableName the name of the table to retrieve data from
     * @return a list of string arrays, where each array represents a row in the table
     */
    public List<String[]> getTableData(String tableName) {
        List<String[]> data = new ArrayList<>();
        String query = "SELECT * FROM " + tableName;

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            int columnCount = rs.getMetaData().getColumnCount();

            // Iterate through the result set and add each row to the list
            while (rs.next()) {
                String[] row = new String[columnCount];
                for (int i = 1; i <= columnCount; i++) {
                    row[i - 1] = rs.getString(i);
                }
                data.add(row);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * Inserts a new name (first name and last name) into the 'test' table.
     *
     * @param firstName the first name to be added
     * @param lastName the last name to be added
     * @return true if the name was added successfully, false otherwise
     */
    public boolean addName(String firstName, String lastName) {
        String query = "INSERT INTO test VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Set values for the query parameters
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);

            // Execute the insert operation
            stmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
