import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.text.DecimalFormat;

/**
 * This class represents the main dashboard shown after login
 * with deliberate vulnerabilities for SAST testing
 */
public class Dashboard extends JFrame {
    private JPanel mainPanel, productPanel, cartPanel, checkoutPanel;
    private JLabel welcomeLabel, totalLabel;
    private JButton addToCartBtn, viewCartBtn, checkoutBtn, logoutBtn;
    private JTable productTable, cartTable;
    private JScrollPane productScrollPane, cartScrollPane;
    private JSpinner quantitySpinner;
    private JTextField searchField;
    private CardLayout cardLayout;
    
    // Color scheme for the app - adjusted for better contrast
    private final Color primaryColor = new Color(25, 118, 210);    // Darker Blue
    private final Color secondaryColor = new Color(34, 49, 63);    // Darker Blue/Gray
    private final Color accentColor = new Color(211, 47, 47);      // Darker Red
    private final Color lightColor = new Color(245, 245, 245);     // Lighter Gray
    private final Color highlightColor = new Color(56, 142, 60);   // Darker Green
    private final Color textColor = new Color(33, 33, 33);         // Nearly Black for text
    
    private Login loginRef;
    private String currentUser;
    private java.util.List<Product> products;
    private ShoppingCart cart;
    
    public Dashboard(String username, Login loginRef) {
        this.currentUser = username;
        this.loginRef = loginRef;
        this.products = generateSampleProducts();
        this.cart = new ShoppingCart(username, "SESSION-" + System.currentTimeMillis());
        
        // Set up the frame
        setTitle("Insecure E-Commerce - Dashboard");
        setSize(1000, 700);  // Increased window size
        setMinimumSize(new Dimension(900, 650));  // Set minimum size
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create main panel with card layout
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);
        
        // Create panels
        createProductPanel();
        createCartPanel();
        createCheckoutPanel();
        
        // Add panels to main panel
        mainPanel.add(productPanel, "products");
        mainPanel.add(cartPanel, "cart");
        mainPanel.add(checkoutPanel, "checkout");
        
        // Show the product panel initially
        cardLayout.show(mainPanel, "products");
        
