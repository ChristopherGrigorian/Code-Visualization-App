import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class ClassResponsibilityPanel extends JPanel {
    public ClassResponsibilityPanel(Map<String, ClassMetrics> metrics) {
        setLayout(new BorderLayout());

        // Create dataset for the chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, ClassMetrics> entry : metrics.entrySet()) {
            String className = entry.getKey();
            ClassMetrics metric = entry.getValue();
            int responsibilities = metric.getMethods().size();  // Assuming the number of methods indicates responsibilities
            dataset.addValue(responsibilities, "Responsibilities", className);
        }

        // Create chart
        JFreeChart chart = ChartFactory.createBarChart(
                "Class Responsibilities",
                "Class",
                "Number of Responsibilities",
                dataset,
                PlotOrientation.VERTICAL,
                false, true, false);

        // Customize the chart
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, new Color(79, 129, 189));
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);

        // Add the chart to a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        add(chartPanel, BorderLayout.CENTER);
    }
}
