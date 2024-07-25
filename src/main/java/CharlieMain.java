import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class CharlieMain extends JFrame {
    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Source Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            PowerHouse ph = PowerHouse.getInstance();
            ph.setCurDirectory(selectedDirectory);
            try {
                ph.parseDirectory(selectedDirectory.toPath());
                createAndShowGUI();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new CharlieMain();
        frame.setTitle("Galaxy Plot");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        while (true) {
            frame.repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public CharlieMain() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Galaxy", new GalaxyPanel());
        tabs.addTab("Raw Metrics", new RawMetricsPanel());
        tabs.addTab("Abstractness vs. Instability", new MetricsChartPanel());
        tabs.addTab("City View", CityController.createChartPanel());
        tabs.addTab("Test4", new JPanel());
        add(tabs, BorderLayout.CENTER);
    }
}
