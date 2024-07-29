import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DPanel;
import org.jfree.chart3d.data.category.CategoryDataset3D;
import org.jfree.chart3d.graphics3d.swing.DisplayPanel3D;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

/**
 * @author Harold Ellis (manualdriver)
 */

public class CityPanelFactory {

    public static JPanel createClassMetricsPanel() {
        CityPanel content = new CityPanel(new BorderLayout());
        content.setPreferredSize(new Dimension(800, 600));
        CategoryDataset3D<String, String, String> dataset = MetricsDatasetFactory.createClassMetricsDataset();
        Chart3D chart = ChartFactory.createChart("Java Code Analysis", "For visualization purposes!", "Class", "Code Metrics", "# of lines", dataset);
        Chart3DPanel chartPanel = new Chart3DPanel(chart);
        chartPanel.setMargin(0.30);
        content.setChartPanel(chartPanel);
        chartPanel.zoomToFit(new Dimension(800, 600));
        content.add(new DisplayPanel3D(chartPanel));
        return content;
    }

    public static JPanel createMethodMetricsPanel() {
        JTabbedPane tabbedPane = new JTabbedPane();
        PowerHouse powerHouse = PowerHouse.getInstance();
        Map<String, ClassMetrics> classMetricsMap = powerHouse.getClassMetricsMap();

        for (String className : classMetricsMap.keySet()) {
            JPanel methodPanel = createMethodMetricsPanelForClass(className);
            tabbedPane.addTab(className, methodPanel);
        }

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(tabbedPane, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel createMethodMetricsPanelForClass(String className) {
        CityPanel content = new CityPanel(new BorderLayout());
        content.setPreferredSize(new Dimension(800, 600));
        CategoryDataset3D<String, String, String> dataset = MetricsDatasetFactory.createMethodMetricsDataset(className);
        Chart3D chart = ChartFactory.createChart("Method Metrics Analysis - " + className, "For visualization purposes!", "Method", "Code Metrics", "# of lines", dataset);
        Chart3DPanel chartPanel = new Chart3DPanel(chart);
        chartPanel.setMargin(0.30);
        content.setChartPanel(chartPanel);
        chartPanel.zoomToFit(new Dimension(800, 600));
        content.add(new DisplayPanel3D(chartPanel));
        return content;
    }
}
