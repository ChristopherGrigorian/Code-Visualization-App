import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

public class GalaxyPanel extends JPanel {

    public GalaxyPanel(ArrayList<Function> functions) {
        Galaxy galaxy = new Galaxy(functions);
        // Create sidebar with legend for class
        JPanel sidebar = new JPanel();
        JLabel title = new JLabel("Legend");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        sidebar.add(title, BorderLayout.NORTH);
        sidebar.setBackground(new java.awt.Color(255, 255, 255));
        Map<String, Color> classColors = galaxy.getClassColors();
        for (String className : classColors.keySet()) {
            JPanel classPanel = new JPanel();
            classPanel.setBackground(classColors.get(className));
            sidebar.add(classPanel);
            JLabel classNameLabel = new JLabel(className);
            sidebar.add(classNameLabel);
        }
        //stack labels vertically
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        //make it scrollable
        JScrollPane scrollPane = new JScrollPane(sidebar);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(200, 800));
        sidebar.setPreferredSize(new Dimension(200, 800));


        // Add galaxy and sidebar to panel
        this.setLayout(new BorderLayout());
        this.add(galaxy, BorderLayout.CENTER);
        this.add(scrollPane, BorderLayout.EAST);
        this.setSize(1200, 800);
    }
}
