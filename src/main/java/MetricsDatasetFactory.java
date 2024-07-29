import org.jfree.chart3d.data.category.CategoryDataset3D;
import org.jfree.chart3d.data.category.StandardCategoryDataset3D;

import java.util.Map;

/**
 * @author Harold Ellis (manualdriver)
 */

public class MetricsDatasetFactory {

    public static CategoryDataset3D<String, String, String> createClassMetricsDataset() {
        StandardCategoryDataset3D<String, String, String> dataset = new StandardCategoryDataset3D<>();
        PowerHouse powerHouse = PowerHouse.getInstance();
        Map<String, ClassMetrics> classMetricsMap = powerHouse.getClassMetricsMap();

        for (ClassMetrics classMetrics : classMetricsMap.values()) {
            dataset.addValue(classMetrics.getTotalLines(), "Total Lines", classMetrics.getClassName(), "Total Lines");
            dataset.addValue(classMetrics.getCommentLines(), "Comment Lines", classMetrics.getClassName(), "Comment Lines");
            dataset.addValue(classMetrics.getBlankLines(), "Blank Lines", classMetrics.getClassName(), "Blank Lines");
            dataset.addValue(classMetrics.getExecutableLines(), "Executable Lines", classMetrics.getClassName(), "Executable Lines");
            dataset.addValue(classMetrics.getLogicalLines(), "Logical Lines", classMetrics.getClassName(), "Logical Lines");
        }
        return dataset;
    }

    public static CategoryDataset3D<String, String, String> createMethodMetricsDataset(String className) {
        StandardCategoryDataset3D<String, String, String> dataset = new StandardCategoryDataset3D<>();
        PowerHouse powerHouse = PowerHouse.getInstance();
        ClassMetrics classMetrics = powerHouse.getClassMetricsMap().get(className);

        if (classMetrics != null) {
            for (MethodMetrics methodMetrics : classMetrics.getMethods()) {
                dataset.addValue(methodMetrics.getLinesOfCode(), "Lines of Code", methodMetrics.getMethodName(), "Lines of Code");
            }
        }
        return dataset;
    }
}
