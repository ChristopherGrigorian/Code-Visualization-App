import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class ClassCompositionPanel extends GraphPanel {

    public ClassCompositionPanel(Map<String, ClassMetrics> metrics, File directory) {
        super();
        createGraph(metrics, directory);
        setupGraphComponent();
    }

    @Override
    protected void createGraph(Map<String, ClassMetrics> metrics, File directory) {
        graph = new SimpleGraph<>(DefaultEdge.class);
        for (String className : metrics.keySet()) {
            File file = findFileInDirectory(directory, className + ".java");
            if (file != null) {
                try {
                    Map<String, Set<String>> classCompositions = ClassAnalyzer.analyzeClassComposition(file);
                    graph.addVertex(className);
                    for (Map.Entry<String, Set<String>> entry : classCompositions.entrySet()) {
                        String clazz = entry.getKey();
                        for (String composedClass : entry.getValue()) {
                            if (!clazz.equals(composedClass)) {
                                graph.addVertex(composedClass);
                                graph.addEdge(clazz, composedClass);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
