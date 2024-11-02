package bookstore.lib;

import java.util.UUID;

public class Listing {
    private UUID uuid;
    public Book book;
    public User seller;
    public String status;
    public double price;

    public Listing(Book book, User seller) {
        this.book = book;
        this.seller = seller;
        this.status = "available";
        this.price = book.price;
        this.uuid = 
    }

    public boolean sellListing(User buyer) {
        if (this.status.equals("available")) {
            this.status = "sold";
            return true;
        }
        return false;
    }

    public boolean deleteListing() {
        if (this.status.equals("available")) {
            this.status = "deleted";
            return true;
        }
        return false;
    }

    public void editListing(Book book) {
        if (this.status.equals("available")) {
            this.book = book;
            this.price = book.price;
        }
    }

}
