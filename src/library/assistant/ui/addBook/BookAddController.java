
package library.assistant.ui.addBook;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.listbook.BookListController;


public class BookAddController implements Initializable {

    @FXML
    private JFXTextField title;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField author;
    @FXML
    private JFXTextField Publisher;
    @FXML
    private JFXButton SaveButton;
    @FXML
    private JFXButton CancelButton;

    DatabaseHandler databaseHandler;
    @FXML
    private AnchorPane rootPane;
    private Boolean isInEditMode = Boolean.FALSE;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        databaseHandler =DatabaseHandler.getInstance();
        checkData();
    }    

    @FXML
    private void addBook(ActionEvent event) {
        String bookID = id.getText();
        String bookName= title.getText();
        String bookAuthor = author.getText();
        String bookPublisher = Publisher.getText();
        
        if(bookID.isEmpty()||bookAuthor.isEmpty()||bookName.isEmpty()||bookPublisher.isEmpty()){
            Alert alert =new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Please Enter in all Fields");
            alert.showAndWait();
            return;
        }
        if(isInEditMode)
        {
            handleEditOperation();
            return;
        }
         
        String qu ="INSERT INTO BOOK  values ("+
                "'" + bookID  + "',"+
                "'" + bookName  + "',"+
                "'" + bookAuthor  + "',"+
                "'" + bookPublisher  + "',"+
              "" + true + ""+    
                 ")";
        System.out.println(qu);
        if( databaseHandler.execAction(qu))
        {
             Alert alert =new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Successfully");
            alert.showAndWait();
        }else
        {
            Alert alert =new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Failed");
            alert.showAndWait();
        }
     
    }
    @FXML
    private void Cancel(ActionEvent event) {
       
        Stage stage = (Stage) rootPane.getScene().getWindow();
       stage.close();
        
        
    }
  private void checkData() {
        String qu ="SELECT title FROM BOOK";
        ResultSet rs= databaseHandler.execQuery(qu);
        try {
            while(rs.next()){
                String titlex =rs.getString("title");
                System.out.println(titlex);

            }
        } catch (SQLException ex) {
            Logger.getLogger(BookAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
  public void inflateUI(BookListController.Book book)
  {
      title.setText(book.getTitle());
      id.setText(book.getId());
      author.setText(book.getAuthor());
      Publisher.setText(book.getPublisher());
      id.setEditable(false);
      isInEditMode = Boolean.TRUE;
  }

    private void handleEditOperation() {
        BookListController.Book book = new BookListController.Book(title.getText(),id.getText(),author.getText(),Publisher.getText(),true);
        if(databaseHandler.updateBook(book))
        {
            AlertMaker.showSimpleAlert("Success", "Book updated.");
        }
        else{
            AlertMaker.showErrorMessage("Failed", "Cannot update the book");
        }
    }
}
