package ui;

import dao.ProductDAO;
import model.Product;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class DashboardPanel extends JPanel {

    private ProductDAO dao;
    private JLabel totalProductsLabel, totalStockLabel, totalValueLabel, lowStockLabel;

    public DashboardPanel(ProductDAO dao) {
        this.dao = dao;
        setLayout(new BorderLayout());
        setBackground(new Color(245, 247, 250));
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(245, 247, 250));
        header.setBorder(new EmptyBorder(20, 20, 10, 20));
        JLabel title = new JLabel("📦 Inventory Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(30, 30, 60));
        header.add(title);
        add(header, BorderLayout.NORTH);

        // Stats cards
        JPanel cards = new JPanel(new GridLayout(1, 4, 15, 0));
        cards.setBackground(new Color(245, 247, 250));
        cards.setBorder(new EmptyBorder(10, 20, 20, 20));

        totalProductsLabel = new JLabel("0");
        totalStockLabel    = new JLabel("0");
        totalValueLabel    = new JLabel("₹0");
        lowStockLabel      = new JLabel("0");

        cards.add(createCard("Total Products", totalProductsLabel, new Color(52, 152, 219), "🗂"));
        cards.add(createCard("Total Stock",    totalStockLabel,    new Color(46, 204, 113), "📦"));
        cards.add(createCard("Stock Value",    totalValueLabel,    new Color(155, 89, 182), "💰"));
        cards.add(createCard("Low Stock",      lowStockLabel,      new Color(231, 76, 60),  "⚠️"));

        add(cards, BorderLayout.CENTER);

        // Low stock table
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setBackground(new Color(245, 247, 250));
        bottom.setBorder(new EmptyBorder(0, 20, 20, 20));

        JLabel alertTitle = new JLabel("⚠️  Low Stock Alerts");
        alertTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        alertTitle.setForeground(new Color(192, 57, 43));
        alertTitle.setBorder(new EmptyBorder(0, 0, 8, 0));
        bottom.add(alertTitle, BorderLayout.NORTH);

        String[] cols = {"ID", "Product Name", "Category", "Current Qty", "Threshold"};
        javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable alertTable = new JTable(model);
        alertTable.setRowHeight(30);
        alertTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        alertTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        alertTable.getTableHeader().setBackground(new Color(231, 76, 60));
        alertTable.getTableHeader().setForeground(Color.WHITE);
        alertTable.setSelectionBackground(new Color(255, 200, 200));
        alertTable.setGridColor(new Color(220, 220, 220));

        // Populate low stock
        for (Product p : dao.getLowStockProducts()) {
            model.addRow(new Object[]{p.getId(), p.getName(), p.getCategory(), p.getQuantity(), p.getLowStockThreshold()});
        }

        JScrollPane scroll = new JScrollPane(alertTable);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 1));
        bottom.add(scroll, BorderLayout.CENTER);

        add(bottom, BorderLayout.SOUTH);

        refresh();
    }

    private JPanel createCard(String title, JLabel valueLabel, Color color, String icon) {
        JPanel card = new JPanel(new GridLayout(3, 1));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 230), 1),
            new EmptyBorder(15, 20, 15, 20)
        ));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 26));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        titleLabel.setForeground(new Color(100, 100, 120));

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);

        card.add(iconLabel);
        card.add(valueLabel);
        card.add(titleLabel);
        return card;
    }

    public void refresh() {
        List<Product> products = dao.getAllProducts();
        int totalStock = 0;
        double totalValue = 0;
        int lowCount = 0;
        for (Product p : products) {
            totalStock += p.getQuantity();
            totalValue += p.getPrice() * p.getQuantity();
            if (p.isLowStock()) lowCount++;
        }
        totalProductsLabel.setText(String.valueOf(products.size()));
        totalStockLabel.setText(String.valueOf(totalStock));
        totalValueLabel.setText(String.format("₹%.0f", totalValue));
        lowStockLabel.setText(String.valueOf(lowCount));
        revalidate();
        repaint();
    }
}
