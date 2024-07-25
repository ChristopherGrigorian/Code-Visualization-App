import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class GalaxyPanel extends JPanel {

    public GalaxyPanel() {
        Galaxy galaxy = Galaxy.getInstance();
        GalaxyPainter galaxyPainter = new GalaxyPainter(galaxy.getFunctions());
        GalaxyMouseListener galaxyMouseListener = new GalaxyMouseListener(galaxyPainter);
        galaxyPainter.addMouseListener(galaxyMouseListener);
        galaxyPainter.addMouseMotionListener(galaxyMouseListener);

        JScrollPane scrollPane = createSidebar(galaxyPainter);
        JPanel controlPanel = new GalaxyControlPanel();

        this.setLayout(new BorderLayout());
        this.add(galaxyPainter, BorderLayout.CENTER);
        this.add(scrollPane, BorderLayout.EAST);
        this.add(controlPanel, BorderLayout.WEST);
        this.setSize(1200, 800);
    }

    private JScrollPane createSidebar(GalaxyPainter galaxyPainter) {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        JLabel title = new JLabel("Legend");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        sidebar.add(title);
        sidebar.setBackground(new Color(255, 255, 255));
        Map<String, Color> classColors = galaxyPainter.getClassColors();
        for (String className : classColors.keySet()) {
            JPanel classPanel = new JPanel();
            classPanel.setBackground(classColors.get(className));
            sidebar.add(classPanel);
            JLabel classNameLabel = new JLabel(className);
            sidebar.add(classNameLabel);
        }
        JScrollPane scrollPane = new JScrollPane(sidebar);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(200, 800));
        sidebar.setPreferredSize(new Dimension(200, 800));
        return scrollPane;
    }
}