package bookstore.lib;
public class User{

   
        String id;
        String username;
        String email;
        String password;
        String role;

        public User(username, email, password, role) {
            this.username = username;
            this.email = email;
            this.password = password;
            this.role = role;
        }
    }
}