        // Add main panel to the frame
        add(mainPanel);
        
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void createProductPanel() {
        productPanel = new JPanel(new BorderLayout(10, 10));
        productPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        productPanel.setBackground(lightColor);
        
        // Top panel with welcome message and logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(primaryColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        welcomeLabel = new JLabel("Welcome, " + currentUser + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));  // Larger font
        welcomeLabel.setForeground(Color.WHITE);
        
        // Create logout button
        logoutBtn = new JButton("Logout");
        logoutBtn.setBackground(accentColor.darker());
        logoutBtn.setForeground(new Color(255, 255, 240)); // Light yellow-white
        logoutBtn.setFont(new Font("Arial", Font.BOLD, 14));  // Larger font
        logoutBtn.setFocusPainted(false);
        logoutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        logoutBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Hand cursor
        logoutBtn.setPreferredSize(new Dimension(100, 35));  // Larger size
        logoutBtn.addActionListener(e -> logout());
        
        topPanel.add(welcomeLabel, BorderLayout.WEST);
        topPanel.add(logoutBtn, BorderLayout.EAST);
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(lightColor);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));
        
        JLabel searchLabel = new JLabel("Search: ");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));  // Larger font
        searchLabel.setForeground(textColor);  // Using darker text color
        
        searchField = new JTextField(25);  // Wider text field
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));  // Larger font
        searchField.setPreferredSize(new Dimension(250, 30));  // Larger size
        
        JButton searchBtn = new JButton("Search");
        searchBtn.setBackground(primaryColor.darker());
        searchBtn.setForeground(new Color(255, 255, 240)); // Light yellow-white
        searchBtn.setFont(new Font("Arial", Font.BOLD, 14));  // Larger font
        searchBtn.setFocusPainted(false);
        searchBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        searchBtn.setPreferredSize(new Dimension(100, 30));  // Fixed size
        searchBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Hand cursor
        searchBtn.addActionListener(e -> searchProducts());
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        
        // Product table
        String[] columnNames = {"ID", "Name", "Description", "Price", "Stock"};
        DefaultTableModel productModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        for (Product product : products) {
            productModel.addRow(new Object[]{
                product.getId(),
                product.getName(),
                product.getDescription(),
                "$" + product.getPrice(),
                product.getStockQuantity()
            });
        }
        
        productTable = new JTable(productModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getTableHeader().setReorderingAllowed(false);
        
        productScrollPane = new JScrollPane(productTable);
        
        // Apply custom rendering to product table
        productTable.setRowHeight(25);
        productTable.setGridColor(new Color(200, 200, 200));
        productTable.setShowGrid(true);
        productTable.setSelectionBackground(new Color(173, 216, 230));
        productTable.getTableHeader().setBackground(secondaryColor);
        productTable.getTableHeader().setForeground(Color.WHITE);
        productTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Bottom panel with quantity and add to cart
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Content area
        JPanel contentArea = new JPanel(new FlowLayout(FlowLayout.LEFT));
        contentArea.setBackground(lightColor);
        contentArea.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        
        JLabel quantityLabel = new JLabel("Quantity: ");
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 12));
        quantityLabel.setForeground(secondaryColor);
        
        SpinnerNumberModel spinnerModel = new SpinnerNumberModel(1, 1, 100, 1);
        quantitySpinner = new JSpinner(spinnerModel);
        
        // Footer panel (blue banner similar to header)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        footerPanel.setBackground(primaryColor);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setBackground(new Color(56, 142, 60).darker());
        addToCartBtn.setForeground(new Color(255, 255, 240)); // Light yellow-white
        addToCartBtn.setFocusPainted(false);
        addToCartBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        addToCartBtn.setFont(new Font("Arial", Font.BOLD, 12));
        addToCartBtn.addActionListener(e -> addToCart());
        
        viewCartBtn = new JButton("View Cart");
        viewCartBtn.setBackground(secondaryColor);
        viewCartBtn.setForeground(new Color(255, 255, 240)); // Light yellow-white
        viewCartBtn.setFocusPainted(false);
        viewCartBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        viewCartBtn.setFont(new Font("Arial", Font.BOLD, 12));
        viewCartBtn.addActionListener(e -> viewCart());
        
        contentArea.add(quantityLabel);
        contentArea.add(quantitySpinner);
        
        footerPanel.add(addToCartBtn);
        footerPanel.add(viewCartBtn);
        
        bottomPanel.add(contentArea, BorderLayout.CENTER);
        bottomPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add components to product panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(lightColor);
        headerPanel.add(topPanel, BorderLayout.NORTH);
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        
        productPanel.add(headerPanel, BorderLayout.NORTH);
        productPanel.add(productScrollPane, BorderLayout.CENTER);
        productPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void createCartPanel() {
        cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        cartPanel.setBackground(lightColor);
        
        // Top panel with title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(primaryColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel cartTitle = new JLabel("Your Shopping Cart");
        cartTitle.setFont(new Font("Arial", Font.BOLD, 18));  // Larger font
        cartTitle.setForeground(Color.WHITE);
        
        // Create back button
        JButton backBtn = new JButton("Back to Products");
        backBtn.setBackground(secondaryColor.darker());
        backBtn.setForeground(new Color(255, 255, 240)); // Light yellow-white
        backBtn.setFont(new Font("Arial", Font.BOLD, 14));  // Larger font
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        backBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));  // Hand cursor
        backBtn.setPreferredSize(new Dimension(150, 35));  // Larger size
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "products"));
        
        topPanel.add(cartTitle, BorderLayout.WEST);
        topPanel.add(backBtn, BorderLayout.EAST);
        
        // Cart table
        String[] columnNames = {"Product", "Price", "Quantity", "Subtotal"};
        DefaultTableModel cartModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only quantity column is editable
            }
        };
        
        cartTable = new JTable(cartModel);
        cartTable.getTableHeader().setReorderingAllowed(false);
        
        // Allow editing of quantity column
        cartTable.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(new JTextField()));
        cartTable.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 2) { // Quantity column
                updateCartQuantity(e.getFirstRow());
            }
        });
        
        cartScrollPane = new JScrollPane(cartTable);
        
        // Apply custom rendering to cart table
        cartTable.setRowHeight(25);
        cartTable.setGridColor(new Color(200, 200, 200));
        cartTable.setShowGrid(true);
        cartTable.setSelectionBackground(new Color(173, 216, 230));
        cartTable.getTableHeader().setBackground(secondaryColor);
        cartTable.getTableHeader().setForeground(Color.WHITE);
        cartTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Bottom panel with total and checkout button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Content area with total
        JPanel contentArea = new JPanel(new BorderLayout());
        contentArea.setBackground(lightColor);
        contentArea.setBorder(BorderFactory.createEmptyBorder(10, 5, 5, 5));
        
        totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalLabel.setForeground(secondaryColor);
        contentArea.add(totalLabel, BorderLayout.WEST);
        
        // Footer panel (blue banner similar to header)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        footerPanel.setBackground(primaryColor);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton removeBtn = new JButton("Remove Selected");
        removeBtn.setBackground(accentColor.darker());
        removeBtn.setForeground(new Color(255, 255, 240)); // Light yellow-white
        removeBtn.setFocusPainted(false);
        removeBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        removeBtn.setFont(new Font("Arial", Font.BOLD, 12));
        removeBtn.addActionListener(e -> removeFromCart());
        
        checkoutBtn = new JButton("Proceed to Checkout");
        checkoutBtn.setBackground(highlightColor.darker());
        checkoutBtn.setForeground(new Color(255, 255, 240)); // Light yellow-white
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 12));
        checkoutBtn.addActionListener(e -> proceedToCheckout());
        
        footerPanel.add(removeBtn);
        footerPanel.add(checkoutBtn);
        
        bottomPanel.add(contentArea, BorderLayout.CENTER);
        bottomPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add components to cart panel
        cartPanel.add(topPanel, BorderLayout.NORTH);
        cartPanel.add(cartScrollPane, BorderLayout.CENTER);
        cartPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private void createCheckoutPanel() {
        checkoutPanel = new JPanel(new BorderLayout(10, 10));
        checkoutPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        checkoutPanel.setBackground(lightColor);
        
        // Top panel with title
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(primaryColor);
        topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel checkoutTitle = new JLabel("Checkout");
        checkoutTitle.setFont(new Font("Arial", Font.BOLD, 18));  // Larger font
        checkoutTitle.setForeground(Color.WHITE);
        
        // Create back button
        JButton backToCartBtn = new JButton("Back to Cart");
        backToCartBtn.setBackground(secondaryColor.darker());
        backToCartBtn.setForeground(new Color(255, 255, 240)); // Light yellow-white
        backToCartBtn.setFocusPainted(false);
        backToCartBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        backToCartBtn.setFont(new Font("Arial", Font.BOLD, 12));
        backToCartBtn.addActionListener(e -> cardLayout.show(mainPanel, "cart"));
        
        topPanel.add(checkoutTitle, BorderLayout.WEST);
        topPanel.add(backToCartBtn, BorderLayout.EAST);
        
        // Center panel with shipping info
        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        centerPanel.setBackground(lightColor);
        
        // Personal information fields - styled
        JLabel[] labels = {
            new JLabel("Full Name:"),
            new JLabel("Email:"),
            new JLabel("Address:"),
            new JLabel("City:"),
            new JLabel("Zip Code:"),
            new JLabel("Credit Card Number:"),
            new JLabel("Expiry Date (MM/YY):"),
            new JLabel("CVV:")
        };
        
        // Apply styles to labels
        for (JLabel label : labels) {
            label.setFont(new Font("Arial", Font.BOLD, 12));
            label.setForeground(secondaryColor);
            centerPanel.add(label);
            
            JTextField field = new JTextField();
            field.setMargin(new Insets(5, 5, 5, 5));
            centerPanel.add(field);
        }
        
        // Order summary panel
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setName("summaryPanel");
        summaryPanel.setBackground(lightColor);
        summaryPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(secondaryColor), 
            "Order Summary", 
            TitledBorder.LEFT, 
            TitledBorder.TOP, 
            new Font("Arial", Font.BOLD, 14), 
            secondaryColor
        ));
        
        JTextArea summaryArea = new JTextArea(5, 20);
        summaryArea.setEditable(false);
        summaryArea.setLineWrap(true);
        summaryArea.setFont(new Font("Arial", Font.PLAIN, 12));
        summaryArea.setBackground(new Color(245, 245, 245));
        summaryArea.setMargin(new Insets(5, 5, 5, 5));
        summaryArea.setText(getOrderSummary());
        
        summaryPanel.add(new JScrollPane(summaryArea), BorderLayout.CENTER);
        
        // Bottom panel with place order button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Footer panel (blue banner similar to header)
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        footerPanel.setBackground(primaryColor);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JButton placeOrderBtn = new JButton("Place Order");
        placeOrderBtn.setBackground(highlightColor.darker());
        placeOrderBtn.setForeground(new Color(255, 255, 240)); // Light yellow-white
        placeOrderBtn.setFocusPainted(false);
        placeOrderBtn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        placeOrderBtn.setFont(new Font("Arial", Font.BOLD, 14));
        placeOrderBtn.addActionListener(e -> placeOrder());
        
        footerPanel.add(placeOrderBtn);
        bottomPanel.add(footerPanel, BorderLayout.CENTER);
        
        // Add components to checkout panel
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.add(centerPanel, BorderLayout.NORTH);
        formPanel.add(summaryPanel, BorderLayout.CENTER);
        
        checkoutPanel.add(topPanel, BorderLayout.NORTH);
        checkoutPanel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        checkoutPanel.add(bottomPanel, BorderLayout.SOUTH);
    }
    
    private java.util.List<Product> generateSampleProducts() {
        java.util.List<Product> productList = new ArrayList<>();
        
        productList.add(new Product(1, "Smartphone", "High-end smartphone with advanced features", 699.99, "/images/smartphone.jpg", 50));
        productList.add(new Product(2, "Laptop", "Professional laptop for work and gaming", 1299.99, "/images/laptop.jpg", 30));
        productList.add(new Product(3, "Headphones", "Noise-cancelling wireless headphones", 199.99, "/images/headphones.jpg", 100));
        productList.add(new Product(4, "Smartwatch", "Fitness tracker with heart rate monitor", 249.99, "/images/smartwatch.jpg", 45));
        productList.add(new Product(5, "Tablet", "10-inch tablet with HD display", 399.99, "/images/tablet.jpg", 25));
        productList.add(new Product(6, "Camera", "DSLR camera with 4K video recording", 799.99, "/images/camera.jpg", 15));
        productList.add(new Product(7, "Speaker", "Bluetooth speaker with premium sound", 149.99, "/images/speaker.jpg", 60));
        productList.add(new Product(8, "Monitor", "27-inch 4K gaming monitor", 349.99, "/images/monitor.jpg", 20));
        productList.add(new Product(9, "Keyboard", "Mechanical RGB gaming keyboard", 129.99, "/images/keyboard.jpg", 40));
        productList.add(new Product(10, "Mouse", "High precision gaming mouse", 79.99, "/images/mouse.jpg", 55));
        
        return productList;
    }
    
    private void addToCart() {
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow >= 0) {
            int productId = (int) productTable.getValueAt(selectedRow, 0);
            int quantity = (int) quantitySpinner.getValue();
            
            // Find the product
            Product selectedProduct = null;
            for (Product product : products) {
                if (product.getId() == productId) {
                    selectedProduct = product;
                    break;
                }
            }
            
            if (selectedProduct != null) {
                cart.addItem(selectedProduct, quantity);
                JOptionPane.showMessageDialog(this, 
                                            "Added " + quantity + " " + selectedProduct.getName() + "(s) to cart",
                                            "Success", 
                                            JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                                        "Please select a product first", 
                                        "No Selection", 
                                        JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void viewCart() {
        DefaultTableModel cartModel = (DefaultTableModel) cartTable.getModel();
        cartModel.setRowCount(0);
        
        DecimalFormat df = new DecimalFormat("0.00");
        
        for (Product product : cart.getProducts()) {
            int quantity = cart.getQuantity(product);
            double subtotal = quantity * product.getPrice();
            
            cartModel.addRow(new Object[]{
                product.getName(),
                "$" + df.format(product.getPrice()),
                quantity,
                "$" + df.format(subtotal)
            });
        }
        
        totalLabel.setText("Total: $" + df.format(cart.calculateTotal()));
        cardLayout.show(mainPanel, "cart");
    }
    
    private void updateCartQuantity(int row) {
        try {
            DefaultTableModel model = (DefaultTableModel) cartTable.getModel();
            String productName = (String) model.getValueAt(row, 0);
            String quantityStr = model.getValueAt(row, 2).toString();
            int newQuantity = Integer.parseInt(quantityStr);
            
            if (newQuantity <= 0) {
                // Remove from cart if quantity is 0 or negative
                for (Product product : cart.getProducts()) {
                    if (product.getName().equals(productName)) {
                        cart.removeItem(product);
                        break;
                    }
                }
                viewCart(); // Refresh the cart view
            } else {
                // Update quantity
                for (Product product : cart.getProducts()) {
                    if (product.getName().equals(productName)) {
                        cart.updateQuantity(product, newQuantity);
                        
                        // Update the subtotal cell
                        double subtotal = newQuantity * product.getPrice();
                        DecimalFormat df = new DecimalFormat("0.00");
                        model.setValueAt("$" + df.format(subtotal), row, 3);
                        
                        // Update the total
                        totalLabel.setText("Total: $" + df.format(cart.calculateTotal()));
                        break;
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                                        "Please enter a valid quantity", 
                                        "Invalid Input", 
                                        JOptionPane.ERROR_MESSAGE);
            viewCart(); // Refresh the cart view
        }
    }
    
    private void removeFromCart() {
        int selectedRow = cartTable.getSelectedRow();
        if (selectedRow >= 0) {
            String productName = (String) cartTable.getValueAt(selectedRow, 0);
            
            for (Product product : cart.getProducts()) {
                if (product.getName().equals(productName)) {
                    cart.removeItem(product);
                    viewCart(); // Refresh the cart view
                    break;
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                                        "Please select an item to remove", 
                                        "No Selection", 
                                        JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void proceedToCheckout() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                                        "Your cart is empty", 
                                        "Empty Cart", 
                                        JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Update order summary in checkout panel
        for (Component comp : checkoutPanel.getComponents()) {
            if (comp instanceof JScrollPane) {
                JScrollPane scrollPane = (JScrollPane) comp;
                if (scrollPane.getViewport().getView() instanceof JPanel) {
                    JPanel formPanel = (JPanel) scrollPane.getViewport().getView();
                    for (Component formComp : formPanel.getComponents()) {
                        if (formComp instanceof JPanel && formComp.getName() != null && formComp.getName().equals("summaryPanel")) {
                            JPanel summaryPanel = (JPanel) formComp;
                            for (Component summaryComp : summaryPanel.getComponents()) {
                                if (summaryComp instanceof JScrollPane) {
                                    JScrollPane textScrollPane = (JScrollPane) summaryComp;
                                    if (textScrollPane.getViewport().getView() instanceof JTextArea) {
                                        JTextArea summaryArea = (JTextArea) textScrollPane.getViewport().getView();
                                        summaryArea.setText(getOrderSummary());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        cardLayout.show(mainPanel, "checkout");
    }
    
    private String getOrderSummary() {
        StringBuilder summary = new StringBuilder();
        DecimalFormat df = new DecimalFormat("0.00");
        
        summary.append("Order Summary:\n\n");
        
        for (Product product : cart.getProducts()) {
            int quantity = cart.getQuantity(product);
            double subtotal = quantity * product.getPrice();
            
            summary.append(quantity).append(" x ")
                   .append(product.getName())
                   .append(" ($").append(df.format(product.getPrice())).append(")")
                   .append(" = $").append(df.format(subtotal))
                   .append("\n");
        }
        
        summary.append("\nTotal: $").append(df.format(cart.calculateTotal()));
        
        return summary.toString();
    }
    
    private void placeOrder() {
        // This would normally validate fields and process the order
        // For this demo, just show a confirmation and clear the cart
        
        JOptionPane.showMessageDialog(this,
                                    "Order placed successfully! Thank you for your purchase.",
                                    "Order Confirmed",
                                    JOptionPane.INFORMATION_MESSAGE);
        
        cart.clear();
        cardLayout.show(mainPanel, "products");
    }
    
    private void searchProducts() {
        String searchTerm = searchField.getText().toLowerCase();
        DefaultTableModel model = (DefaultTableModel) productTable.getModel();
        model.setRowCount(0);
        
        // Insecure search - SAST should flag this
        for (Product product : products) {
            if (searchTerm.isEmpty() || 
                product.getName().toLowerCase().contains(searchTerm) ||
                product.getDescription().toLowerCase().contains(searchTerm)) {
                
                model.addRow(new Object[]{
                    product.getId(),
                    product.getName(),
                    product.getDescription(),
                    "$" + product.getPrice(),
                    product.getStockQuantity()
                });
            }
        }
    }
    
    private void logout() {
        // Clear user session
        cart.clear();
        dispose();
        loginRef.setVisible(true);
        loginRef.resetFields();
    }
}