package bookstore;

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
    public String login(String username, String password) {
        String authResult = authenticate(username, password);
        if (authResult.startsWith("Error:")) {
            return authResult;
        } else {
            String role = authResult;
            String query = "SELECT id, created_at FROM \"Users\" WHERE username = ?";

            try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);PreparedStatement stmt = conn.prepareStatement(query)) {
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
    public String authenticateUser(String username, String password) {
        String authResult = authenticate(username, password);
        if (authResult.startsWith("Error:")) {
        
            return "error";
        } else {
            String role = authResult;

            System.out.println("Role: " + role);

            role = role.replaceAll("[^a-zA-Z0-9]", "");




            if(!("admin".equals(role)) &&!(role !="buyer"&&!(role!="seller"){role = "error";}
            return role;
            
        }
    }
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
                
                return rs.getString("role");
            } else {
               
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

}
