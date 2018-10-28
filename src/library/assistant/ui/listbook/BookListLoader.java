
package library.assistant.ui.listbook;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class BookListLoader extends Application {
    public void start(Stage stage) throws Exception{
        Parent root =FXMLLoader.load(getClass().getResource("book_list.fxml"));
        
        Scene scene =new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args)
    {
        launch(args);
    }
    
}
