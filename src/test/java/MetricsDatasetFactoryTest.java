import org.jfree.chart3d.data.category.CategoryDataset3D;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class MetricsDatasetFactoryTest {

    @Test
    public void testCreateClassMetricsDataset() {
        PowerHouse powerHouse = PowerHouse.getInstance();
        Map<String, ClassMetrics> classMetricsMap = new HashMap<>();
        classMetricsMap.put("TestClass1", new ClassMetrics("TestClass1"));
        classMetricsMap.put("TestClass2", new ClassMetrics("TestClass2"));
        powerHouse.setClassMetricsMap(classMetricsMap);

        CategoryDataset3D<String, String, String> dataset = MetricsDatasetFactory.createClassMetricsDataset();
        assertNotNull(dataset);
        assertEquals(5, dataset.getSeriesCount()); // Total Lines, Comment Lines, Blank Lines, Executable Lines, Logical Lines
        assertEquals(2, dataset.getRowCount()); // The two classes
    }

    @Test
    public void testCreateMethodMetricsDataset() {
        PowerHouse powerHouse = PowerHouse.getInstance();
        Map<String, ClassMetrics> classMetricsMap = new HashMap<>();
        ClassMetrics classMetrics = new ClassMetrics("TestClass1");
        classMetrics.addMethod(new MethodMetrics("Method1", 50));
        classMetrics.addMethod(new MethodMetrics("Method2", 60));
        classMetricsMap.put("TestClass1", classMetrics);
        powerHouse.setClassMetricsMap(classMetricsMap);

        CategoryDataset3D<String, String, String> dataset = MetricsDatasetFactory.createMethodMetricsDataset("TestClass1");
        assertNotNull(dataset);
        assertEquals(1, dataset.getSeriesCount()); // Lines of code
        assertEquals(2, dataset.getRowCount()); // The two methods
    }
}
