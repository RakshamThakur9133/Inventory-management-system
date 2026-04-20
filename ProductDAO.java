package dao;

import model.Product;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private static final String FILE_PATH = "inventory.csv";
    private int nextId = 1;

    public ProductDAO() {
        initFile();
        updateNextId();
    }

    private void initFile() {
        File f = new File(FILE_PATH);
        if (!f.exists()) {
            try { f.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void updateNextId() {
        List<Product> all = getAllProducts();
        for (Product p : all) {
            if (p.getId() >= nextId) nextId = p.getId() + 1;
        }
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Product p = Product.fromCSV(line);
                    if (p != null) list.add(p);
                }
            }
        } catch (IOException e) { e.printStackTrace(); }
        return list;
    }

    public boolean addProduct(Product product) {
        product.setId(nextId++);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            bw.write(product.toCSV());
            bw.newLine();
            return true;
        } catch (IOException e) { e.printStackTrace(); return false; }
    }

    public boolean updateProduct(Product updated) {
        List<Product> all = getAllProducts();
        boolean found = false;
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i).getId() == updated.getId()) {
                all.set(i, updated);
                found = true;
                break;
            }
        }
        if (found) saveAll(all);
        return found;
    }

    public boolean deleteProduct(int id) {
        List<Product> all = getAllProducts();
        boolean removed = all.removeIf(p -> p.getId() == id);
        if (removed) saveAll(all);
        return removed;
    }

    public List<Product> searchProducts(String keyword) {
        List<Product> result = new ArrayList<>();
        String kw = keyword.toLowerCase();
        for (Product p : getAllProducts()) {
            if (p.getName().toLowerCase().contains(kw) ||
                p.getCategory().toLowerCase().contains(kw)) {
                result.add(p);
            }
        }
        return result;
    }

    public List<Product> getLowStockProducts() {
        List<Product> result = new ArrayList<>();
        for (Product p : getAllProducts()) {
            if (p.isLowStock()) result.add(p);
        }
        return result;
    }

    private void saveAll(List<Product> list) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (Product p : list) {
                bw.write(p.toCSV());
                bw.newLine();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}
