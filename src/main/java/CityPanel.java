import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.jfree.chart3d.Chart3DPanel;

/**
 * @author Harold Ellis (manualdriver)
 */

public class CityPanel extends JPanel {

    private final List<Chart3DPanel> chartPanels;

    public CityPanel(LayoutManager layout) {
        super(layout);
        this.chartPanels = new ArrayList<>();
    }

    public Chart3DPanel getChartPanel() {
        if (this.chartPanels.isEmpty()) {
            return null;
        }
        return this.chartPanels.get(0);
    }

    public List<Chart3DPanel> getChartPanels() {
        return this.chartPanels;
    }

    public void setChartPanel(Chart3DPanel panel) {
        this.chartPanels.clear();
        this.chartPanels.add(panel);
    }

    public void addChartPanel(Chart3DPanel panel) {
        this.chartPanels.add(panel);
    }
}
