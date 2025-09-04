import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.WebServlet;
import java.net.*;

/**
 * This servlet demonstrates insecure deserialization vulnerabilities for SAST testing
 */
@WebServlet("/user-profile")
public class SerializationServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final String DATA_DIR = "/tmp/serialized_data/";
    
    public SerializationServlet() {
        super();
        
        // Create data directory if it doesn't exist
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        String userId = request.getParameter("id");
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html><head><title>User Profile</title></head><body>");
        
        if ("view".equals(action) && userId != null) {
            // Deserialize user profile from file
            out.println("<h2>User Profile:</h2>");
            UserProfile profile = deserializeUserProfile(userId);
            
            if (profile != null) {
                out.println("<p>User ID: " + profile.getUserId() + "</p>");
                out.println("<p>Username: " + profile.getUsername() + "</p>");
                out.println("<p>Email: " + profile.getEmail() + "</p>");
                out.println("<p>Created: " + profile.getCreatedDate() + "</p>");
                
                // Display preferences
                out.println("<h3>Preferences:</h3><ul>");
                for (Map.Entry<String, String> pref : profile.getPreferences().entrySet()) {
                    out.println("<li>" + pref.getKey() + ": " + pref.getValue() + "</li>");
                }
                out.println("</ul>");
            } else {
                out.println("<p>User profile not found</p>");
            }
            
        } else if ("upload".equals(action)) {
            out.println("<h2>Upload Serialized Profile</h2>");
            out.println("<form method=\"post\" action=\"user-profile\" enctype=\"multipart/form-data\">");
            out.println("<input type=\"hidden\" name=\"action\" value=\"upload\">");
            out.println("<p>User ID: <input type=\"text\" name=\"userId\" required></p>");
            out.println("<p>Profile Data: <input type=\"file\" name=\"profileData\" required></p>");
            out.println("<p><input type=\"submit\" value=\"Upload\"></p>");
            out.println("</form>");
            
        } else if ("load".equals(action)) {
            out.println("<h2>Load Profile from URL</h2>");
            out.println("<form method=\"post\" action=\"user-profile\">");
            out.println("<input type=\"hidden\" name=\"action\" value=\"load\">");
            out.println("<p>User ID: <input type=\"text\" name=\"userId\" required></p>");
            out.println("<p>Profile URL: <input type=\"text\" name=\"profileUrl\" size=\"50\" required></p>");
            out.println("<p><input type=\"submit\" value=\"Load\"></p>");
            out.println("</form>");
            
        } else {
            out.println("<h2>User Profile Management</h2>");
            out.println("<ul>");
            out.println("<li><a href=\"?action=view&id=1\">View Sample Profile</a></li>");
            out.println("<li><a href=\"?action=upload\">Upload Profile</a></li>");
            out.println("<li><a href=\"?action=load\">Load Profile from URL</a></li>");
            out.println("</ul>");
        }
        
        out.println("</body></html>");
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        
        if ("create".equals(action)) {
            // Create and serialize a user profile
            String userId = request.getParameter("userId");
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            
            if (userId != null && username != null && email != null) {
                UserProfile profile = new UserProfile();
                profile.setUserId(userId);
                profile.setUsername(username);
                profile.setEmail(email);
                profile.setCreatedDate(new Date());
                
                // Add some preferences
                Map<String, String> preferences = new HashMap<>();
                preferences.put("theme", request.getParameter("theme"));
                preferences.put("language", request.getParameter("language"));
                profile.setPreferences(preferences);
                
                // Serialize profile
                serializeUserProfile(profile);
                
                response.sendRedirect("user-profile?action=view&id=" + userId);
                return;
            }
            
        } else if ("load".equals(action)) {
            // Insecure: Deserialize object from URL
            String userId = request.getParameter("userId");
            String profileUrl = request.getParameter("profileUrl");
            
            if (userId != null && profileUrl != null) {
                try {
                    // VULNERABILITY: Deserializing object from untrusted URL
                    URL url = new URL(profileUrl);
                    ObjectInputStream ois = new ObjectInputStream(url.openStream());
                    Object obj = ois.readObject(); // Insecure deserialization
                    ois.close();
                    
                    if (obj instanceof UserProfile) {
                        UserProfile profile = (UserProfile) obj;
                        serializeUserProfile(profile);
                        
                        response.sendRedirect("user-profile?action=view&id=" + profile.getUserId());
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if ("processData".equals(action)) {
            // Insecure deserialization directly from request parameter
            String serializedData = request.getParameter("data");
            
            if (serializedData != null) {
                try {
                    // VULNERABILITY: Deserializing from Base64 string in request
                    byte[] data = Base64.getDecoder().decode(serializedData);
                    
                    // Insecure deserialization
                    ByteArrayInputStream bais = new ByteArrayInputStream(data);
                    ObjectInputStream ois = new ObjectInputStream(bais);
                    Object obj = ois.readObject(); // Insecure deserialization
                    ois.close();
                    
                    // Process the object
                    response.setContentType("text/plain");
                    response.getWriter().println("Data processed successfully: " + obj.toString());
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        response.sendRedirect("user-profile");
    }
    
    // VULNERABILITY: Insecure deserialization from file
    private UserProfile deserializeUserProfile(String userId) {
        try {
            File file = new File(DATA_DIR + "profile_" + userId + ".ser");
            
            if (file.exists()) {
                // Insecure deserialization from file
                FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);
                Object obj = ois.readObject(); // Insecure deserialization
                ois.close();
                fis.close();
                
                if (obj instanceof UserProfile) {
                    return (UserProfile) obj;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    // Serializing user profile to file
    private void serializeUserProfile(UserProfile profile) {
        try {
            File file = new File(DATA_DIR + "profile_" + profile.getUserId() + ".ser");
            
            // Serialize the object
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(profile);
            oos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Insecure deserialization from cookie
    private Object deserializeFromCookie(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    try {
                        // VULNERABILITY: Deserializing from cookie value
                        byte[] data = Base64.getDecoder().decode(cookie.getValue());
                        
                        // Insecure deserialization
                        ByteArrayInputStream bais = new ByteArrayInputStream(data);
                        ObjectInputStream ois = new ObjectInputStream(bais);
                        Object obj = ois.readObject(); // Insecure deserialization
                        ois.close();
                        
                        return obj;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return null;
    }
}

// Serializable user profile class
class UserProfile implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String userId;
    private String username;
    private String email;
    private Date createdDate;
    private Map<String, String> preferences;
    
    public UserProfile() {
        this.preferences = new HashMap<>();
    }
    
    // Getters and setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public Date getCreatedDate() {
        return createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }
    
    public Map<String, String> getPreferences() {
        return preferences;
    }
    
    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }
    
    // VULNERABILITY: readObject method doesn't validate object state
    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        
        // No validation of deserialized data
    }
}