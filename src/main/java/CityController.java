import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.Chart3DPanel;
import org.jfree.chart3d.data.category.CategoryDataset3D;
import org.jfree.chart3d.data.category.StandardCategoryDataset3D;
import org.jfree.chart3d.graphics3d.Dimension3D;
import org.jfree.chart3d.graphics3d.swing.DisplayPanel3D;
import org.jfree.chart3d.legend.LegendAnchor;
import org.jfree.chart3d.plot.CategoryPlot3D;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CityController {
    public static JPanel createChartPanel() {
        CityPanel content = new CityPanel(new BorderLayout());
        content.setPreferredSize(new Dimension(800, 600));
        CategoryDataset3D<String, String, String> dataset = createDataset();
        Chart3D chart = createChart(dataset);
        Chart3DPanel chartPanel = new Chart3DPanel(chart);
        chartPanel.setMargin(0.30);
        content.setChartPanel(chartPanel);
        chartPanel.zoomToFit(new Dimension(800, 600));
        content.add(new DisplayPanel3D(chartPanel));
        return content;
    }

    public static Chart3D createChart(CategoryDataset3D<String, String, String> dataset) {
        Chart3D chart = Chart3DFactory.createBarChart("Java Code Analysis",
                "for visualization purposes! :)", dataset, "Class", "Code Metrics",
                "# of lines");
        chart.setChartBoxColor(new Color(255, 255, 255, 127));
        chart.setLegendAnchor(LegendAnchor.BOTTOM_RIGHT);
        CategoryPlot3D plot = (CategoryPlot3D) chart.getPlot();
        plot.setGridlinePaintForValues(Color.BLACK);
        plot.setDimensions(new Dimension3D(20, 10, 10)); // Adjust dimensions as needed
        return chart;
    }

    private static CategoryDataset3D<String, String, String> createDataset() {
        StandardCategoryDataset3D<String, String, String> dataset = new StandardCategoryDataset3D<>();
        PowerHouse powerHouse = PowerHouse.getInstance();

        Map<String, ClassMetrics> classMetricsMap = powerHouse.getClassMetricsMap();

        for (ClassMetrics metrics : classMetricsMap.values()) {
            dataset.addValue(metrics.getTotalLines(), "Total Lines", metrics.getClassName(), "Total Lines");
            dataset.addValue(metrics.getCommentLines(), "Comment Lines", metrics.getClassName(), "Comment Lines");
            dataset.addValue(metrics.getBlankLines(), "Blank Lines", metrics.getClassName(), "Blank Lines");
            dataset.addValue(metrics.getExecutableLines(), "Executable Lines", metrics.getClassName(), "Executable Lines");
            dataset.addValue(metrics.getLogicalLines(), "Logical Lines", metrics.getClassName(), "Logical Lines");
        }

        return dataset;
    }
}
