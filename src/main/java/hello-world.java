package hello;

public class HelloWorld
{
  static void insecure(String user, String pass) 
  {
    System.out.println("Here is my " + user + "password! " + pass);
  } 
  public static void main(String[] args) 
  {
    String username = "silly";
    String password = "insecure_password_123";
    
    System.out.println("Hello, World!"); 
    insecure(username, password);
  }
}
