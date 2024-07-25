import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;

/**
 * @author Eric Canihuante
 */

public class ClassResponsibilityPanel extends JPanel {
    public ClassResponsibilityPanel(Map<String, ClassMetrics> metrics, File directory) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (String className : metrics.keySet()) {
            File file = findFileInDirectory(directory, className + ".java");
            if (file != null) {
                try {
                    int responsibilities = analyzeClassResponsibilities(file);
                    dataset.addValue(responsibilities, "Responsibilities", className);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        JFreeChart barChart = ChartFactory.createBarChart(
                "Class Responsibilities",
                "Class",
                "Responsibilities",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);

        CategoryPlot plot = barChart.getCategoryPlot();
        BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setItemMargin(0.1);

        // Color coding based on responsibilities
        for (int i = 0; i < dataset.getColumnCount(); i++) {
            String className = (String) dataset.getColumnKey(i);
            int responsibilities = dataset.getValue(0, i).intValue();
            if (responsibilities <= 5) {
                renderer.setSeriesPaint(i, Color.GREEN);
            } else {
                renderer.setSeriesPaint(i, Color.RED);
            }
        }

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        ChartPanel chartPanel = new ChartPanel(barChart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        add(chartPanel, BorderLayout.CENTER);
    }

    private File findFileInDirectory(File directory, String fileName) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    File found = findFileInDirectory(file, fileName);
                    if (found != null) {
                        return found;
                    }
                } else if (file.getName().equals(fileName)) {
                    return file;
                }
            }
        }
        return null;
    }

    private int analyzeClassResponsibilities(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(in).getResult().orElseThrow(() -> new IOException("Parsing error"));

        Map<String, Integer> classResponsibilities = new HashMap<>();
        new ResponsibilityVisitor(classResponsibilities).visit(cu, null);

        return classResponsibilities.values().stream().findFirst().orElse(0);
    }

    private static class ResponsibilityVisitor extends VoidVisitorAdapter<Void> {
        private final Map<String, Integer> classResponsibilities;

        ResponsibilityVisitor(Map<String, Integer> classResponsibilities) {
            this.classResponsibilities = classResponsibilities;
        }

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            String className = n.getNameAsString();
            int responsibilityCount = n.getMethods().size() + n.getFields().size();
            classResponsibilities.put(className, responsibilityCount);
            super.visit(n, arg);
        }
    }
}
