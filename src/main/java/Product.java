/**
 * This class represents a product in the e-commerce system
 * with deliberate vulnerabilities for SAST testing
 */
public class Product {
    private int id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private int stockQuantity;
    
    // Insecure direct object reference - SAST should flag this
    public Product(int id, String name, String description, double price, String imageUrl, int stockQuantity) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stockQuantity = stockQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        // No validation of URL - SAST should flag this
        this.imageUrl = imageUrl;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    // Insecure serialization - SAST should flag this
    private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
    }
    
    @Override
    public String toString() {
        return name + " - $" + price;
    }
}