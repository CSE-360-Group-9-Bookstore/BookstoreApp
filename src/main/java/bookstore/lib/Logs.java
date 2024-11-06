package bookstore.lib;
import java.util.UUID;


public class Logs {

    //create a log purchase public method that takes a listing UUID, user UUID, sell price, buy price, book title 
    public static void logPurchase(UUID listingUUID, UUID userUUID, double sellPrice, double buyPrice, String bookTitle) {
        //print out the following message: "Purchase: Listing UUID: <listingUUID>, Bookstore ID: <bookstoreID>, User UUID: <userUUID>, Sell Price: <sellPrice>, Buy Price: <buyPrice>, Book Title: <bookTitle>"
        System.out.println("NEW PURCHASE LOGGED: Listing UUID: " + listingUUID  + ", User UUID: " + userUUID + ", Sell Price: " + sellPrice + ", Buy Price: " + buyPrice + ", Book Title: " + bookTitle);
    }
    
}
