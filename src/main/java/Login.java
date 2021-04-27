import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class Login extends JFrame implements ActionListener 
{
	JPanel panel;
	JLabel user_label, password_label, message;
	JTextField userName_text;
	JPasswordField password_text;
	JButton submit, cancel;

	Login() 
	{
		user_label = new JLabel();
		user_label.setText("User Name :");
		userName_text = new JTextField();

		password_label = new JLabel();
		password_label.setText("Password :");
		password_text = new JPasswordField();

		submit = new JButton("SUBMIT");
		panel = new JPanel(new GridLayout(3, 1));
		panel.add(user_label);
		panel.add(userName_text);
		panel.add(password_label);
		panel.add(password_text);
		message = new JLabel();

		panel.add(message);
		panel.add(submit);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		submit.addActionListener(this);
		add(panel, BorderLayout.CENTER);
		setTitle("Please login here");
		setSize(450,350);
		setVisible(true);
	}
	public static void main(String[] args) {
		new Login();
	}
	@Override
	public void actionPerformed(ActionEvent ae) 
	{
		String user = userName_text.getText();
		String pass = password_text.getText();
 
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
}
