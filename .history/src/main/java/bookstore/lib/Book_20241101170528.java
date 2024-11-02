package bookstore.lib;

public class Book {
    public String title;
    public String author;
    public double price;
    public int isbn10;
    public int isbn13;
    public String condition;
    public String genre;

    public Book(String title, String author, double price, int isbn10, int isbn13, String condition, String genre) {
        this.title = title;
        this.author = author;
        this.price = price;
        this.isbn10 = isbn10;
        this.isbn13 = isbn13;
        this.condition = condition;
        this.genre = genre;
    }

    public boolean editBook(String title, String author, double price, int isbn10, int isbn13, String condition,
            String genre) {
        return true;
    }
}
