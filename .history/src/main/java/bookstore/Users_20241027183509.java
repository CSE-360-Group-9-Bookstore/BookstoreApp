package bookstore;

import java.sql.*;
import java.util.UUID;

public class Users {

    // Database connection details
    private static final String DB_URL = "jdbc:postgresql://aws-0-us-west-1.pooler.supabase.com:6543/postgres";
    private static final String USER = "postgres.jsxtgxrxqaoyeetpmlhd";
    private static final String PASS = "CSE360Group9$";
    public String addUser(String username, String password, String role) {
        if (username == null || password == null || role == null ||
                username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            return "Error: All fields must be filled.";
        }

        String checkUserQuery = "SELECT 1 FROM \"Users\" WHERE username = ?";
        String insertUserQuery = "INSERT INTO \"Users\" (username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
            try (PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery)) {
                checkStmt.setString(1, username);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    return "Error: Username already exists.";
                }
            }
            try (PreparedStatement insertStmt = conn.prepareStatement(insertUserQuery)) {
                insertStmt.setString(1, username);
                insertStmt.setString(2, password);
                insertStmt.setString(3, role);
                insertStmt.executeUpdate();
                return "Success: User added.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Unable to add user.";
        }
    }

    /**
     * Logs in a user by verifying username and password.
     *
     * @param username the username
     * @param password the password
     * @return user information if successful, or an error message
     */
    public String login(String username, String password) {
        String authResult = authenticate(username, password);
        if (authResult.startsWith("Error:")) {
            return authResult;
        } else {
            // Authentication successful
            String role = authResult;
            // Now retrieve the rest of the user info
            String query = "SELECT id, created_at FROM \"Users\" WHERE username = ?";

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, username);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    UUID id = (UUID) rs.getObject("id");
                    Timestamp createdAt = rs.getTimestamp("created_at");

                    return "Login successful. User Info:\n" +
                            "ID: " + id + "\n" +
                            "Created At: " + createdAt + "\n" +
                            "Role: " + role;
                } else {
                    return "Error: Unable to retrieve user info.";
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return "Error: Unable to login.";
            }
        }
    }

    /**
     * Retrieves the password for a given username.
     *
     * @param username the username
     * @return the password if username exists, or an error message
     */
    public String forgotPassword(String username) {
        String query = "SELECT password FROM \"Users\" WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String password = rs.getString("password");
                return "Your password is: " + password;
            } else {
                return "Error: Username not found.";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Unable to retrieve password.";
        }
    }

    /**
     * Authenticates a user and returns role-specific information.
     *
     * @param username the username
     * @param password the password
     * @return a String array containing role-specific data or error messages
     */
    public String[] authenticateUser(String username, String password) {
        String authResult = authenticate(username, password);
        if (authResult.startsWith("Error:")) {
            // Return an array with "error" and the error message
            return new String[]{"error", authResult.substring(7)}; // Remove "Error: " prefix
        } else {
            String role = authResult;

            System.out.println("Role: " + role);

            //strip all non-alphanumeric characters
            role = role.replaceAll("[^a-zA-Z0-9]", "");




            // Use a switch statement to return role-specific information
            switch (role.toLowerCase()) {
                case "admin":
                    return new String[]{"testing", "page2"};
                case "buyer":
                    return new String[]{"testing"};
                case "seller":
                    return new String[]{"page2"};
                default:
                    return new String[]{"error", "Unknown role."};
            }
        }
    }

    /**
     * Authenticates the user by verifying the username and password.
     * Returns the user's role if successful, or an error message if not.
     *
     * @param username the username
     * @param password the password
     * @return the role of the user, or an error message starting with "Error:"
     */
    private String authenticate(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return "Error: Username and password must be provided.";
        }

        String query = "SELECT role FROM \"Users\" WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Authentication successful
                return rs.getString("role");
            } else {
                // Check if username exists
                String checkUserQuery = "SELECT 1 FROM \"Users\" WHERE username = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery)) {
                    checkStmt.setString(1, username);
                    ResultSet rsCheck = checkStmt.executeQuery();
                    if (rsCheck.next()) {
                        return "Error: Incorrect password.";
                    } else {
                        return "Error: Username not found.";
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: Unable to authenticate.";
        }
    }

    // Additional methods can be added here if needed

}
