/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package library.assistant.settings;

import library.assistant.alert.AlertMaker;
import com.google.gson.Gson;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;
import org.apache.commons.codec.digest.DigestUtils;


/**
 *
 * @author Divya Jain
 */
public class Preferences {
    public static final String CONFIG_FILE = "config.txt";
    int nDaysWithoutFine;
    float finePerDay;
    String username;
    String password;
    
    public Preferences()
    {
        nDaysWithoutFine = 14;
        finePerDay = 2;
        username = "admin";
        setPassword("admin");
    }

    public int getnDaysWithoutFine() {
        return nDaysWithoutFine;
    }

    public void setnDaysWithoutFine(int nDaysWithoutFine) {
        this.nDaysWithoutFine = nDaysWithoutFine;
    }

    public float getFinePerDay() {
        return finePerDay;
    }

    public void setFinePerDay(float finePerDay) {
        this.finePerDay = finePerDay;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if(password.length()<16){
        this.password = DigestUtils.shaHex(password);
    }else{
            this.password = password;
        }
    }
    public static void initConfig()
    {
        Writer writer = null;
        try
        {
            Preferences preference = new Preferences();
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference,writer);
        }
        catch(IOException ex)
        {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE,null,ex);
        }
        finally
        {
            try
            {
                writer.close();
            }
            catch(IOException ex)
            {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE,null,ex);
            }
        }
                
    }
    public static Preferences getPreferences(){
        Gson gson = new Gson();
        Preferences preferences =new Preferences();
        try {
            preferences =gson.fromJson(new FileReader(CONFIG_FILE), Preferences.class);
        } catch (FileNotFoundException ex) {
            initConfig();
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE, null, ex);
        }
        return preferences;
    }
    
    public static void writePreferenceToFile(Preferences preference)
    {
        Writer writer = null;
        try
        {
            Gson gson = new Gson();
            writer = new FileWriter(CONFIG_FILE);
            gson.toJson(preference,writer);
          AlertMaker.showSimpleAlert("Success","Settings updated");
        }
        catch(IOException ex)
        {
            Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE,null,ex);
            AlertMaker.showErrorMessage(ex, "Failed","Cant Save configuration file ");
        }
        finally
        {
            try
            {
                writer.close();
            }
            catch(IOException ex)
            {
                Logger.getLogger(Preferences.class.getName()).log(Level.SEVERE,null,ex);
            }
        }
    }
}