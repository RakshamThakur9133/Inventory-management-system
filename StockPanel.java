package ui;

import dao.ProductDAO;
import model.Product;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class StockPanel extends JPanel {

    private ProductDAO dao;
    private DefaultTableModel tableModel;
    private JTable table;
    private DashboardPanel dashboard;

    private JComboBox<String> productCombo;
    private JTextField amountField;
    private JLabel currentStockLabel;

    public StockPanel(ProductDAO dao, DashboardPanel dashboard) {
        this.dao = dao;
        this.dashboard = dashboard;
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(new EmptyBorder(15, 15, 15, 15));
        buildUI();
        loadTable();
    }

    private void buildUI() {
        // Header
        JLabel header = new JLabel("📦 Stock Management");
        header.setFont(new Font("Segoe UI", Font.BOLD, 20));
        header.setForeground(new Color(30, 30, 60));
        header.setBorder(new EmptyBorder(0, 0, 10, 0));
        add(header, BorderLayout.NORTH);

        // Stock table
        String[] cols = {"ID", "Product Name", "Category", "Price (₹)", "Quantity", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(tableModel) {
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int row, int col) {
                Component c = super.prepareRenderer(r, row, col);
                Object status = getModel().getValueAt(row, 5);
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

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 210)));
        add(scroll, BorderLayout.CENTER);

        // Update panel
        JPanel south = new JPanel(new GridBagLayout());
        south.setBackground(Color.WHITE);
        south.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(46, 204, 113), 1),
            "Update Stock", TitledBorder.LEFT, TitledBorder.TOP,
            new Font("Segoe UI", Font.BOLD, 13), new Color(46, 204, 113)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Product dropdown
        gbc.gridx = 0; gbc.gridy = 0;
        south.add(new JLabel("Select Product:"), gbc);
        productCombo = new JComboBox<>();
        productCombo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        productCombo.setPreferredSize(new Dimension(220, 32));
        refreshCombo();
        productCombo.addActionListener(e -> updateCurrentLabel());
        gbc.gridx = 1;
        south.add(productCombo, gbc);

        // Current stock
        gbc.gridx = 2;
        south.add(new JLabel("Current Stock:"), gbc);
        currentStockLabel = new JLabel("-");
        currentStockLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        currentStockLabel.setForeground(new Color(52, 152, 219));
        gbc.gridx = 3;
        south.add(currentStockLabel, gbc);

        // Amount field
        gbc.gridx = 0; gbc.gridy = 1;
        south.add(new JLabel("Amount:"), gbc);
        amountField = new JTextField(8);
        amountField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        amountField.setPreferredSize(new Dimension(120, 32));
        gbc.gridx = 1;
        south.add(amountField, gbc);

        // Buttons
        JButton restockBtn = styledButton("📥 Restock (+)", new Color(46, 204, 113));
        JButton sellBtn    = styledButton("📤 Sell (-)",     new Color(230, 126, 34));
        restockBtn.addActionListener(e -> updateStock(true));
        sellBtn.addActionListener(e -> updateStock(false));

        gbc.gridx = 2;
        south.add(restockBtn, gbc);
        gbc.gridx = 3;
        south.add(sellBtn, gbc);

        add(south, BorderLayout.SOUTH);
        updateCurrentLabel();
    }

    private void refreshCombo() {
        productCombo.removeAllItems();
        for (Product p : dao.getAllProducts()) {
            productCombo.addItem(p.getId() + " - " + p.getName());
        }
    }

    private void updateCurrentLabel() {
        String selected = (String) productCombo.getSelectedItem();
        if (selected == null) { currentStockLabel.setText("-"); return; }
        int id = Integer.parseInt(selected.split(" - ")[0].trim());
        for (Product p : dao.getAllProducts()) {
            if (p.getId() == id) {
                currentStockLabel.setText(String.valueOf(p.getQuantity()));
                if (p.isLowStock()) {
                    currentStockLabel.setForeground(new Color(180, 0, 0));
                } else {
                    currentStockLabel.setForeground(new Color(46, 204, 113));
                }
                return;
            }
        }
    }

    private void updateStock(boolean isRestock) {
        String selected = (String) productCombo.getSelectedItem();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a product.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            int amount = Integer.parseInt(amountField.getText().trim());
            if (amount <= 0) throw new NumberFormatException();

            int id = Integer.parseInt(selected.split(" - ")[0].trim());
            for (Product p : dao.getAllProducts()) {
                if (p.getId() == id) {
                    int newQty = isRestock ? p.getQuantity() + amount : p.getQuantity() - amount;
                    if (newQty < 0) {
                        JOptionPane.showMessageDialog(this, "Insufficient stock! Current: " + p.getQuantity(), "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    p.setQuantity(newQty);
                    dao.updateProduct(p);
                    break;
                }
            }
            loadTable();
            refreshCombo();
            updateCurrentLabel();
            amountField.setText("");
            String msg = isRestock ? "✅ Stock restocked successfully!" : "✅ Stock sold successfully!";
            JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid positive number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void loadTable() {
        tableModel.setRowCount(0);
        for (Product p : dao.getAllProducts()) {
            String status = p.isLowStock() ? "⚠ Low Stock" : "✔ In Stock";
            tableModel.addRow(new Object[]{
                p.getId(), p.getName(), p.getCategory(),
                String.format("%.2f", p.getPrice()),
                p.getQuantity(), status
            });
        }
        dashboard.refresh();
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
}
