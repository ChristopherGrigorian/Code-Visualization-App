import javax.swing.*;
import java.awt.*;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * @author Eric Canihuante
 */

public class ClassAnalysisResponsibilityPanel extends JPanel {
    public ClassAnalysisResponsibilityPanel() {
        setLayout(new BorderLayout());
        PowerHouse ph = PowerHouse.getInstance();
        Map<String, Integer> responsibilityData = ph.getClassResponsibilityData();

        // Calculate dynamic ranges for responsibilities
        int minResponsibility = responsibilityData.values().stream().min(Integer::compare).orElse(0);
        int maxResponsibility = responsibilityData.values().stream().max(Integer::compare).orElse(1);
        int lowThreshold = minResponsibility + (maxResponsibility - minResponsibility) / 3;
        int highThreshold = minResponsibility + 2 * (maxResponsibility - minResponsibility) / 3;

        // Create dataset for the chart
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Integer> entry : responsibilityData.entrySet()) {
            String className = entry.getKey();
            int responsibilities = entry.getValue();
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
        BarRenderer renderer = new BarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                int responsibilities = dataset.getValue(row, column).intValue();
                if (responsibilities <= lowThreshold) {
                    return Color.GREEN; // Adheres well to SRP
                } else if (responsibilities <= highThreshold) {
                    return Color.YELLOW; // Borderline adherence to SRP
                } else {
                    return Color.RED; // Likely violating SRP
                }
            }
        };
        plot.setRenderer(renderer);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setDefaultItemLabelsVisible(true);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // Rotate labels 45 degrees
        domainAxis.setTickLabelFont(new Font("SansSerif", Font.PLAIN, 10)); // Adjust font size

        // Add the chart to a panel
        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        add(chartPanel, BorderLayout.CENTER);

        // Explanation of SRP
        JTextArea srpExplanation = new JTextArea(
                "The Single Responsibility Principle (SRP) states that a class should have only one reason to change, " +
                        "meaning it should only have one job or responsibility. Adhering to SRP helps in making the code " +
                        "more maintainable, testable, and easier to understand.\n\n" +
                        "This visualization shows the number of responsibilities each class has. The color coding is dynamic " +
                        "based on the range of responsibilities in the project:\n" +
                        "- Green: Low number of responsibilities (well adheres to SRP)\n" +
                        "- Yellow: Moderate number of responsibilities (borderline adherence to SRP)\n" +
                        "- Red: High number of responsibilities (likely violating SRP)"
        );
        srpExplanation.setEditable(false);
        srpExplanation.setLineWrap(true);
        srpExplanation.setWrapStyleWord(true);
        srpExplanation.setBackground(getBackground());
        srpExplanation.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(srpExplanation, BorderLayout.NORTH);

        // Add legend
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new GridLayout(3, 1));
        legendPanel.add(createLegendItem(Color.GREEN, lowThreshold + " or fewer responsibilities " +
                "(adheres well to SRP)"));
        legendPanel.add(createLegendItem(Color.YELLOW, (lowThreshold + 1) + " to " + highThreshold + " " +
                "responsibilities (borderline adherence to SRP)"));
        legendPanel.add(createLegendItem(Color.RED, (highThreshold + 1) + " or more responsibilities" +
                " (likely violating SRP)"));
        add(legendPanel, BorderLayout.SOUTH);
    }

    private JPanel createLegendItem(Color color, String text) {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel colorLabel = new JLabel();
        colorLabel.setOpaque(true);
        colorLabel.setBackground(color);
        colorLabel.setPreferredSize(new Dimension(20, 20));
        JLabel textLabel = new JLabel(text);
        panel.add(colorLabel);
        panel.add(textLabel);
        return panel;
    }
}
