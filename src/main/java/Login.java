import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.Base64;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;

// This is the generic login class with deliberate vulnerabilities

public class Login extends JFrame implements ActionListener {
	JPanel panel;
	JLabel user_label, password_label, message;
	JTextField userName_text;
	JPasswordField password_text;
	JButton submit, cancel;
	
	// Hardcoded credentials for database access - SAST should flag this
	private static final String DB_USERNAME = "admin";
	private static final String DB_PASSWORD = "super_secret_password123";
	
	// Weak encryption key - SAST should flag this
	private static final String ENCRYPTION_KEY = "1234567890abcdef";
	
	// Demo credentials for testing (username:password)
	// - user:password
	// - admin:admin123
	// - test:test123

	Login() {
		// Set color scheme
		Color primaryColor = new Color(41, 128, 185); // Blue
		Color secondaryColor = new Color(44, 62, 80); // Dark Blue/Gray
		Color accentColor = new Color(231, 76, 60);   // Red
		Color lightColor = new Color(236, 240, 241);  // Light Gray
		
		user_label = new JLabel();
		user_label.setText("User Name:");
		user_label.setForeground(secondaryColor);
		user_label.setFont(new Font("Arial", Font.BOLD, 14)); // Increased font size
		userName_text = new JTextField();
		userName_text.setPreferredSize(new Dimension(200, 30)); // Set preferred size
		userName_text.setFont(new Font("Arial", Font.PLAIN, 14)); // Increased font size
		
		password_label = new JLabel();
		password_label.setText("Password:");
		password_label.setForeground(secondaryColor);
		password_label.setFont(new Font("Arial", Font.BOLD, 14)); // Increased font size
		password_text = new JPasswordField();
		password_text.setPreferredSize(new Dimension(200, 30)); // Set preferred size
		password_text.setFont(new Font("Arial", Font.PLAIN, 14)); // Increased font size
		
		submit = new JButton("SUBMIT");
		submit.setBackground(new Color(20, 100, 180)); // Darker blue
		submit.setForeground(new Color(255, 255, 220)); // Light yellow-white for contrast
		submit.setFont(new Font("Arial", Font.BOLD, 14)); 
		submit.setFocusPainted(false);
		submit.setBorderPainted(true);
		submit.setBorder(BorderFactory.createLineBorder(new Color(0, 70, 140), 2)); // Dark blue border
		submit.setCursor(new Cursor(Cursor.HAND_CURSOR));
		submit.setPreferredSize(new Dimension(120, 35));
		
		cancel = new JButton("CANCEL");
		cancel.setBackground(new Color(200, 30, 30)); // Darker red
		cancel.setForeground(new Color(255, 255, 220)); // Light yellow-white for contrast
		cancel.setFont(new Font("Arial", Font.BOLD, 14));
		cancel.setFocusPainted(false);
		cancel.setBorderPainted(true);
		cancel.setBorder(BorderFactory.createLineBorder(new Color(150, 10, 10), 2)); // Dark red border
		cancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
		cancel.setPreferredSize(new Dimension(120, 35));
		
		JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
		inputPanel.setBackground(lightColor);
		inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		inputPanel.add(user_label);
		inputPanel.add(userName_text);
		inputPanel.add(password_label);
		inputPanel.add(password_text);
		
		// Create a panel for buttons with a colored background
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
		buttonPanel.setBackground(new Color(41, 128, 185)); // Same blue as header
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
		buttonPanel.add(submit);
		buttonPanel.add(cancel);
		
		message = new JLabel("", JLabel.CENTER);
		message.setForeground(accentColor);
		message.setFont(new Font("Arial", Font.ITALIC, 12));
		
		// Panel for the message between content and buttons
		JPanel messagePanel = new JPanel(new BorderLayout());
		messagePanel.setBackground(lightColor);
		messagePanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		messagePanel.add(message, BorderLayout.CENTER);
		
		panel = new JPanel(new BorderLayout(5, 0));
		panel.setBackground(lightColor);
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		// Header with title
		JPanel headerPanel = new JPanel(new BorderLayout());
		headerPanel.setBackground(new Color(41, 128, 185)); // Blue header
		headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		JLabel title = new JLabel("Insecure E-Commerce Login", JLabel.CENTER);
		title.setFont(new Font("Arial", Font.BOLD, 18));
		title.setForeground(Color.WHITE); // White text on blue background
		
		headerPanel.add(title, BorderLayout.CENTER);
		
		panel.add(headerPanel, BorderLayout.NORTH);
		panel.add(inputPanel, BorderLayout.CENTER);
		panel.add(messagePanel, BorderLayout.SOUTH);
		panel.add(buttonPanel, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		submit.addActionListener(this);
		cancel.addActionListener(e -> resetFields());
		
		add(panel, BorderLayout.CENTER);
		setTitle("Insecure E-Commerce - Login");
		setSize(550, 300);  // Increased window size
		setMinimumSize(new Dimension(500, 280));  // Set minimum size
		setLocationRelativeTo(null);
		setVisible(true);
		
		// Insecure cookie creation - SAST should flag this
		setCookie("auth", "session123", false);
	}
	
	public void resetFields() {
		userName_text.setText("");
		password_text.setText("");
		message.setText("");
	}

	public static void main(String[] args) {
		// Insecure SSL/TLS - SAST should flag this
		disableSSLVerification();
		new Login();
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		String user = userName_text.getText();
		String password = new String(password_text.getPassword());
		
		// SQL Injection vulnerability (multiple instances) - SAST should flag these
		try {
			String url = "jdbc:msql://12.34.56.78:910/App";
			Connection conn = DriverManager.getConnection(url, DB_USERNAME, DB_PASSWORD);
			Statement stmt = conn.createStatement();
			ResultSet rs;
			
			// Multiple SQL injection vulnerabilities
			rs = stmt.executeQuery("SELECT * FROM users WHERE username = '" + user + "' AND password = '" + password + "'");
			
			if (rs.next()) {
				// Successful login
				message.setText("Login successful for: " + user);
				
				// Additional SQL injection vulnerability
				String query = "UPDATE users SET last_login = NOW() WHERE id = " + rs.getString("id");
				stmt.executeUpdate(query);
				
				// Another SQL injection vulnerability with different pattern
				PreparedStatement badPstmt = conn.prepareStatement("SELECT * FROM user_data WHERE user_id = " + rs.getInt("id"));
				badPstmt.executeQuery();
				
				// More SQL injection examples for different detection patterns
				String userId = rs.getString("id");
				Statement stmt2 = conn.createStatement();
				stmt2.executeQuery("SELECT * FROM permissions WHERE user_id = " + userId);
				
				// Launch the Dashboard
				Dashboard dashboard = new Dashboard(user, this);
				dashboard.setVisible(true);
				this.setVisible(false);
				
			} else {
				message.setText("Invalid username or password");
			}
			
			conn.close();
		} catch (Exception e) {
			System.err.println("Got an exception! ");
			System.err.println(e.getMessage());
			
			// For demo purposes, allow login with hardcoded credentials when DB fails
			if ((user.trim().equals("admin") && password.trim().equals("admin123")) ||
				(user.trim().equals("user") && password.trim().equals("password")) ||
				(user.trim().equals("test") && password.trim().equals("test123"))) {
				
				message.setText("Login successful for: " + user);
				
				// Launch the Dashboard
				Dashboard dashboard = new Dashboard(user, this);
				dashboard.setVisible(true);
				this.setVisible(false);
			} else {
				message.setText("Invalid username or password");
			}
		}
		
		// Log user attempts to a file with sensitive data - SAST should flag this
		logLoginAttempt(user, new String(password_text.getPassword()));
	}
	
	// Weak encryption method - SAST should flag this
	private String encryptPassword(String password) {
		try {
			// Using weak algorithm (DES instead of AES)
			Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
			SecretKeySpec secretKey = new SecretKeySpec(ENCRYPTION_KEY.getBytes(), "DES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return Base64.getEncoder().encodeToString(cipher.doFinal(password.getBytes()));
		} catch (Exception e) {
			return password; // Fallback to plaintext on error - SAST should flag this
		}
	}
	
	// Insecure cookie method - SAST should flag this
	private void setCookie(String name, String value, boolean secure) {
		// Implementation would set insecure cookies in a real web app context
		System.out.println("Setting cookie: " + name + "=" + value + " (secure: " + secure + ")");
	}
	
	// Logging sensitive data - SAST should flag this
	private void logLoginAttempt(String username, String password) {
		try {
			File file = new File("login_attempts.log");
			FileWriter writer = new FileWriter(file, true);
			writer.write("Login attempt - Username: " + username + ", Password: " + password + ", Time: " + new java.util.Date() + "\n");
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	// Insecure SSL/TLS configuration - SAST should flag this
	private static void disableSSLVerification() {
		try {
			// Create a trust manager that does not validate certificate chains
			TrustManager[] trustAllCerts = new TrustManager[] {
				new X509TrustManager() {
					public java.security.cert.X509Certificate[] getAcceptedIssuers() { return null; }
					public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
					public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) { }
				}
			};
			
			// Install the all-trusting trust manager
			SSLContext sc = SSLContext.getInstance("SSL");
			sc.init(null, trustAllCerts, new java.security.SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
			
			// Create and install host name verifier that trusts all hosts
			HostnameVerifier allHostsValid = new HostnameVerifier() {
				public boolean verify(String hostname, SSLSession session) { return true; }
			};
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}