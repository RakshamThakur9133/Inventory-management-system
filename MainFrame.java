package ui;

import dao.ProductDAO;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    public MainFrame() {
        setTitle("📦 Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(900, 600));

        // App icon color
        getContentPane().setBackground(new Color(245, 247, 250));

        ProductDAO dao = new ProductDAO();

        // Build panels
        DashboardPanel dashboardPanel = new DashboardPanel(dao);
        ProductPanel   productPanel   = new ProductPanel(dao, dashboardPanel);
        StockPanel     stockPanel     = new StockPanel(dao, dashboardPanel);

        // Tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(new Color(245, 247, 250));

        tabs.addTab("🏠  Dashboard",        dashboardPanel);
        tabs.addTab("🗂  Products",          productPanel);
        tabs.addTab("📦  Stock Management",  stockPanel);

        // Refresh dashboard when switching tabs
        tabs.addChangeListener(e -> {
            if (tabs.getSelectedIndex() == 0) dashboardPanel.refresh();
            if (tabs.getSelectedIndex() == 2) stockPanel.loadTable();
        });

        // Side header bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 39, 60));
        topBar.setPreferredSize(new Dimension(0, 50));

        JLabel appName = new JLabel("  📦 Inventory Management System");
        appName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        appName.setForeground(Color.WHITE);
        topBar.add(appName, BorderLayout.WEST);

        JLabel version = new JLabel("v1.0   ");
        version.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        version.setForeground(new Color(150, 170, 200));
        topBar.add(version, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);

        setVisible(true);
    }
}
