package library.assistant.ui.listmember;

import java.io.IOException;
import java.net.URL;
import static java.rmi.Naming.list;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.addBook.BookAddController;
import library.assistant.ui.addmember.MemberAddController;
import library.assistant.ui.listbook.BookListController;
import library.assistant.ui.listmember.MemberListController.Member;
import library.assistant.ui.main.MainController;
import library.assistant.util.LibraryAssistantUtil;



public class MemberListController implements Initializable {
    ObservableList<BookListController.Book> list =FXCollections.observableArrayList();

    @FXML
    private TableView<Member> tableView;
    @FXML
    private TableColumn<Member, String> nameCol;
    @FXML
    private TableColumn<Member, String> idCol;
    @FXML
    private TableColumn<Member, String> mobileCol;
    @FXML
    private TableColumn<Member, String> emailCol;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        intiCol();
        loadData();
    }

    private void intiCol() {
         nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
       idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        mobileCol.setCellValueFactory(new PropertyValueFactory<>("mobile"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
    }
   private void loadData() {
             List list = new ArrayList();
             list.clear();
             DatabaseHandler handler =  DatabaseHandler.getInstance();
          
        String qu ="SELECT * FROM MEMBER";
        ResultSet rs= handler.execQuery(qu);
        try {
            while(rs.next()){
                String name =rs.getString("name");
                String mobile =rs.getString("mobile");
                String id =rs.getString("id");
                String email =rs.getString("email");
                
                
                list.add(new Member(name,id,mobile,email));
            }
        } catch (SQLException ex) {
            Logger.getLogger(BookAddController.class.getName()).log(Level.SEVERE, null, ex);
        }
        tableView.getItems().setAll(list);
        //tableView.setItems((ObservableList<Member>) list);
      
 }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void handleMemberEdit(ActionEvent event) {
        Member selectedForEdit =tableView.getSelectionModel().getSelectedItem();
        if(selectedForEdit == null)
        {
            AlertMaker.showErrorMessage("No member selected", "Please select a member to edit");
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/library/assistant/ui/addmember/member_add.fxml"));
            Parent parent = loader.load();
            
            MemberAddController controller = (MemberAddController)loader.getController();
            controller.inflateUI(selectedForEdit);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle("Edit Member");
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
    private void handleMemberDelete(ActionEvent event) {
        MemberListController.Member selectedForDeletion =tableView.getSelectionModel().getSelectedItem();
        if(selectedForDeletion == null)
        {
            AlertMaker.showErrorMessage("No member selected", "Please select a member for deletion");
            return;
        }
        Alert alert =new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Deleting Member");
        alert.setContentText( "Are you sure want to delete the Member " + selectedForDeletion.getName()+"?");
        Optional<ButtonType>answer =alert.showAndWait();
        if(answer.get() == ButtonType.OK)
        { 
            Boolean result =DatabaseHandler.getInstance().deleteMember(selectedForDeletion);
            //if(result){
                AlertMaker.showSimpleAlert("Member",selectedForDeletion.getName() + "was deleted successfully.");   
                list.remove(selectedForDeletion);
           // } 
           /* else{
                AlertMaker.showErrorMessage("Cannot be deleted",selectedForDeletion.getName()+ " is already issued");
            }*/
        }else{    
            AlertMaker.showSimpleAlert("Deletion cancelled", "Deletion process cancelled");
        }
    }
    
    public static class Member {

        private final SimpleStringProperty name;
        private final SimpleStringProperty id;
        private final SimpleStringProperty mobile;
        private final SimpleStringProperty email;

        public Member(String name, String id, String mobile, String email) {
            this.name = new SimpleStringProperty(name);
            this.id = new SimpleStringProperty(id);
            this.mobile = new SimpleStringProperty(mobile);
            this.email = new SimpleStringProperty(email);

        }

        public String getName() {
            return name.get();
        }

        public String getId() {
            return id.get();
        }

        public String getMobile() {
            return mobile.get();
        }

        public String getEmail() {
            return email.get();
        }

    }

}
