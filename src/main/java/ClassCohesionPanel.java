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
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.ext.JGraphXAdapter;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

/**
 * @author Eric Canihuante
 */

public class ClassCohesionPanel extends JPanel {
    public ClassCohesionPanel(Map<String, ClassMetrics> metrics, File directory) {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        SimpleGraph<String, DefaultEdge> graph = new SimpleGraph<>(DefaultEdge.class);

        for (String className : metrics.keySet()) {
            File file = findFileInDirectory(directory, className + ".java");
            if (file != null) {
                try {
                    Map<String, Set<String>> methodToFields = analyzeClassCohesion(file);
                    graph.addVertex(className);  // Add class vertex
                    for (Map.Entry<String, Set<String>> entry : methodToFields.entrySet()) {
                        String method = entry.getKey();
                        String methodVertex = className + "::" + method;  // Unique identifier for the method
                        graph.addVertex(methodVertex);
                        graph.addEdge(className, methodVertex);  // Edge from class to method
                        for (String field : entry.getValue()) {
                            graph.addVertex(field);
                            graph.addEdge(methodVertex, field);
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

        // Hiding edge labels
        graphAdapter.getEdgeToCellMap().values().forEach(edge -> {
            if (edge instanceof com.mxgraph.model.mxCell) {
                ((com.mxgraph.model.mxCell) edge).setValue("");
            }
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

    private Map<String, Set<String>> analyzeClassCohesion(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(in).getResult().orElseThrow(() -> new IOException("Parsing error"));

        Map<String, Set<String>> methodToFields = new HashMap<>();
        new MethodFieldVisitor(methodToFields).visit(cu, null);

        return methodToFields;
    }

    private static class MethodFieldVisitor extends VoidVisitorAdapter<Void> {
        private final Map<String, Set<String>> methodToFields;

        MethodFieldVisitor(Map<String, Set<String>> methodToFields) {
            this.methodToFields = methodToFields;
        }

        @Override
        public void visit(MethodDeclaration n, Void arg) {
            String methodName = n.getNameAsString();
            Set<String> fields = new HashSet<>();
            n.getBody().ifPresent(body -> body.findAll(FieldAccessExpr.class).forEach(field -> fields.add(field.getNameAsString())));
            methodToFields.put(methodName, fields);
            super.visit(n, arg);
        }
    }
}
