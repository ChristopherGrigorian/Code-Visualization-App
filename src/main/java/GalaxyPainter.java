import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * @author CharlieRay668 (Charlie Ray)
 */
class GalaxyPainter extends JPanel {
    private ArrayList<Function> functions;
    private Simulator simulator = Simulator.getInstance();
    private Map<String, Color> classColors;

    public GalaxyPainter(ArrayList<Function> functions) {
        this.functions = functions;
        setBackground(new Color(176, 250, 192));
        this.classColors = generateClassColors();
        simulator.setFunctions(functions);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawFunctions(g);
        drawLinks(g);
        drawWalls(g);
        simulator.simulateTime();
    }

    private void drawFunctions(Graphics g) {
        for (Function f : functions) {
            g.setColor(classColors.get(f.getParentClass()));
            int size = f.getLength();
            g.fillOval(f.getX(), f.getY(), size, size);
            g.drawString(f.getName(), f.getX(), f.getY());
            drawWarningOrErrorIcons(g, f, size);
        }
    }

    private void drawWarningOrErrorIcons(Graphics g, Function f, int size) {
        if (size >= 40 && size < 60) {
            drawWarningIcon(g, f, size);
        } else if (size >= 60) {
            drawErrorIcon(g, f, size);
        }
    }

    private void drawWarningIcon(Graphics g, Function f, int size) {
        g.setColor(Color.ORANGE);
        g.fillOval(f.getX() + size, f.getY(), 10, 10);
        g.setColor(Color.BLACK);
        g.drawLine(f.getX() + size + 5, f.getY() + 2, f.getX() + size + 5, f.getY() + 8);
        g.drawLine(f.getX() + size + 5, f.getY() + 8, f.getX() + size + 2, f.getY() + 10);
        g.drawLine(f.getX() + size + 5, f.getY() + 8, f.getX() + size + 8, f.getY() + 10);
    }

    private void drawErrorIcon(Graphics g, Function f, int size) {
        g.setColor(Color.RED);
        g.fillOval(f.getX() + size, f.getY(), 10, 10);
        g.setColor(Color.BLACK);
        g.drawLine(f.getX() + size + 2, f.getY() + 2, f.getX() + size + 8, f.getY() + 8);
        g.drawLine(f.getX() + size + 8, f.getY() + 2, f.getX() + size + 2, f.getY() + 8);
    }

    private void drawLinks(Graphics g) {
        for (Function f : functions) {
            int fOffset = f.getLength() / 2;
            for (Function call : f.getCalls()) {
                int callOffset = call.getLength() / 2;
                g.setColor(Color.BLACK);
                g.drawLine(f.getX() + fOffset, f.getY() + fOffset, call.getX() + callOffset, call.getY() + callOffset);
                drawArrowHead(g, f, call, fOffset, callOffset);
            }
        }
    }

    private void drawArrowHead(Graphics g, Function f, Function call, int fOffset, int callOffset) {
        int x1 = f.getX() + fOffset;
        int y1 = f.getY() + fOffset;
        int x2 = call.getX() + callOffset;
        int y2 = call.getY() + callOffset;
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int x3 = (int) (x2 - 10 * Math.cos(angle - Math.PI / 6));
        int y3 = (int) (y2 - 10 * Math.sin(angle - Math.PI / 6));
        int x4 = (int) (x2 - 10 * Math.cos(angle + Math.PI / 6));
        int y4 = (int) (y2 - 10 * Math.sin(angle + Math.PI / 6));
        g.drawLine(x2, y2, x3, y3);
        g.drawLine(x2, y2, x4, y4);
    }

    private void drawWalls(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(0, 0, 1000, 0);
        g.drawLine(0, 0, 0, 725);
        g.drawLine(1000, 0, 1000, 725);
        g.drawLine(0, 725, 1000, 725);
    }

    public Map<String, Color> generateClassColors() {
        ArrayList<String> uniqueClasses = getUniqueClasses();
        ArrayList<Color> colors = getDistinctColors(uniqueClasses.size());
        Map<String, Color> classColors = new HashMap<>();
        for (int i = 0; i < uniqueClasses.size(); i++) {
            classColors.put(uniqueClasses.get(i), colors.get(i));
        }
        return classColors;
    }

    public ArrayList<String> getUniqueClasses() {
        ArrayList<String> uniqueClasses = new ArrayList<>();
        for (Function f : this.functions) {
            if (!uniqueClasses.contains(f.getParentClass())) {
                uniqueClasses.add(f.getParentClass());
            }
        }
        return uniqueClasses;
    }

    public Map<String, Color> getClassColors() {
        return classColors;
    }

    public static ArrayList<Color> getDistinctColors(int numColors) {
        ArrayList<Color> colors = new ArrayList<>();
        for (int i = 0; i < numColors; i++) {
            // Generate a random color that is not too light
            Color color = new Color((int) (Math.random() * 200),
                    (int) (Math.random() * 200),
                    (int) (Math.random() * 200));
            colors.add(color);
        }
        return colors;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }
}
