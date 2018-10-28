/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.assistant.ui.addmember;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import library.assistant.alert.AlertMaker;
import library.assistant.database.DatabaseHandler;
import library.assistant.ui.listmember.MemberListController;
import library.assistant.ui.listmember.MemberListController.Member;

/**
 * FXML Controller class
 *
 * @author Divya Jain
 */
public class MemberAddController implements Initializable {
    DatabaseHandler handler;
    @FXML
    private JFXTextField name;
    @FXML
    private JFXTextField id;
    @FXML
    private JFXTextField mobile;
    @FXML
    private JFXTextField email;
    @FXML
    private JFXButton SaveButton;
    @FXML
    private JFXButton CancelButton;

    private boolean isInEditMode = false;    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        handler = DatabaseHandler.getInstance();
    }    

    @FXML
    private void addMember(ActionEvent event) {
        String mName = name.getText();
        String mID = id.getText();
        String mMobile = mobile.getText();
        String mEmail = email.getText();
        
        Boolean flag = mName.isEmpty()||mID.isEmpty()||mMobile.isEmpty()||mEmail.isEmpty();
        if(flag)
        {
              AlertMaker.showErrorMessage("Can't add member","Please select all fields");
//            Alert alert =new Alert(Alert.AlertType.ERROR);
//            alert.setHeaderText(null);
//            alert.setContentText("Please Enter in all Fields");
//            alert.showAndWait();
            return;
        }
        if(isInEditMode)
        {
            handleUpdateMember();
            return;
        }
 
        String st = "INSERT INTO MEMBER VALUES (" +
                "'" + mID +"',"+
                "'" + mName +"',"+
                "'" + mMobile +"',"+
                "'" + mEmail +"'"+
                ")";
        System.out.println(st);
        if(handler.execAction(st))
        {
            Alert alert =new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText("Saved");
            alert.showAndWait();
            return;
        }
        else
        {
            Alert alert =new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("Error Occured");
            alert.showAndWait();
            return;
        }
    }

    @FXML
    private void Cancel(ActionEvent event) {
        Stage stage = (Stage)name.getScene().getWindow();
        stage.close();
    }
    public void inflateUI(MemberListController.Member member)
    {
        name.setText(member.getName());
        id.setText(member.getId());
        id.setEditable(false);
        mobile.setText(member.getMobile());
        email.setText(member.getEmail());
        isInEditMode = Boolean.TRUE;
    }

    private void handleUpdateMember() {
         Member member = new MemberListController.Member(name.getText(),id.getText(),mobile.getText(),email.getText());
        if(DatabaseHandler.getInstance().updateMember(member))
        {
            AlertMaker.showSimpleAlert("Success", "Member updated.");
        }
        else{
            AlertMaker.showErrorMessage("Failed", "Cannot update the member");
        }
    }
}
