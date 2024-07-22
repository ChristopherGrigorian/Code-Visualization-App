import javax.swing.*;
import java.util.ArrayList;
import javafx.util.Pair;

public class CharlieMain {
    /**
     * This class is intentionally made very bland and simple
     * In the hopes that it will make it easier to merge later
     * I didn't want to mess with the current Main function, as it looks like whoever
     * wrote it wants some certain fucntionality, the goal with this is just to use the
     * PowerHouse class to use my Galaxy class to create the graph with a specified directory
     * The directory is going to be hardcoded as that should make it clear when trying to merge
     * That also means this file wont work on anyone elses machine, but that's fine.
     *
     * @author Charlie Ray
     */
    public static void main(String[] args) {
//        JFrame frame = new JFrame("Galaxy Plot");
        PowerHouse ph = PowerHouse.getInstance();
        ArrayList<Pair<String, String>> functions = ph.getFunctionsFromDirectory("/Users/charlieray/Desktop/School/CSC 307/finalproj/src");
//        BasicVisualizationServer<String, String> visualGraph = Galaxy.getGraph(functions);
        JFrame frame = new JFrame("Galaxy Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.getContentPane().add(visualGraph);
        frame.pack();
        frame.setVisible(true);

    }
}
