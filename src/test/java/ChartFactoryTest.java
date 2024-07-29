import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.data.category.StandardCategoryDataset3D;
import org.jfree.chart3d.graphics3d.Dimension3D;
import org.jfree.chart3d.plot.CategoryPlot3D;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class ChartFactoryTest {

    @Test
    public void testCreateChart() {
        StandardCategoryDataset3D<String, String, String> dataset = new StandardCategoryDataset3D<>();
        dataset.addValue(1.0, "Series1", "Category1", "Value1");

        Chart3D chart = ChartFactory.createChart("Test Chart", "Test Subtitle", "Row Key", "Column Key", "Value Key", dataset);
        assertNotNull(chart);

        CategoryPlot3D plot = (CategoryPlot3D) chart.getPlot();
        assertEquals(Color.BLACK, plot.getGridlinePaintForValues());
        assertEquals(new Dimension3D(20, 10, 10), plot.getDimensions());
    }
}
