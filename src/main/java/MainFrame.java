import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * @author CharlieRay668 (Charlie Ray)
 * @author Eric Canihuante
 * @author christophergrigorian
 * @author Harold Ellis
 */
public class MainFrame extends JFrame {
    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Source Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File selectedDirectory = fileChooser.getSelectedFile();
            PowerHouse ph = PowerHouse.getInstance();
            try {
                ph.parseDirectory(selectedDirectory.toPath());
                createAndShowGUI();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void createAndShowGUI() {
        JFrame frame = new MainFrame();
        frame.setTitle("Galaxy Plot");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setVisible(true);

        Timer timer = new Timer(10, e -> frame.repaint());
        timer.start();
    }

    public MainFrame() {
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Galaxy", new GalaxyPanel());
        tabs.addTab("Raw Metrics", new RawMetricsPanel());
        tabs.addTab("Abstractness vs. Instability", new MetricsChartPanel());
        tabs.addTab("City View", CityController.createChartPanel());

        // Create "Class Analysis" tab with sub-tabs for each visualization
        JTabbedPane classAnalysisTabs = new JTabbedPane();
        classAnalysisTabs.addTab("Class Cohesion", new ClassAnalysisCohesionPanel());
        classAnalysisTabs.addTab("Class Composition", new ClassAnalysisCompositionPanel());
        classAnalysisTabs.addTab("Class Responsibilities", new ClassAnalysisResponsibilityPanel());
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
