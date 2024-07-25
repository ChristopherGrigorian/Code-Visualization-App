import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class CharlieMain extends JFrame {
    /**
     * This class is intentionally made very bland and simple
     * In the hopes that it will make it easier to merge later
     * I didn't want to mess with the current Main function, as it looks like whoever
     * wrote it wants some certain fucntionality, the goal with this is just to use the
     * PowerHouse class to use my Galaxy class to create the graph with a specified directory
     * The directory is going to be hardcoded as that should make it clear when trying to merge
     * That also means this file wont work on anyone elses machine, but that's fine.
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
                JFrame frame = new CharlieMain(ph);
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

    public CharlieMain (PowerHouse ph) {
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
        JPanel test4 = new JPanel();
        test4.setBackground(new java.awt.Color(0, 0, 255));
//        Create tabs to switch between panels
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Galaxy", galaxy);
        tabs.addTab("Raw Metrics", rawMetrics);
        tabs.addTab("Abstractness vs. Instability", metricsChart);
        tabs.addTab("City View", cityPanel);
        tabs.addTab("Test4", test4);
        add(tabs, BorderLayout.CENTER);
    }
}
