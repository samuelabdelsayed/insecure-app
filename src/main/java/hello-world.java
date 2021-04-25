package hello;

class HelloWorld 
{
  static void insecure(String user, String pass) 
  {
    System.out.println("Here is my " + user + " " + pass + "!" );
  } 
  
  public static void main(String[] args) 
  {
    String username = "silly";
    String password = "insecure_password";
    
    insecure(username, password);
  }
}
