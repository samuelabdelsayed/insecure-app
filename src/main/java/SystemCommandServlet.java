import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;

/**
 * This servlet demonstrates command injection vulnerabilities for SAST testing
 */
@WebServlet("/system")
public class SystemCommandServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    public SystemCommandServlet() {
        super();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>System Command Result</title></head><body>");
        
        if ("ping".equals(action)) {
            String host = request.getParameter("host");
            out.println("<h2>Ping Result:</h2>");
            out.println("<pre>");
            
            // Command injection vulnerability
            executePingCommand(out, host);
            
            out.println("</pre>");
        } else if ("netstat".equals(action)) {
            out.println("<h2>Network Status:</h2>");
            out.println("<pre>");
            
            // Command injection vulnerability
            executeNetstatCommand(out, request.getParameter("options"));
            
            out.println("</pre>");
        } else if ("lookup".equals(action)) {
            String domain = request.getParameter("domain");
            out.println("<h2>DNS Lookup Result:</h2>");
            out.println("<pre>");
            
            // Command injection vulnerability
            executeDnsLookup(out, domain);
            
            out.println("</pre>");
        } else if ("file".equals(action)) {
            String file = request.getParameter("filename");
            out.println("<h2>File Content:</h2>");
            out.println("<pre>");
            
            // Command injection vulnerability
            showFileContent(out, file);
            
            out.println("</pre>");
        } else {
            out.println("<h2>Available Commands:</h2>");
            out.println("<ul>");
            out.println("<li><a href=\"?action=ping&host=example.com\">Ping</a></li>");
            out.println("<li><a href=\"?action=netstat&options=-an\">Network Status</a></li>");
            out.println("<li><a href=\"?action=lookup&domain=example.com\">DNS Lookup</a></li>");
            out.println("<li><a href=\"?action=file&filename=/etc/passwd\">View File</a></li>");
            out.println("</ul>");
        }
        
        out.println("</body></html>");
    }
    
    // Command injection vulnerability - directly using user input in command
    private void executePingCommand(PrintWriter out, String host) {
        try {
            if (host == null || host.isEmpty()) {
                out.println("Host parameter is required");
                return;
            }
            
            // Command injection vulnerability: user input directly in command string
            String command = "ping -c 4 " + host;
            
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }
            
            reader.close();
            process.waitFor();
        } catch (Exception e) {
            out.println("Error executing command: " + e.getMessage());
        }
    }
    
    // Command injection vulnerability with different pattern
    private void executeNetstatCommand(PrintWriter out, String options) {
        try {
            String command = "netstat";
            
            if (options != null && !options.isEmpty()) {
                // Command injection vulnerability: user input appended to command
                command += " " + options;
            }
            
            // Using ProcessBuilder but still vulnerable
            ProcessBuilder pb = new ProcessBuilder(command.split(" "));
            Process process = pb.start();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }
            
            reader.close();
            process.waitFor();
        } catch (Exception e) {
            out.println("Error executing command: " + e.getMessage());
        }
    }
    
    // Command injection vulnerability using shell invocation
    private void executeDnsLookup(PrintWriter out, String domain) {
        try {
            if (domain == null || domain.isEmpty()) {
                out.println("Domain parameter is required");
                return;
            }
            
            // Command injection vulnerability: shell execution with user input
            String[] command = {"/bin/sh", "-c", "nslookup " + domain};
            
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }
            
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            while ((line = errorReader.readLine()) != null) {
                out.println("ERROR: " + line);
            }
            
            reader.close();
            errorReader.close();
            process.waitFor();
        } catch (Exception e) {
            out.println("Error executing command: " + e.getMessage());
        }
    }
    
    // Command injection to read files
    private void showFileContent(PrintWriter out, String filename) {
        try {
            if (filename == null || filename.isEmpty()) {
                out.println("Filename parameter is required");
                return;
            }
            
            // Command injection vulnerability: user input in file read command
            String[] command = {"/bin/sh", "-c", "cat " + filename};
            
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            
            String line;
            while ((line = reader.readLine()) != null) {
                out.println(line);
            }
            
            reader.close();
            process.waitFor();
        } catch (Exception e) {
            out.println("Error executing command: " + e.getMessage());
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String command = request.getParameter("command");
        
        response.setContentType("text/plain");
        PrintWriter out = response.getWriter();
        
        // Command injection vulnerability - executing arbitrary commands
        if (command != null && !command.isEmpty()) {
            try {
                // Extremely dangerous - executing arbitrary commands from user input
                Process process = Runtime.getRuntime().exec(command);
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                
                String line;
                while ((line = reader.readLine()) != null) {
                    out.println(line);
                }
                
                reader.close();
                process.waitFor();
            } catch (Exception e) {
                out.println("Error executing command: " + e.getMessage());
            }
        } else {
            out.println("No command specified");
        }
    }
}