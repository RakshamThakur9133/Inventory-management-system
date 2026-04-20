package model;

public class Product {
    private int id;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private int lowStockThreshold;

    public Product(int id, String name, String category, double price, int quantity, int lowStockThreshold) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.lowStockThreshold = lowStockThreshold;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getLowStockThreshold() { return lowStockThreshold; }

    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setCategory(String category) { this.category = category; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }

    public boolean isLowStock() {
        return quantity <= lowStockThreshold;
    }

    public String toCSV() {
        return id + "," + name + "," + category + "," + price + "," + quantity + "," + lowStockThreshold;
    }

    public static Product fromCSV(String line) {
        String[] parts = line.split(",", -1);
        if (parts.length < 6) return null;
        return new Product(
            Integer.parseInt(parts[0].trim()),
            parts[1].trim(),
            parts[2].trim(),
            Double.parseDouble(parts[3].trim()),
            Integer.parseInt(parts[4].trim()),
            Integer.parseInt(parts[5].trim())
        );
    }

    @Override
    public String toString() {
        return name;
    }
}
