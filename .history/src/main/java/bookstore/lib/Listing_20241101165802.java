package bookstore.lib;

public class Listing {
    private String uuid;
    public Book book;
    public User seller;
    public String status;
    public double price;

    public Listing(Book book, User seller) {
        this.book = book;
        this.seller = seller;
        this.status = "available";
        this.price = book.price;
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
    public void editListing()

}
