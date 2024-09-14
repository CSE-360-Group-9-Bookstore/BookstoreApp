package bookstore;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Hello JavaFX!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");

        testDB db = new testDB();
        System.out.println(db.getTableData("test"));

        String[][] data = db.getTableData("test").toArray(new String[0][]);

        for (String[] row : data) {
            for (String cell : row) {
                System.out.print(cell + " ");
            }
            System.out.println();  // Move to the next line after each row

            //testing
        }

        //super basic connection to a database, will write basic database methods such as add and stuff soon.
        //testing

        //testing
        btn.setOnAction(event -> System.out.println("Hello World!"));

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
