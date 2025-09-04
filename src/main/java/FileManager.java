import java.io.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.security.*;

/**
 * This class demonstrates insecure file operations for SAST testing
 */
public class FileManager {
    
    private static final String DATA_DIR = "/tmp/app_data/";
    private static final String TEMP_DIR = "/tmp/";
    
    public FileManager() {
        // Create data directory with insecure permissions if it doesn't exist
        createDataDirectory();
    }
    
    // Creating directory with insecure world-writable permissions
    private void createDataDirectory() {
        try {
            File dataDir = new File(DATA_DIR);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                
                // Setting insecure permissions - world writable
                dataDir.setReadable(true, false); // readable by anyone
                dataDir.setWritable(true, false); // writable by anyone
                dataDir.setExecutable(true, false); // executable by anyone
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Insecure file creation with excessive permissions
    public void createUserDataFile(String username, String data) {
        try {
            File userFile = new File(DATA_DIR + username + "_data.txt");
            
            try (FileWriter writer = new FileWriter(userFile)) {
                writer.write(data);
            }
            
            // Setting insecure permissions - world readable and writable
            userFile.setReadable(true, false);
            userFile.setWritable(true, false);
            
            System.out.println("User data saved for: " + username);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Insecure temp file creation
    public File createTempFile(String prefix, String data) {
        try {
            // Insecure: using predictable file name in shared directory
            File tempFile = new File(TEMP_DIR + prefix + "_" + System.currentTimeMillis() + ".tmp");
            
            try (FileWriter writer = new FileWriter(tempFile)) {
                writer.write(data);
            }
            
            // Not using deleteOnExit() - potential temp file leak
            
            // Setting excessive permissions
            tempFile.setReadable(true, false);
            tempFile.setWritable(true, false);
            
            return tempFile;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Insecure file handling - race condition vulnerability
    public boolean updateConfigFile(String setting, String value) {
        File configFile = new File(DATA_DIR + "config.properties");
        File tempFile = new File(DATA_DIR + "config.properties.tmp");
        
        try {
            // Read existing config
            Properties props = new Properties();
            if (configFile.exists()) {
                try (FileInputStream fis = new FileInputStream(configFile)) {
                    props.load(fis);
                }
            }
            
            // Update property
            props.setProperty(setting, value);
            
            // Write to temp file
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                props.store(fos, "Updated configuration");
            }
            
            // TOCTOU vulnerability: Time gap between these operations
            if (configFile.exists()) {
                configFile.delete();
            }
            
            // Race condition vulnerability window
            return tempFile.renameTo(configFile);
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Insecure file permissions using Java NIO
    public void createSecretFile(String content) {
        try {
            Path path = Paths.get(DATA_DIR, "secret.txt");
            Files.write(path, content.getBytes());
            
            // Insecure permissions: world readable
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            perms.add(PosixFilePermission.GROUP_READ);
            perms.add(PosixFilePermission.OTHERS_READ); // Insecure
            
            Files.setPosixFilePermissions(path, perms);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Information leakage through file operations
    public void storeUserCredentials(String username, String password) {
        try {
            // Insecure: storing passwords in plaintext
            File credFile = new File(DATA_DIR + "credentials.txt");
            
            try (FileWriter writer = new FileWriter(credFile, true)) {
                writer.write(username + ":" + password + "\\n");
            }
            
            // Insecure permissions
            credFile.setReadable(true, false); // Readable by anyone
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // File link vulnerability
    public File accessUserFile(String filename) {
        // Insecure: no symlink or hard link checking
        File file = new File(DATA_DIR + filename);
        
        if (file.exists()) {
            return file;
        }
        
        return null;
    }
    
    // Insecure file deletion that doesn't actually remove sensitive data
    public boolean deleteFile(String filename) {
        File file = new File(DATA_DIR + filename);
        
        // Insecure deletion - doesn't secure wipe contents
        return file.delete();
    }
    
    // Insecure file locking
    public void lockFile(String filename) {
        try {
            File file = new File(DATA_DIR + filename);
            
            // Using RandomAccessFile for file locking
            try (RandomAccessFile raf = new RandomAccessFile(file, "rw")) {
                // Try to obtain exclusive lock but doesn't check lock result
                raf.getChannel().tryLock();
                
                // Proceed without checking if lock was successful
                raf.writeUTF("Updated data");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Insecure file resource handling - leak file descriptors
    public String readFileContent(String filename) {
        StringBuilder content = new StringBuilder();
        BufferedReader reader = null;
        
        try {
            File file = new File(DATA_DIR + filename);
            reader = new BufferedReader(new FileReader(file));
            
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\\n");
            }
            
            // Resource leak: not closing reader in finally block
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return content.toString();
    }
    
    public static void main(String[] args) {
        FileManager manager = new FileManager();
        manager.createUserDataFile("testuser", "sensitive data");
        manager.storeUserCredentials("admin", "admin123");
    }
}