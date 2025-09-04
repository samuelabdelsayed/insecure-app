import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.net.ssl.*;
import java.net.*;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3Client;

/**
 * This class contains various hardcoded credentials for SAST testing
 */
public class ConfigManager {
    
    // Database connection parameters with hardcoded credentials
    private static final String DB_URL = "jdbc:mysql://production-db.example.com:3306/customer_data";
    private static final String DB_USER = "admin_user";
    private static final String DB_PASSWORD = "S3cr3tP@ssw0rd!"; 
    
    // API keys hardcoded
    private static final String API_KEY = "AIzaSyDkjfEiI98s5QW1JzJiYNQIdsz3-F3vme0";
    private static final String API_SECRET = "a6a8490427664832BG443d09672b5397";
    
    // AWS credentials hardcoded
    private static final String AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE";
    private static final String AWS_SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY";
    
    // FTP credentials hardcoded
    private static final String FTP_HOST = "ftp.example.com";
    private static final String FTP_USERNAME = "ftpuser";
    private static final String FTP_PASSWORD = "ftppassword123";
    
    // OAuth client credentials hardcoded
    private static final String OAUTH_CLIENT_ID = "387a4cdcbb28d532";
    private static final String OAUTH_CLIENT_SECRET = "e9fd8a5930flkdjsafabde39528abe";
    
    // Hardcoded encryption key
    private static final String ENCRYPTION_KEY = "0123456789abcdef";
    private static final String ENCRYPTION_IV = "abcdef0123456789";
    
    // SSH private key as string
    private static final String SSH_PRIVATE_KEY = "-----BEGIN RSA PRIVATE KEY-----\n" +
            "MIIEowIBAAKCAQEAy8IsIbC9vxbI0OZfy94/okYvhM+dT9J3gBBYqgqZwG3AWB0P\n" +
            "Zn0XpipHkJFd0g6afFj8SSRSYrZBN/xOoYfge5vjbZKwF8ubHAC9s3pz+lY2+V8I\n" +
            "oQ5XVg4fmPxqulQg/PhxSl1fF79iJhwTFjpQfvJrNDTZCMqKCQiWM8kTYcFIlT5Q\n" +
            "xjPdyT92rXzXcyVlHLxEZBDSt8c19YFbHsN8V5ABb6Q+nH8/bR+B19WYzYvsBBJ+\n" +
            "... (shortened for brevity) ..." +
            "-----END RSA PRIVATE KEY-----";
    
    // JKS keystore password
    private static final String KEYSTORE_PASSWORD = "changeit";
    
    public static Connection getDatabaseConnection() throws SQLException {
        // Using hardcoded credentials directly in connection
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    public static Connection getAlternativeDatabaseConnection() throws SQLException {
        // Different style of hardcoded credentials
        Properties props = new Properties();
        props.setProperty("user", "root");
        props.setProperty("password", "mysql_root_password");
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/test", props);
    }
    
    public static void connectToApi() {
        try {
            URL url = new URL("https://api.example.com/data");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            
            // Adding hardcoded API key to header
            conn.setRequestProperty("X-Api-Key", API_KEY);
            conn.setRequestProperty("Authorization", "Bearer " + API_SECRET);
            
            conn.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static AmazonS3Client getS3Client() {
        // Creating AWS client with hardcoded credentials
        AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        return new AmazonS3Client(credentials);
    }
    
    public static void connectToFTP() {
        try {
            // Hardcoded FTP credentials in method
            String ftpUrl = "ftp://" + FTP_USERNAME + ":" + FTP_PASSWORD + "@" + FTP_HOST + "/public_html/";
            URL url = new URL(ftpUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Credentials embedded in connection string
    public static Connection getConnectionStringWithCredentials() throws SQLException {
        return DriverManager.getConnection("jdbc:postgresql://localhost:5432/testdb?user=postgres&password=postgres123");
    }
    
    // Commented credentials (many SAST tools will still detect these)
    public static void setupTestEnvironment() {
        // TODO: Remove before production
        // username: test_admin
        // password: test_password_123
        System.out.println("Setting up test environment");
    }
    
    // Environment-specific credentials with one being hardcoded
    public static String getPassword(String environment) {
        if ("development".equals(environment)) {
            return "dev_password";
        } else if ("testing".equals(environment)) {
            return "test_password";
        } else {
            return "Pr0duct!on_P@ssw0rd"; // Hardcoded production password
        }
    }
    
    // Main method with inline credentials
    public static void main(String[] args) {
        try {
            // Alternative form of hardcoded credentials
            String username = "system";
            String password = "manager";
            
            // Connection with inline credentials
            Connection conn = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:orcl", username, password);
            
            System.out.println("Connected successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}