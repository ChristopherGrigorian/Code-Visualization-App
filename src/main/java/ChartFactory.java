import org.jfree.chart3d.Chart3D;
import org.jfree.chart3d.Chart3DFactory;
import org.jfree.chart3d.data.category.CategoryDataset3D;
import org.jfree.chart3d.graphics3d.Dimension3D;
import org.jfree.chart3d.legend.LegendAnchor;
import org.jfree.chart3d.plot.CategoryPlot3D;

import java.awt.*;

/**
 * @author Harold Ellis (manualdriver)
 */

public class ChartFactory {

    public static Chart3D createChart(String title, String subtitle, String rowKey, String columnKey, String valueKey, CategoryDataset3D<String, String, String> dataset) {
        Chart3D chart = Chart3DFactory.createBarChart(title, subtitle, dataset, rowKey, columnKey, valueKey);
        chart.setChartBoxColor(new Color(255, 255, 255, 127));
        chart.setLegendAnchor(LegendAnchor.BOTTOM_RIGHT);
        CategoryPlot3D plot = (CategoryPlot3D) chart.getPlot();
        plot.setGridlinePaintForValues(Color.BLACK);
        plot.setDimensions(new Dimension3D(20, 10, 10));
        return chart;
    }
}
