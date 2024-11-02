package bookstore.lib;
import java.util.UUID;
public class User{


        public String username;
        public String role;
        public UUID user_uuid;

        public User(String username, String role) {
            this.username = username;
            this.role = role;

        }
    }
