
package library.assistant.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import library.assistant.ui.listbook.BookListController.Book;
import library.assistant.ui.listmember.MemberListController;
import library.assistant.ui.listmember.MemberListController.Member;

public final class DatabaseHandler {
     private static DatabaseHandler handler=null;
    
    private static final String DB_URL = "jdbc:derby:database;create=true";
    private static Connection conn =null;
    private static Statement stmt =null;
    
    private DatabaseHandler(){
        createConnection();
        setupBookTable(); 
        setupMemberTable();
        setupIssueTable();
    }
    public static DatabaseHandler getInstance()
    {
        if(handler==null)
        {
            handler =new DatabaseHandler();
        }
        return handler;
        
    }

    void createConnection(){
        try{
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            conn=DriverManager.getConnection(DB_URL);
        }catch(Exception e){
             JOptionPane.showMessageDialog(null,"Can't load Database","Database Error", JOptionPane.ERROR_MESSAGE);
             System.exit(0);
        }
    }
    void setupBookTable(){
        String TABLE_NAME="BOOK";
        try{
            stmt=conn.createStatement();
            
            DatabaseMetaData dbm =conn.getMetaData();
            ResultSet tables =dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);
            
            if(tables.next())
            {
                System.out.println(" TABLE " + TABLE_NAME + " already exists. Ready for go!");
            }else{
                stmt.execute("Create TABLE "+TABLE_NAME+"("
                + "      id varchar(200) primary key,\n"
                + "      title varchar(200),\n "
                + "      author varchar(200),\n "
                + "      publisher varchar(100),\n "
                + "      isAvail boolean default true"
                +")");                
            }
    
        } catch (SQLException e) {
           System.err.println(e.getMessage() + "...setupDatabase");
        }finally{       
        }
    }
    void setupMemberTable(){
        String TABLE_NAME="MEMBER";
        try{
            stmt=conn.createStatement();
            
            DatabaseMetaData dbm =conn.getMetaData();
            ResultSet tables =dbm.getTables(null,null,TABLE_NAME.toUpperCase(),null);
            
            if(tables.next())
            {
                System.out.println(" TABLE " + TABLE_NAME + " already exists. Ready for go!");
            }else{
                stmt.execute("Create "+ TABLE_NAME + "("
                + "      id varchar(200) primary key,\n"
                + "      name varchar(200),\n "
                + "      mobile varchar(20),\n "
                + "      email varchar(100)"
                +")");                
            }
    
        } catch (SQLException e) {
           System.err.println(e.getMessage() + "...setupDatabase");
        }finally{       
        }
    }
    void setupIssueTable()
    {
        String TABLE_NAME = "ISSUE";
        try
        {
            stmt = conn.createStatement();
            DatabaseMetaData dbm = conn.getMetaData();
            ResultSet tables = dbm.getTables(null, null, TABLE_NAME.toUpperCase(), null);
            if(tables.next())
            {
                System.out.println("TABLE " + TABLE_NAME + " already exists. Ready for go!");
            }else{
                stmt.execute("CREATE TABLE "+ TABLE_NAME + "("
                + "      bookID varchar(200) primary key,\n"
                + "      memberID varchar(200),\n "
                + "      issueTime timestamp default CURRENT_TIMESTAMP,\n "
                + "      renew_count integer default 0,\n"
                + "      FOREIGN KEY (bookID) REFERENCES BOOK(id),\n"
                + "      FOREIGN KEY (memberID) REFERENCES MEMBER(id)"
                +")");                
            }
    
        } catch (SQLException e) {
           System.err.println(e.getMessage() + "...setupDatabase");
        }finally{       
        }
        }
    
     public ResultSet execQuery(String query) {
        ResultSet result;
        try {
            stmt = conn.createStatement();
            result = stmt.executeQuery(query);
        }
        catch (SQLException ex) {
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return null;
        }
        finally {
        }
        return result;
    }
      
       public boolean execAction(String qu) {
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(qu);
            return true;
        }catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error:" + ex.getMessage(), "Error Occured", JOptionPane.ERROR_MESSAGE);
            System.out.println("Exception at execQuery:dataHandler" + ex.getLocalizedMessage());
            return false;
        }
        finally {
        }
    }
       public boolean deleteBook(Book book)
       {
         try {
             
             String deleteStatement = "DELETE FROM BOOK WHERE ID=?";
             PreparedStatement stmt = conn.prepareStatement(deleteStatement);
             stmt.setString(1, book.getId());
             int res = stmt.executeUpdate();
             if(res==1)
             {
                 return true;
             }
             
         } catch (SQLException ex) {
             Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
         }
         return false;  
       }
       
       public boolean deleteMember(Member member)
       {
         try {
             
             String deleteStatement = "DELETE FROM MEMBER WHERE ID=?";
             PreparedStatement stmt = conn.prepareStatement(deleteStatement);
             stmt.setString(1, member.getName());
             int res = stmt.executeUpdate();
             if(res==1)
             {
                 return true;
             }
             
         } catch (SQLException ex) {
             Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
         }
         return false;  
       }
       
      public boolean isAlreadyIssued(Book book)
       {        
            try {

                  String checkstmt = "SELECT COUNT(*) FROM ISSUE WHERE bookid=?";
                  PreparedStatement stmt = conn.prepareStatement(checkstmt);
                  stmt.setString(1, book.getId());
                  ResultSet rs = stmt.executeQuery();
                  if(rs.next())
                  {
                      int count = rs.getInt(1);
                      System.out.println(count);
                      return (count>0);
                  }
              } catch (SQLException ex) {
                  Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
              }
              return false;  
       }       
       
      public boolean updateBook(Book book)
      {
          try {
              String update = "UPDATE BOOK SET TITLE=?, AUTHOR=?, PUBLISHER=? WHERE ID=?";
             PreparedStatement stmt = conn.prepareStatement(update);
             stmt.setString(1, book.getTitle());
             stmt.setString(2, book.getAuthor());
             stmt.setString(3, book.getPublisher());
             stmt.setString(4, book.getId());
             int res = stmt.executeUpdate();
             return (res>0);
          }
          catch (SQLException ex) {
             Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
         }
        return false;
      }
      
      public boolean updateMember(MemberListController.Member member)
      {
          try {
              String update = "UPDATE MEMBER SET NAME=?, EMAIL=?, MOBILE=? WHERE ID=?";
             PreparedStatement stmt = conn.prepareStatement(update);
             stmt.setString(1, member.getName());
             stmt.setString(2, member.getEmail());
             stmt.setString(3, member.getMobile());
             stmt.setString(4, member.getId());
             int res = stmt.executeUpdate();
             return (res>0);
          }
          catch (SQLException ex) {
             Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
         }
        return false;
      }

    public boolean updateBook(Member member) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
      
}