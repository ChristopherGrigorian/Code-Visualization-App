import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.Chart3DPanel;
import org.jfree.chart3d.data.category.StandardCategoryDataset3D;
import org.jfree.chart3d.graphics3d.Dimension3D;
import org.jfree.chart3d.legend.LegendAnchor;
import org.jfree.chart3d.plot.CategoryPlot3D;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class CityPanelTest {

    private Chart3DPanel createTestChart3DPanel() {
        StandardCategoryDataset3D<String, String, String> dataset = new StandardCategoryDataset3D<>();
        dataset.addValue(1.0, "Series1", "Category1", "Value1");

        Chart3D chart = Chart3DFactory.createBarChart("Test Chart", "Test Subtitle", dataset, "Row Key", "Column Key", "Value Key");
        chart.setChartBoxColor(new Color(255, 255, 255, 127));
        chart.setLegendAnchor(LegendAnchor.BOTTOM_RIGHT);
        CategoryPlot3D plot = (CategoryPlot3D) chart.getPlot();
        plot.setGridlinePaintForValues(Color.BLACK);
        plot.setDimensions(new Dimension3D(20, 10, 10)); // Adjust dimensions as needed

        return new Chart3DPanel(chart);
    }

    @Test
    public void testAddAndGetChartPanel() {
        CityPanel cityPanel = new CityPanel(new BorderLayout());
        Chart3DPanel chartPanel = createTestChart3DPanel();
        cityPanel.addChartPanel(chartPanel);

        assertNotNull(cityPanel.getChartPanel());
        assertEquals(chartPanel, cityPanel.getChartPanel());
    }

    @Test
    public void testSetChartPanel() {
        CityPanel cityPanel = new CityPanel(new BorderLayout());
        Chart3DPanel chartPanel1 = createTestChart3DPanel();
        Chart3DPanel chartPanel2 = createTestChart3DPanel();
        cityPanel.addChartPanel(chartPanel1);
        cityPanel.setChartPanel(chartPanel2);

        assertNotNull(cityPanel.getChartPanel());
        assertEquals(chartPanel2, cityPanel.getChartPanel());
        assertNotEquals(chartPanel1, cityPanel.getChartPanel());
    }

    @Test
    public void testGetChartPanels() {
        CityPanel cityPanel = new CityPanel(new BorderLayout());
        Chart3DPanel chartPanel1 = createTestChart3DPanel();
        Chart3DPanel chartPanel2 = createTestChart3DPanel();
        cityPanel.addChartPanel(chartPanel1);
        cityPanel.addChartPanel(chartPanel2);

        assertNotNull(cityPanel.getChartPanels());
        assertEquals(2, cityPanel.getChartPanels().size());
        assertEquals(chartPanel1, cityPanel.getChartPanels().get(0));
        assertEquals(chartPanel2, cityPanel.getChartPanels().get(1));
    }
}
