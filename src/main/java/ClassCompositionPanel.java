import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.ext.JGraphXAdapter;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

/**
 * @author Eric Canihuante
 */

public class ClassCompositionPanel extends JPanel {
    public ClassCompositionPanel(Map<String, ClassMetrics> metrics, File directory) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        SimpleGraph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        for (String className : metrics.keySet()) {
            File file = findFileInDirectory(directory, className + ".java");
            if (file != null) {
                try {
                    Map<String, Set<String>> classCompositions = analyzeClassComposition(file);
                    for (Map.Entry<String, Set<String>> entry : classCompositions.entrySet()) {
                        String clazz = entry.getKey();
                        graph.addVertex(clazz);
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

    private File findFileInDirectory(File directory, String fileName) {
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

    private Map<String, Set<String>> analyzeClassComposition(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(in).getResult().orElseThrow(() -> new IOException("Parsing error"));

        Map<String, Set<String>> classCompositions = new HashMap<>();
        new CompositionVisitor(classCompositions).visit(cu, null);

        return classCompositions;
    }

    private static class CompositionVisitor extends VoidVisitorAdapter<Void> {
        private final Map<String, Set<String>> classCompositions;

        CompositionVisitor(Map<String, Set<String>> classCompositions) {
            this.classCompositions = classCompositions;
        }

        @Override
        public void visit(FieldDeclaration n, Void arg) {
            String className = n.getParentNode().get().findFirst(ClassOrInterfaceDeclaration.class).get().getNameAsString();
            String fieldType = n.getElementType().asString();
            if (!className.equals(fieldType)) { // Prevent self-loops
                classCompositions.computeIfAbsent(className, k -> new HashSet<>()).add(fieldType);
            }
            super.visit(n, arg);
        }
    }
}
