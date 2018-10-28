
package library.assistant.ui.listbook;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import javafx.stage.StageStyle;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.addBook.BookAddController;
import library.assistant.ui.main.MainController;
import library.assistant.util.LibraryAssistantUtil;


public class BookListController implements Initializable {
    
    ObservableList<Book> list =FXCollections.observableArrayList();

    @FXML
    private AnchorPane rootPane;
    @FXML
    private TableView<Book> tableView;
    @FXML
    private TableColumn<Book, String> titleCol;
    @FXML
    private TableColumn<Book, String> idCol;
    @FXML
    private TableColumn<Book, String> authorCol;
    @FXML
    private TableColumn<Book, String> publisherCol;
    @FXML
    private TableColumn<Book, Boolean> availabilityCol;

    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initCol();
        loadData();
    } 

    private void initCol() {
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
       idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("author"));
        publisherCol.setCellValueFactory(new PropertyValueFactory<>("publisher"));
        availabilityCol.setCellValueFactory(new PropertyValueFactory<>("availability"));
  
    }

    private void loadData() {
        list.clear();  
        
        DatabaseHandler handler = DatabaseHandler.getInstance();
        String qu ="SELECT * FROM BOOK";
        ResultSet rs= handler.execQuery(qu);
        try {
            while(rs.next()){
                String titlex =rs.getString("title");
                String id =rs.getString("id");
                String author =rs.getString("author");
                String publisher =rs.getString("publisher");
                Boolean avail =rs.getBoolean("isAvail");
                
                list.add(new Book(titlex, id, author, publisher, avail));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.setItems(list);
    }

    @FXML
    private void handleDeleteBookOption(ActionEvent event) {
        Book selectedForDeletion =tableView.getSelectionModel().getSelectedItem();
        if(selectedForDeletion == null)
        {
            AlertMaker.showErrorMessage("No book selected", "Please select a book for deletion");
            return;
        }
        Alert alert =new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting Book");
        alert.setContentText( "Are you sure want to delete the Book " + selectedForDeletion.getTitle()+"?");
        Optional<ButtonType>answer =alert.showAndWait();
        if(answer.get() == ButtonType.OK)
        { 
            Boolean result =DatabaseHandler.getInstance().deleteBook(selectedForDeletion);
            if(result){
                AlertMaker.showSimpleAlert("Book",selectedForDeletion.getTitle() + "was deleted successfully.");   
                list.remove(selectedForDeletion);
            } 
            else{
                AlertMaker.showErrorMessage("Cannot be deleted",selectedForDeletion.getTitle()+ " is already issued");
            }
        }else{    
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }

    @FXML
    private void handleBookEditOption(ActionEvent event) {
        Book selectedForEdit =tableView.getSelectionModel().getSelectedItem();
        if(selectedForEdit == null)
        {
            AlertMaker.showErrorMessage("No book selected", "Please select a book to edit");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addBook/add_book.fxml"));
            Parent parent = loader.load();
            BookAddController controller = (BookAddController)loader.getController();
            controller.inflateUI(selectedForEdit);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Book");
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);
            stage.setOnCloseRequest((e)->{
                handleRefresh(new ActionEvent());
            });

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }
    
    
    public static class Book{
        private final SimpleStringProperty title;
        private final SimpleStringProperty id;
        private final SimpleStringProperty author;
        private final SimpleStringProperty publisher;
        private final SimpleBooleanProperty availability;
        
        public Book(String title, String id, String author ,String pub, Boolean avail){
            this.title =new SimpleStringProperty(title);
            this.id =new SimpleStringProperty(id);
            this.author =new SimpleStringProperty(author);
            this.publisher =new SimpleStringProperty(pub);
            this.availability =new SimpleBooleanProperty(avail);
   
        }

        public String getTitle() {
            return title.get();
        }

        public String getId() {
            return id.get();
        }

        public String getAuthor() {
            return author.get();
        }

        public String getPublisher() {
            return publisher.get();
        }

        public Boolean getAvailability() {
            return availability.get();
        }
 
    }
    
}
