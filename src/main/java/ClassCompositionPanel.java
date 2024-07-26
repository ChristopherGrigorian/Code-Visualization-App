import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * @author Eric Canihuante
 */

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
                    if (!graph.containsVertex(className)) {
                        graph.addVertex(className);
                    }
                    for (Map.Entry<String, Set<String>> entry : classCompositions.entrySet()) {
                        String clazz = entry.getKey();
                        if (!graph.containsVertex(clazz)) {
                            graph.addVertex(clazz);
                        }
                        for (String composedClass : entry.getValue()) {
                            if (!clazz.equals(composedClass)) {
                                if (!graph.containsVertex(composedClass)) {
                                    graph.addVertex(composedClass);
                                }
                                if (!graph.containsEdge(clazz, composedClass)) {
                                    graph.addEdge(clazz, composedClass);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
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
