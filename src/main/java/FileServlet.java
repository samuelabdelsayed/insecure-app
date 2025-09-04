import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.nio.file.*;
import java.util.zip.*;

/**
 * This servlet contains deliberate path traversal vulnerabilities for SAST testing
 */
@WebServlet("/files")
public class FileServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    // Base directory for file operations - hardcoded path
    private static final String BASE_DIR = "/tmp/userfiles/";
    
    // Archive extraction directory
    private static final String EXTRACT_DIR = "/tmp/extracted/";
    
    public FileServlet() {
        super();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("file");
        
        // Path traversal vulnerability - directly using user input in file path
        try {
            // No validation of user input before using it in file path
            File file = new File(BASE_DIR + fileName);
            
            if (file.exists() && file.isFile()) {
                // Set content type based on file extension
                String contentType = getServletContext().getMimeType(file.getName());
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                
                response.setContentType(contentType);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
                
                // Copy file to output stream
                try (FileInputStream in = new FileInputStream(file);
                     OutputStream out = response.getOutputStream()) {
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = in.read(buffer)) != -1) {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error accessing file");
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String path = request.getParameter("path");
        
        if ("read".equals(action)) {
            // Path traversal in different method
            readFile(response, path);
        } else if ("write".equals(action)) {
            String content = request.getParameter("content");
            // Path traversal in write operation
            writeFile(response, path, content);
        } else if ("delete".equals(action)) {
            // Path traversal in delete operation
            deleteFile(response, path);
        } else if ("extract".equals(action)) {
            // Path traversal in ZIP extraction (Zip Slip vulnerability)
            extractZipFile(response, path);
        } else {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid action");
        }
    }
    
    // Path traversal in read operation
    private void readFile(HttpServletResponse response, String filePath) throws IOException {
        try {
            // Unsafe file access - concatenating paths
            File file = new File(BASE_DIR, filePath);
            
            if (file.exists() && file.isFile()) {
                response.setContentType("text/plain");
                
                try (BufferedReader reader = new BufferedReader(new FileReader(file));
                     PrintWriter writer = response.getWriter()) {
                    
                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.println(line);
                    }
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error reading file");
            e.printStackTrace();
        }
    }
    
    // Path traversal in write operation
    private void writeFile(HttpServletResponse response, String filePath, String content) throws IOException {
        try {
            // Unsafe file access - directly using user input
            File file = new File(BASE_DIR + filePath);
            
            // Ensure directory exists
            file.getParentFile().mkdirs();
            
            try (FileWriter writer = new FileWriter(file)) {
                writer.write(content);
            }
            
            response.setContentType("text/plain");
            response.getWriter().println("File written successfully");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error writing file");
            e.printStackTrace();
        }
    }
    
    // Path traversal in delete operation
    private void deleteFile(HttpServletResponse response, String filePath) throws IOException {
        try {
            // Unsafe file access
            File file = new File(BASE_DIR + "/" + filePath);
            
            if (file.exists()) {
                if (file.delete()) {
                    response.setContentType("text/plain");
                    response.getWriter().println("File deleted successfully");
                } else {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete file");
                }
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            }
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error deleting file");
            e.printStackTrace();
        }
    }
    
    // Zip Slip vulnerability
    private void extractZipFile(HttpServletResponse response, String zipFilePath) throws IOException {
        try {
            File zipFile = new File(BASE_DIR, zipFilePath);
            
            if (!zipFile.exists()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Zip file not found");
                return;
            }
            
            // Create extraction directory if it doesn't exist
            File extractDir = new File(EXTRACT_DIR);
            if (!extractDir.exists()) {
                extractDir.mkdirs();
            }
            
            // Vulnerable ZIP extraction - doesn't validate paths
            try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFile))) {
                ZipEntry entry;
                while ((entry = zipIn.getNextEntry()) != null) {
                    String entryName = entry.getName();
                    
                    // Vulnerable: no validation if entry path is within target directory
                    File outputFile = new File(EXTRACT_DIR, entryName);
                    
                    if (entry.isDirectory()) {
                        outputFile.mkdirs();
                    } else {
                        // Create parent directories if they don't exist
                        outputFile.getParentFile().mkdirs();
                        
                        // Extract file
                        try (FileOutputStream out = new FileOutputStream(outputFile)) {
                            byte[] buffer = new byte[4096];
                            int bytesRead;
                            while ((bytesRead = zipIn.read(buffer)) != -1) {
                                out.write(buffer, 0, bytesRead);
                            }
                        }
                    }
                    zipIn.closeEntry();
                }
            }
            
            response.setContentType("text/plain");
            response.getWriter().println("Zip file extracted successfully");
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error extracting zip file");
            e.printStackTrace();
        }
    }
}