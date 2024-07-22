import javafx.util.Pair;
import org.jgrapht.*;


import java.awt.*;
import java.util.ArrayList;

class Galaxy {
//    DirectedSparseMultigraph<String, String> graph = new DirectedSparseMultigraph<>();

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
