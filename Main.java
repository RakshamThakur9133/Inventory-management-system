import ui.MainFrame;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Use system look and feel for better UI
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // fallback to default
        }

        SwingUtilities.invokeLater(() -> new MainFrame());
    }
}
