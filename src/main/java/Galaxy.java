import javafx.util.Pair;


import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

class Galaxy extends JPanel  {
//    DirectedSparseMultigraph<String, String> graph = new DirectedSparseMultigraph<>();
    private ArrayList<Function> functions;
    public Galaxy (ArrayList<Function> functions) {
        this.functions = functions;
        setBackground(new Color(176, 250, 192));
    }

    @Override
    public void paintComponent(Graphics g) {
//        Overseer overseer = Overseer.getInstance();
        super.paintComponent(g);
        for (int i = 0; i < 10; i++) {
//            Draw random circles
            g.setColor(new Color((int) (Math.random() * 0x1000000)));
            g.fillOval((int) (Math.random() * 1200), (int) (Math.random() * 600), 50, 50);

        }
//        for (Component s : overseer.getStack()) {
//            s.drawShape(g);
//        }
//        if (overseer.getBox() != null) {
//            overseer.getBox().drawShape(g);
//        }
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

    static ArrayList<Color> getDistinctColors(int numColors) {
        ArrayList<Color> colors = new ArrayList<>();
        for (int i = 0; i < numColors; i++) {
            // Generate a random color
            Color color = new Color((int) (Math.random() * 0x1000000));
            // Add the color to the list
            colors.add(color);
        }
        return colors;
    }
}
