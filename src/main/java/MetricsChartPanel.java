import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.chart.renderer.xy.XYShapeRenderer;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Map;

/**
 * Responsible for creating Abstract vs. Instability chart.
 *
 * @author christophergrigorian
 */

public class MetricsChartPanel extends JPanel {

    public MetricsChartPanel() {
        Map<String, ClassMetrics> classMetricsMap = PowerHouse.getInstance().getClassMetricsMap();
        XYSeriesCollection dataset = createDataset(classMetricsMap);
        JFreeChart chart = ChartFactory.createScatterPlot(
                "Instability vs. Abstractness",
                "Instability (I)",
                "Abstractness (A)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        XYPlot plot = chart.getXYPlot();
        plot.setDomainPannable(true);
        plot.setRangePannable(true);

        plot.getDomainAxis().setRange(-0.1, 1.1);
        plot.getRangeAxis().setRange(-0.1, 1.1);

        XYShapeRenderer renderer = new XYShapeRenderer();
        renderer.setSeriesShape(0, new Ellipse2D.Double(-6, -6, 12, 12));
        renderer.setSeriesPaint(0, Color.RED);

        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(600, 600));

        JPanel legendPanel = createLegendPanel(classMetricsMap);
        legendPanel.setPreferredSize(new Dimension(300, 600));

        setLayout(new BorderLayout());
        add(chartPanel, BorderLayout.CENTER);
        add(legendPanel, BorderLayout.EAST);
    }

    private XYSeriesCollection createDataset(Map<String, ClassMetrics> classMetricsMap) {
        XYSeries series = new XYSeries("Classes");

        for (ClassMetrics metrics : classMetricsMap.values()) {
            double instability = metrics.getInstability();
            double abstractness = metrics.getAbstractness();

            series.add(instability, abstractness);
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(series);
        return dataset;
    }

    private JPanel createLegendPanel(Map<String, ClassMetrics> classMetricsMap) {
        JPanel legendPanel = new JPanel();
        legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
        legendPanel.setBorder(BorderFactory.createTitledBorder("Class Legend"));

        for (Map.Entry<String, ClassMetrics> entry : classMetricsMap.entrySet()) {
            String className = entry.getKey();
            ClassMetrics metrics = entry.getValue();
            double instability = metrics.getInstability();
            double abstractness = metrics.getAbstractness();

            JLabel label = new JLabel(String.format("%s: (I: %.2f, A: %.2f)", className, instability, abstractness));
            legendPanel.add(label);
        }

        return legendPanel;
    }
}
