import javafx.util.Pair;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

class Galaxy extends JPanel  {
//    DirectedSparseMultigraph<String, String> graph = new DirectedSparseMultigraph<>();
    private ArrayList<Function> functions;
    private Simulator simulator = Simulator.getInstance();
    private Map<String, Color> classColors;
    public Galaxy (ArrayList<Function> functions) {
        this.functions = functions;
        setBackground(new Color(176, 250, 192));
        this.classColors = generateClassColors();
        simulator.setFunctions(functions);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Function f : functions) {
//           Draw a circle for each function
            g.setColor(classColors.get(f.getParentClass()));
            g.fillOval(f.getX(), f.getY(), 20, 20) ;
            g.drawString(f.getName(), f.getX(), f.getY());
        }
        for (Function f : functions) {
            for (Function call : f.getCalls()) {
                g.setColor(Color.BLACK);
                g.drawLine(f.getX()+10, f.getY()+10, call.getX()+10, call.getY()+10);
                // Draw arrow head
                int x1 = f.getX()+10;
                int y1 = f.getY()+10;
                int x2 = call.getX()+10;
                int y2 = call.getY()+10;
                double angle = Math.atan2(y2 - y1, x2 - x1);
                int x3 = (int) (x2 - 10 * Math.cos(angle - Math.PI / 6));
                int y3 = (int) (y2 - 10 * Math.sin(angle - Math.PI / 6));
                int x4 = (int) (x2 - 10 * Math.cos(angle + Math.PI / 6));
                int y4 = (int) (y2 - 10 * Math.sin(angle + Math.PI / 6));
                g.drawLine(x2, y2, x3, y3);
                g.drawLine(x2, y2, x4, y4);
            }
        }
//        Draw the walls
        g.setColor(Color.BLACK);
        g.drawLine(0, 0, 1000, 0);
        g.drawLine(0, 0, 0, 725);
        g.drawLine(1000, 0, 1000, 725);
        g.drawLine(0, 725, 1000, 725);

        simulator.simulateTime();
    }


     static void getGraphExample() {

     }

    static void getGraph(ArrayList<Pair<String, String>> functions) {
       // Get unique colors for each class, classes are stored as the second element in the pair
          ArrayList<String> uniqueClasses = new ArrayList<>();
          for (Pair<String, String> func : functions) {
             if (!uniqueClasses.contains(func.getValue())) {
                uniqueClasses.add(func.getValue());
             }
          }
          ArrayList<Color> colors = getDistinctColors(uniqueClasses.size());
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

    public Map<String, Color> generateClassColors() {
        ArrayList<String> uniqueClasses = getUniqueClasses();
        ArrayList<Color> colors = getDistinctColors(uniqueClasses.size());
        Map<String, Color> classColors = new HashMap<>();
        for (int i = 0; i < uniqueClasses.size(); i++) {
            classColors.put(uniqueClasses.get(i), colors.get(i));
        }
        return classColors;
    }

    public Map<String, Color> getClassColors() {
        return classColors;
    }

    static ArrayList<Color> getDistinctColors(int numColors) {
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
