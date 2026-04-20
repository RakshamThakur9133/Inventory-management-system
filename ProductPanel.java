package ui;

import dao.ProductDAO;
import model.Product;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ProductPanel extends JPanel {

    private ProductDAO dao;
    private DefaultTableModel tableModel;
    private JTable table;
    private JTextField searchField;

    private JTextField idField, nameField, categoryField, priceField, qtyField, thresholdField;

    private DashboardPanel dashboard;

    public ProductPanel(ProductDAO dao, DashboardPanel dashboard) {
        this.dao = dao;
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();
        loadTable(dao.getAllProducts());
    }

    private void buildUI() {
        // TOP: Search bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBackground(new Color(245, 247, 250));
        JLabel searchIcon = new JLabel("🔍");
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setPreferredSize(new Dimension(250, 32));
        JButton searchBtn = styledButton("Search", new Color(52, 152, 219));
        JButton resetBtn  = styledButton("Show All", new Color(100, 100, 120));

        searchBtn.addActionListener(e -> loadTable(dao.searchProducts(searchField.getText().trim())));
        resetBtn.addActionListener(e -> { searchField.setText(""); loadTable(dao.getAllProducts()); });

        topBar.add(searchIcon);
        topBar.add(searchField);
        topBar.add(searchBtn);
        topBar.add(resetBtn);
        add(topBar, BorderLayout.NORTH);

        // CENTER: Table
        String[] cols = {"ID", "Name", "Category", "Price (₹)", "Quantity", "Threshold", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                Object status = getModel().getValueAt(row, 6);
                if ("⚠ Low Stock".equals(status)) {
                    c.setBackground(new Color(255, 235, 235));
                    c.setForeground(new Color(180, 0, 0));
                } else {
                    c.setBackground(isRowSelected(row) ? new Color(210, 230, 255) : Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                return c;
            }
        };
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.getTableHeader().setBackground(new Color(52, 73, 94));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(220, 220, 220));
        table.setSelectionBackground(new Color(210, 230, 255));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Click to populate form
        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (row >= 0) populateForm(row);
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210)));
        add(scroll, BorderLayout.CENTER);

        // SOUTH: Form + buttons
        JPanel south = new JPanel(new BorderLayout(10, 10));
        south.setBackground(new Color(245, 247, 250));

        // Form
        JPanel form = new JPanel(new GridLayout(2, 6, 10, 8));
        form.setBackground(new Color(245, 247, 250));
        form.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
            "Product Details", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13), new Color(52, 152, 219)
        ));

        idField        = formField("ID (auto)"); idField.setEditable(false); idField.setBackground(new Color(230,230,230));
        nameField      = formField("Product Name");
        categoryField  = formField("Category");
        priceField     = formField("Price (₹)");
        qtyField       = formField("Quantity");
        thresholdField = formField("Low Stock Threshold");

        form.add(labeledField("ID:", idField));
        form.add(labeledField("Name:", nameField));
        form.add(labeledField("Category:", categoryField));
        form.add(labeledField("Price (₹):", priceField));
        form.add(labeledField("Quantity:", qtyField));
        form.add(labeledField("Threshold:", thresholdField));

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnPanel.setBackground(new Color(245, 247, 250));

        JButton addBtn    = styledButton("➕ Add Product",    new Color(46, 204, 113));
        JButton updateBtn = styledButton("✏️ Update",         new Color(52, 152, 219));
        JButton deleteBtn = styledButton("🗑 Delete",         new Color(231, 76, 60));
        JButton clearBtn  = styledButton("🔄 Clear Form",     new Color(149, 165, 166));

        addBtn.addActionListener(e -> addProduct());
        updateBtn.addActionListener(e -> updateProduct());
        deleteBtn.addActionListener(e -> deleteProduct());
        clearBtn.addActionListener(e -> clearForm());

        btnPanel.add(addBtn);
        btnPanel.add(updateBtn);
        btnPanel.add(deleteBtn);
        btnPanel.add(clearBtn);

        south.add(form, BorderLayout.CENTER);
        south.add(btnPanel, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
    }

    private JTextField formField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        f.setPreferredSize(new Dimension(120, 30));
        return f;
    }

    private JPanel labeledField(String label, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(3, 0));
        p.setBackground(new Color(245, 247, 250));
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        p.add(lbl, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JButton styledButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(160, 35));
        return btn;
    }

    private void populateForm(int row) {
        idField.setText(tableModel.getValueAt(row, 0).toString());
        nameField.setText(tableModel.getValueAt(row, 1).toString());
        categoryField.setText(tableModel.getValueAt(row, 2).toString());
        priceField.setText(tableModel.getValueAt(row, 3).toString());
        qtyField.setText(tableModel.getValueAt(row, 4).toString());
        thresholdField.setText(tableModel.getValueAt(row, 5).toString());
    }

    private void loadTable(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            String status = p.isLowStock() ? "⚠ Low Stock" : "✔ In Stock";
            tableModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getCategory(),
                String.format("%.2f", p.getPrice()),
                p.getQuantity(), p.getLowStockThreshold(), status
            });
        }
        dashboard.refresh();
    }

    private void addProduct() {
        try {
            String name      = nameField.getText().trim();
            String category  = categoryField.getText().trim();
            double price     = Double.parseDouble(priceField.getText().trim());
            int qty          = Integer.parseInt(qtyField.getText().trim());
            int threshold    = Integer.parseInt(thresholdField.getText().trim());

            if (name.isEmpty() || category.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Name and Category are required.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            Product p = new Product(0, name, category, price, qty, threshold);
            dao.addProduct(p);
            loadTable(dao.getAllProducts());
            clearForm();
            JOptionPane.showMessageDialog(this, "✅ Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values for Price, Quantity, and Threshold.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateProduct() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product to update.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int id           = Integer.parseInt(idField.getText().trim());
            String name      = nameField.getText().trim();
            String category  = categoryField.getText().trim();
            double price     = Double.parseDouble(priceField.getText().trim());
            int qty          = Integer.parseInt(qtyField.getText().trim());
            int threshold    = Integer.parseInt(thresholdField.getText().trim());

            Product p = new Product(id, name, category, price, qty, threshold);
            dao.updateProduct(p);
            loadTable(dao.getAllProducts());
            clearForm();
            JOptionPane.showMessageDialog(this, "✅ Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numeric values.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteProduct() {
        if (idField.getText().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this product?", "Confirm Delete",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (confirm == JOptionPane.YES_OPTION) {
            dao.deleteProduct(Integer.parseInt(idField.getText().trim()));
            loadTable(dao.getAllProducts());
            clearForm();
            JOptionPane.showMessageDialog(this, "🗑 Product deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        categoryField.setText("");
        priceField.setText("");
        qtyField.setText("");
        thresholdField.setText("");
        table.clearSelection();
    }
}
