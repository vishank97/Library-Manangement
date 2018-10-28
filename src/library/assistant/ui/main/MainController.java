package library.assistant.ui.main;


import com.jfoenix.controls.JFXTextField;
import com.jfoenix.effects.JFXDepthManager;
import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import library.assistant.database.DatabaseHandler;
import library.assistant.util.LibraryAssistantUtil;

public class MainController implements Initializable {

    @FXML
    private HBox book_info;
    @FXML
    private HBox member_info;
    @FXML
    private Text bookName;
    @FXML
    private Text bookAuthor;
    @FXML
    private Text bookStatus;

    
    @FXML
    private TextField memberIDInput;
    @FXML
    private Text memberName;
    @FXML
    private Text memberMobile;
    @FXML
    private TextField bookID;
    @FXML
    private ListView<String> issueDataList;
    @FXML
    private StackPane rootPane;

    
    DatabaseHandler databaseHandler;
    Boolean isReadyForSubmission=false;
    @FXML
    private TextField bookIDinput;
  
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        JFXDepthManager.setDepth(book_info, 1);
        JFXDepthManager.setDepth(member_info, 1);

        databaseHandler = DatabaseHandler.getInstance();
      
    }

    @FXML
    private void loadAddMember(ActionEvent event) {
        loadWindow("/library/assistant/ui/addmember/member_add.fxml", "Add New Member");
    }

    @FXML
    private void loadAddBook(ActionEvent event) {
        loadWindow("/library/assistant/ui/addBook/add_book.fxml", "Add New Book");
    }

    @FXML
    private void loadMemberTable(ActionEvent event) {
        loadWindow("/library/assistant/ui/listmember/member_list.fxml", "Member List");
    }

    @FXML
    private void loadBookTable(ActionEvent event) {
        loadWindow("/library/assistant/ui/listbook/book_list.fxml", "Book List");
    }
    
    @FXML
    private void loadSettings(ActionEvent event) {
        loadWindow("/library/assistant/settings/settings.fxml", "Settings");
    }

    void loadWindow(String loc, String title) {
        try {
            Parent parent = FXMLLoader.load(getClass().getResource(loc));
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(title);
            stage.setScene(new Scene(parent));
            stage.show();
            LibraryAssistantUtil.setStageIcon(stage);

        } catch (IOException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadBookInfo(ActionEvent event) {
        clearBookCache();

        String id = bookIDinput.getText();
        String qu = "SELECT *FROM BOOK WHERE id ='" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;
        try {
            while (rs.next()) {
                String bName = rs.getString("title");
                String bAuthor = rs.getString("author");
                Boolean bStatus = rs.getBoolean("isAvail");

                bookName.setText(bName);
                bookAuthor.setText(bAuthor);
                String status = (bStatus) ? "Available" : "Not Available";
                bookStatus.setText(status);
                flag = true;
            }
            if (!flag) {
                bookName.setText("No Such Book Available");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void clearBookCache() {
        bookName.setText("");
        bookAuthor.setText("");
        bookStatus.setText("");
    }

    void clearMemberCache() {
        memberName.setText("");
        memberMobile.setText("");

    }

    @FXML
    private void loadMemberInfo(ActionEvent event) {
        clearMemberCache();

        String id = memberIDInput.getText();
        String qu = "SELECT * FROM MEMBER WHERE id = '" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        Boolean flag = false;
        try {
            while (rs.next()) {
                String mName = rs.getString("name");
                String mMobile = rs.getString("mobile");

                memberName.setText(mName);
                memberMobile.setText(mMobile);

                flag = true;
            }
            if (!flag) {
                memberName.setText("NO Such Member Available");
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @FXML
    private void loadIssueOperation(ActionEvent event) {
        String memberID = memberIDInput.getText();
        String bookID = bookIDinput.getText();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Issue Operation");
        alert.setHeaderText(null);
        alert.setContentText("Are you sure you want to issue the book " + bookName.getText() + "\n to " + memberName.getText() + "?");

        Optional<ButtonType> response = alert.showAndWait();
        if (response.get() == ButtonType.OK) {
            String str = "INSERT INTO ISSUE(memberID,bookID) VALUES ("
                    + "'" + memberID + "',"
                    + "'" + bookID + "')";
            String str2 = "UPDATE BOOK SET isAvail = false WHERE id = '" + bookID + "'";
            System.out.println(str + " and " + str2);
            if (databaseHandler.execAction(str) && databaseHandler.execAction(str2)) {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Issue Complete");
                alert1.showAndWait();
            } else {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Book Issue Operation Failed");
                alert1.showAndWait();
            }
        } else {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
            alert1.setTitle("Cancelled");
            alert1.setHeaderText(null);
            alert1.setContentText("Book Issue Operation Cancelled");
            alert1.showAndWait();
        }
    }

    @FXML
    private void loadBookInfo2(ActionEvent event) {
        ObservableList<String> issueData = FXCollections.observableArrayList();
        
        isReadyForSubmission=false;
        String id = bookID.getText();
        String qu = "SELECT * FROM ISSUE WHERE bookID ='" + id + "'";
        ResultSet rs = databaseHandler.execQuery(qu);
        try {
            while (rs.next()) {
                String mBookID = id;
                String mMemberID = rs.getString("memberID");
                Timestamp mIssueTime = rs.getTimestamp("issueTime");
                int mRenewCount = rs.getInt("renew_count");

                issueData.add("Issue Date and Time : " + mIssueTime.toLocalDateTime());
                issueData.add("Renew Count : " + mRenewCount);

                issueData.add("Book Information :- "); 

                qu = "SELECT * FROM BOOK WHERE id='"+mBookID+"'";
                ResultSet r1 = databaseHandler.execQuery(qu);
                while (r1.next()) {
                        issueData.add("\tBook Name :- " +r1.getString("title"));
                        issueData.add("\tBook ID :- " +r1.getString("id"));
                        issueData.add("\tBook Author :- " +r1.getString("author"));
                        issueData.add("\tBook Publisher :- " +r1.getString("publisher"));
                }
                qu = "SELECT * FROM MEMBER WHERE id ='"+ mMemberID+"'";
                r1 = databaseHandler.execQuery(qu);
                  issueData.add("Member Information :- ");
                while (r1.next()) {
                    issueData.add("Name :"+r1.getString("name"));
                    issueData.add("Mobile :"+r1.getString("mobile"));
                    issueData.add("Email :"+r1.getString("email"));
                }
               isReadyForSubmission=true; 
            }
        } catch (SQLException ex) {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
        issueDataList.getItems().setAll(issueData);
    }

    @FXML
    private void loadSubmissionOp(ActionEvent event) {
        
       
        if(!isReadyForSubmission)
        {
             Alert alert = new Alert(Alert.AlertType.ERROR);
             alert.setTitle("Failed");
             alert.setHeaderText(null);
             alert.setContentText("Please select a book to submit");
             alert.showAndWait();   
          return;
        }
             Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
             alert.setTitle("Confirm Submission Operation ");
             alert.setHeaderText(null);
             alert.setContentText("Are you sure you want to submit the book?");
             Optional<ButtonType> response = alert.showAndWait();
        if(response.get()==ButtonType.OK)
        {
            String id =bookID.getText();
            String ac1="DELETE FROM ISSUE WHERE bookID='" + id +"'";
            String ac2="UPDATE BOOK SET ISAVAIL = TRUE WHERE id='" + id +"'";

            if(databaseHandler.execAction(ac1)&&databaseHandler.execAction(ac2))
            {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Success");
                alert1.setHeaderText(null);
                alert1.setContentText("Book has been successfully submitted");
                alert1.showAndWait();
            }
            else
            {
                Alert alert1 = new Alert(Alert.AlertType.ERROR);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Submission has been failed");
                alert1.showAndWait();
            }
        }
        else
        {
            Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Submission operation cancelled");
                alert1.showAndWait();
        }
    }

    @FXML
    private void loadRenewOp(ActionEvent event) {
             if(!isReadyForSubmission)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Failed");
                alert.setHeaderText(null);
                alert.setContentText("Please select a book to renew");
                alert.showAndWait();   
                return;
            }
        
             Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
             alert.setTitle("Confirm Renew Operation ");
             alert.setHeaderText(null);
             alert.setContentText("Are you sure you want to renew the book?");
             Optional<ButtonType> response = alert.showAndWait();
             if(response.get()==ButtonType.OK)
             {
                 String ac="UPDATE ISSUE SET issueTime = CURRENT_TIMESTAMP, renew_count = renew_count+1 WHERE bookID='" + bookID.getText() +"'";
                 System.out.println(ac);
                if(databaseHandler.execAction(ac))
                {
                    Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                    alert1.setTitle("Success");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Book has been renewed");
                    alert1.showAndWait();
                }
                else
                {
                    Alert alert1 = new Alert(Alert.AlertType.ERROR);
                    alert1.setTitle("Failed");
                    alert1.setHeaderText(null);
                    alert1.setContentText("Renew has been failed");
                    alert1.showAndWait();
                }
            }
            else
            {
                Alert alert1 = new Alert(Alert.AlertType.INFORMATION);
                alert1.setTitle("Failed");
                alert1.setHeaderText(null);
                alert1.setContentText("Renew operation cancelled");
                alert1.showAndWait();
            }
    }   

    @FXML
    private void handleMenuClose(ActionEvent event) {
        ((Stage)rootPane.getScene().getWindow()).close();
    }

    @FXML
    private void handleMenuAddBook(ActionEvent event) {
        loadWindow("/library/assistant/ui/addBook/add_book.fxml", "Add New Book");
    }

    @FXML
    private void handleMenuAddMember(ActionEvent event) {
        loadWindow("/library/assistant/ui/addmember/member_add.fxml", "Add New Member");
    }

    @FXML
    private void handleMenuViewBook(ActionEvent event) {
        loadWindow("/library/assistant/ui/listbook/book_list.fxml", "Book List");
    }

    @FXML
    private void handleMenuViewMember(ActionEvent event) {
        loadWindow("/library/assistant/ui/listmember/member_list.fxml", "Member List");
    }

    @FXML
    private void handleMenuFullScreen(ActionEvent event) {
        Stage stage = ((Stage)rootPane.getScene().getWindow());
        stage.setFullScreen(!stage.isFullScreen());
    }

}