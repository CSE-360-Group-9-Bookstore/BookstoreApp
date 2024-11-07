package bookstore.lib;

import java.sql.*;
import java.util.UUID;

public class Users {

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

    public String forgotPassword(String username) {
        String query = "SELECT password FROM \"Users\" WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);PreparedStatement stmt = conn.prepareStatement(query)) {

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

    public User authenticateUser(String username, String password) {
        return authenticate(username, password);
    }

    private User authenticate(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            return null;
        }

        String query = "SELECT id, username, password, role FROM \"Users\" WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UUID id = rs.getObject("id", java.util.UUID.class);
                String uname = rs.getString("username");
                String pwd = rs.getString("password");
                String role = rs.getString("role");
                User user = new User(id, uname, pwd, role);
                return user;
            } else {
                // Authentication failed
                return null;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

}