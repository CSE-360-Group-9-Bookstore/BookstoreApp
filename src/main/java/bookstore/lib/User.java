package bookstore.lib;
import java.util.UUID;

public class User{

    public UUID user_uuid;
    public String username;
    public String password;
    public String role;

    public User(UUID user_uuid, String username, String password, String role) {
        this.user_uuid = user_uuid;
        this.username = username;
        this.password = password;
        this.role = role;
    }
}