import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class Main extends JFrame {
    /**
     * This Main class is designed to initialize and display various visualizations
     * of class metrics and relationships. It prompts the user to select a directory
     * containing Java source files, parses the directory to extract class metrics, and
     * then displays different visualizations including Galaxy, Raw Metrics, Abstractness vs. Instability,
     * City View, and Class Analysis (with sub-tabs for Cohesion, Composition, and Responsibilities).
     *
     * @author Charlie Ray
     */
    public static void main(String[] args) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Load Source Folder");
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            PowerHouse ph = PowerHouse.getInstance();
            File selectedDirectory = fileChooser.getSelectedFile();
            ph.setCurDirectory(selectedDirectory);

            try {
                ph.parseDirectory(selectedDirectory.toPath());
                JFrame frame = new Main(ph);
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
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

    }

    public Main(PowerHouse ph) {
        Map<String, ClassMetrics> metrics = ph.getClassMetricsMap();
        ArrayList<Function> functions = new ArrayList<>();
        for (ClassMetrics metric : metrics.values()) {
            String className = metric.getClassName();
            for (MethodMetrics method : metric.getMethods()) {
                Function f = new Function(method.getMethodName(), className, method.getLinesOfCode());
                functions.add(f);
            }
        }
//        add relationships
        for (ClassMetrics metric : metrics.values()) {
            String callerClassName = metric.getClassName();
            for (MethodMetrics method : metric.getMethods()) {
                String callerFunctionName = method.getMethodName();
                Function caller = null;
                for (Function f : functions) {
                    if (f.getName().equals(callerFunctionName) && f.getParentClass().equals(callerClassName)) {
                        caller = f;
                        break;
                    }
                }
                for (MethodCallDetails call : method.getMethodCalls()) {
                    String calleeClassName = call.getParentClass();
                    String calleeFunctionName = call.getMethodName();
                    Function callee = null;
                    for (Function f : functions) {
                        String upperFName = f.getName().toUpperCase();
                        String upperCName = calleeFunctionName.toUpperCase();
                        String upperFClass = f.getParentClass().toUpperCase();
                        String upperCClass = calleeClassName.toUpperCase();
                        if (upperFName.equals(upperCName) && upperFClass.equals(upperCClass)) {
                            callee = f;
                            break;
                        }
                        if (f.getName().equals(calleeFunctionName) && f.getParentClass().equals(calleeClassName)) {
                            callee = f;
                            break;
                        }
                    }
                    if (caller != null && callee != null) {
                        caller.addCall(callee);
                        callee.addCalledBy(caller);
                    }
                }
            }
        }
        JPanel galaxy = new GalaxyPanel(functions);

        JPanel rawMetrics = new RawMetricsPanel();
        JPanel metricsChart = new MetricsChartPanel();
        metricsChart.setBackground(new java.awt.Color(255, 0, 0));
        JPanel cityPanel= CityController.createChartPanel();


        // Create "Class Analysis" tab with sub-tabs for each visualization
        JTabbedPane classAnalysisTabs = new JTabbedPane();

        File directory = ph.getCurDirectory();
        classAnalysisTabs.addTab("Class Cohesion", new ClassCohesionPanel(metrics, directory));
        classAnalysisTabs.addTab("Class Composition", new ClassCompositionPanel(metrics, directory));
        classAnalysisTabs.addTab("Class Responsibilities", new ClassResponsibilityPanel(metrics, directory));
        JPanel classAnalysis = new JPanel(new BorderLayout());
        classAnalysis.add(classAnalysisTabs, BorderLayout.CENTER);


//        Create tabs to switch between panels
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Galaxy", galaxy);
        tabs.addTab("Raw Metrics", rawMetrics);
        tabs.addTab("Abstractness vs. Instability", metricsChart);
        tabs.addTab("City View", cityPanel);
        tabs.addTab("Class Analysis", classAnalysis);

        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                int selectedIndex = tabs.getSelectedIndex();
                String title = tabs.getTitleAt(selectedIndex);
                setTitle(title);
            }
        });

        add(tabs, BorderLayout.CENTER);
    }
}
