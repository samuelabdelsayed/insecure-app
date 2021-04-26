package application;

import javax.swing.JOptionPane;
import java.sql.*;

class insecure
{
  static void show(String user, String pass) 
  {
    System.out.println("Here is my " + user + " " + pass + "!" );
    try 
    {
      String url = "jdbc:msql://12.34.56.78:910/App";
      Connection conn = DriverManager.getConnection(url,"","");
      Statement stmt = conn.createStatement();
      ResultSet rs;
 
      rs = stmt.executeQuery("SELECT Lname FROM Client WHERE Uname = '" + user + "'");
      while ( rs.next() ) 
      {
        String person = rs.getString("Uname");
        System.out.println(person);
      }
      conn.close();
     } 
     catch (Exception e) 
     {
          System.err.println("Got an exception! ");
          System.err.println(e.getMessage());
     }
  } 
  public static void main(String[] args)
  {
    String username, user;
    String password, pass;

	  username = JOptionPane.showInputDialog("username");
	  user = String.parseString(username);

    password = JOptionPane.showInputDialog("password");
	  pass = String.parseString(password);

    show(user, pass);
  }
}
