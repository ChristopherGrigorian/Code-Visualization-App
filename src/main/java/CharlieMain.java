import javax.swing.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import javafx.util.Pair;
import java.awt.BorderLayout;

public class CharlieMain extends JFrame {
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
//        PowerHouse ph = PowerHouse.getInstance();
//        ArrayList<Pair<String, String>> functions = ph.getFunctionsFromDirectory("/Users/charlieray/Desktop/School/CSC 307/finalproj/src");
//        BasicVisualizationServer<String, String> visualGraph = Galaxy.getGraph(functions);
        JFrame frame = new CharlieMain();
        frame.setTitle("Galaxy Plot");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setVisible(true);
        Simulator simulator = Simulator.getInstance();
        while (true) {
//            simulator.simulateTime();
            frame.repaint();
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public CharlieMain () {
        ArrayList<Function> functions = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            functions.add(new Function("Function" + i, "Class" + i, i));
            if (i % 3 == 0 && i != 0) {
                functions.get(i).addCall(functions.get(i - 1));
                functions.get(i-1).addCalledBy(functions.get(i));
            } else if (i % 2 == 0 && i != 0) {
                functions.get(i).addCall(functions.get(i - 2));
                functions.get(i-2).addCalledBy(functions.get(i));
            }
        }
        JPanel galaxy = new GalaxyPanel(functions);
        JPanel test1 = new JPanel();
        test1.setBackground(new java.awt.Color(255, 255, 255));
        JPanel test2 = new JPanel();
        test2.setBackground(new java.awt.Color(255, 0, 0));
        JPanel test3 = new JPanel();
        test3.setBackground(new java.awt.Color(0, 255, 0));
        JPanel test4 = new JPanel();
        test4.setBackground(new java.awt.Color(0, 0, 255));
//        Create tabs to switch between panels
        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Galaxy", galaxy);
        tabs.addTab("Test1", test1);
        tabs.addTab("Test2", test2);
        tabs.addTab("Test3", test3);
        tabs.addTab("Test4", test4);
        add(tabs, BorderLayout.CENTER);
    }
}
