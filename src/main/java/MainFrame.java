import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MainFrame extends JFrame {
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
                createAndShowGUI(ph.getClassMetricsMap(), selectedDirectory);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void createAndShowGUI(Map<String, ClassMetrics> metrics, File directory) {
        JFrame frame = new MainFrame(metrics, directory);
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

    public MainFrame(Map<String, ClassMetrics> metrics, File directory) {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Galaxy", new GalaxyPanel());
        tabs.addTab("Raw Metrics", new RawMetricsPanel());
        tabs.addTab("Abstractness vs. Instability", new MetricsChartPanel());
        tabs.addTab("City View", CityController.createChartPanel());

        // Create "Class Analysis" tab with sub-tabs for each visualization
        JTabbedPane classAnalysisTabs = new JTabbedPane();
        classAnalysisTabs.addTab("Class Cohesion", new ClassCohesionPanel(metrics, directory));
        classAnalysisTabs.addTab("Class Composition", new ClassCompositionPanel(metrics, directory));
        classAnalysisTabs.addTab("Class Responsibilities", new ClassResponsibilityPanel(metrics, directory));

        JPanel classAnalysis = new JPanel(new BorderLayout());
        classAnalysis.add(classAnalysisTabs, BorderLayout.CENTER);
        tabs.addTab("Class Analysis", classAnalysis);

        tabs.addChangeListener(e -> {
            int selectedIndex = tabs.getSelectedIndex();
            String title = tabs.getTitleAt(selectedIndex);
            setTitle(title);
        });

        add(tabs, BorderLayout.CENTER);
    }
}
