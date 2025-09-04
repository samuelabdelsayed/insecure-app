import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;

/**
 * This servlet contains deliberate XSS vulnerabilities for SAST testing
 */
@WebServlet("/user")
public class WebController extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    
    public WebController() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // XSS vulnerability - directly writing user input to response
        String username = request.getParameter("username");
        String message = request.getParameter("message");
        String theme = request.getParameter("theme");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<title>User Profile</title>");
        
        // XSS in CSS/style attribute
        out.println("<style>");
        out.println("body { background-color: " + theme + "; }");
        out.println("</style>");
        
        out.println("</head>");
        out.println("<body>");
        
        // XSS in HTML content - direct reflection of user input
        out.println("<h1>Welcome, " + username + "!</h1>");
        
        // XSS in HTML attributes
        out.println("<div id='" + username + "'></div>");
        
        // XSS in JavaScript context
        out.println("<script>");
        out.println("var userMessage = '" + message + "';");
        out.println("document.getElementById('" + username + "').innerHTML = userMessage;");
        out.println("</script>");
        
        // XSS in event handlers
        out.println("<button onclick=\"alert('" + message + "')\">Click me</button>");
        
        out.println("</body>");
        out.println("</html>");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String comment = request.getParameter("comment");
        
        // Store the comment (pretend database operation)
        saveComment(username, comment);
        
        // Redirect to GET handler with the XSS payload
        response.sendRedirect("user?username=" + username + "&message=" + comment);
    }
    
    private void saveComment(String username, String comment) {
        // Pretend database operation that doesn't sanitize input
        System.out.println("Saving comment for " + username + ": " + comment);
    }
    
    // Additional XSS vulnerability with JSON data
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String userData = readRequestBody(request);
        
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        
        // Vulnerable to XSS when userData contains malicious script
        out.println("{\"status\": \"success\", \"data\": " + userData + "}");
    }
    
    private String readRequestBody(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }
}