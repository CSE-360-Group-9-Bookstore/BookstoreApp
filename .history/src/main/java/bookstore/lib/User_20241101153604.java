package bookstore.lib;
public class User{

   
        private String id;
        public String username;
        public String email;
        String password;
        String role;

        public User(String username, String email, String password, String role) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.role = role;
        }
    }
}