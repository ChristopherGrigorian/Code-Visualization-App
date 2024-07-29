import org.jfree.chart3d.Chart3DPanel;
import org.junit.jupiter.api.Test;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class CityPanelFactoryTest {

    @Test
    public void testCreateClassMetricsPanel() {
        JPanel panel = CityPanelFactory.createClassMetricsPanel();
        assertNotNull(panel);
        assertEquals(800, panel.getPreferredSize().width);
        assertEquals(600, panel.getPreferredSize().height);

        CityPanel cityPanel = (CityPanel) panel;
        Chart3DPanel chartPanel = cityPanel.getChartPanel();
        assertNotNull(chartPanel);
    }

    @Test
    public void testCreateMethodMetricsPanel() {
        PowerHouse powerHouse = PowerHouse.getInstance();
        Map<String, ClassMetrics> classMetricsMap = new HashMap<>();
        ClassMetrics classMetrics = new ClassMetrics("TestClass1");
        classMetrics.addMethod(new MethodMetrics("Method1", 50));
        classMetrics.addMethod(new MethodMetrics("Method2", 60));
        classMetricsMap.put("TestClass1", classMetrics);
        powerHouse.setClassMetricsMap(classMetricsMap);

        JPanel panel = CityPanelFactory.createMethodMetricsPanel();
        assertNotNull(panel);

        Component[] components = ((JPanel) panel).getComponents();
        assertInstanceOf(JTabbedPane.class, components[0]);
        JTabbedPane tabbedPane = (JTabbedPane) components[0];
        assertEquals(1, tabbedPane.getTabCount());
        assertNotNull(tabbedPane.getComponentAt(0));
    }
}
