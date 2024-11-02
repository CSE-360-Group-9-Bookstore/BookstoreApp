package bookstore.lib;
public class User{


        public String username;
        public String role;
        public UUID user_uuid;

        public User(String username, String role, UUID user_uuid) {
            this.username = username;
            this.role = role;
            this.user_uuid = user_uuid;
        }
    }
