import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.ext.JGraphXAdapter;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

/**
 * @author Eric Canihuante
 */

public abstract class GraphPanel extends JPanel {
    protected SimpleGraph<String, DefaultEdge> graph;

    public GraphPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
    }

    protected abstract void createGraph(Map<String, ClassMetrics> metrics, File directory);

    protected void setupGraphComponent() {
        JGraphXAdapter<String, DefaultEdge> graphAdapter = new JGraphXAdapter<>(graph);
        mxGraphComponent graphComponent = new mxGraphComponent(graphAdapter);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.setIntraCellSpacing(50.0);
        layout.execute(graphAdapter.getDefaultParent());
        add(graphComponent, BorderLayout.CENTER);

        // Hide edge labels by setting them to an empty string in the graph model
        graphAdapter.getEdgeToCellMap().values().forEach(edge -> {
            graphAdapter.getModel().setValue(edge, "");
        });
    }

    protected File findFileInDirectory(File directory, String fileName) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                if (file.isDirectory()) {
                    File found = findFileInDirectory(file, fileName);
                    if (found != null) {
                        return found;
                    }
                } else if (file.getName().equals(fileName)) {
                    return file;
                }
            }
        }
        return null;
    }
}
