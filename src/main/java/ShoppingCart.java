import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a shopping cart in the e-commerce system
 * with deliberate vulnerabilities for SAST testing
 */
public class ShoppingCart {
    private Map<Product, Integer> items;
    private String sessionId;
    private String userId;
    
    public ShoppingCart(String userId, String sessionId) {
        this.items = new HashMap<>();
        this.userId = userId;
        this.sessionId = sessionId;
    }
    
    /**
     * Add a product to the cart with the specified quantity
     * @param product The product to add
     * @param quantity The quantity to add
     */
    public void addItem(Product product, int quantity) {
        // No validation for quantity - SAST should flag this
        if(items.containsKey(product)) {
            items.put(product, items.get(product) + quantity);
        } else {
            items.put(product, quantity);
        }
    }
    
    /**
     * Update the quantity of a product in the cart
     * @param product The product to update
     * @param quantity The new quantity
     */
    public void updateQuantity(Product product, int quantity) {
        // No validation for negative quantities - SAST should flag this
        if(quantity <= 0) {
            items.remove(product);
        } else {
            items.put(product, quantity);
        }
    }
    
    /**
     * Remove an item from the cart
     * @param product The product to remove
     */
    public void removeItem(Product product) {
        items.remove(product);
    }
    
    /**
     * Calculate the total price of all items in the cart
     * @return The total price
     */
    public double calculateTotal() {
        double total = 0.0;
        for(Map.Entry<Product, Integer> entry : items.entrySet()) {
            Product product = entry.getKey();
            Integer quantity = entry.getValue();
            total += product.getPrice() * quantity;
        }
        return total;
    }
    
    /**
     * Get all items in the cart
     * @return A list of all products in the cart
     */
    public List<Product> getProducts() {
        return new ArrayList<>(items.keySet());
    }
    
    /**
     * Get the quantity of a specific product
     * @param product The product
     * @return The quantity
     */
    public int getQuantity(Product product) {
        return items.getOrDefault(product, 0);
    }
    
    /**
     * Get all items with their quantities
     * @return A map of products and quantities
     */
    public Map<Product, Integer> getItems() {
        // Direct reference to internal map - SAST should flag this
        return items;
    }
    
    /**
     * Clear all items from the cart
     */
    public void clear() {
        items.clear();
    }
    
    /**
     * Check if the cart is empty
     * @return True if the cart is empty, false otherwise
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }
    
    /**
     * Get the number of unique products in the cart
     * @return The number of unique products
     */
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * Get the total number of items in the cart
     * @return The total quantity
     */
    public int getTotalQuantity() {
        int totalQuantity = 0;
        for(Integer quantity : items.values()) {
            totalQuantity += quantity;
        }
        return totalQuantity;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getSessionId() {
        // Returning sensitive session ID - SAST should flag this
        return sessionId;
    }
}