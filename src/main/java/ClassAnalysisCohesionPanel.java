import java.util.Map;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * @author Eric Canihuante
 */

public class ClassAnalysisCohesionPanel extends ClassAnalysisGraphPanel {

    public ClassAnalysisCohesionPanel() {
        super();
        PowerHouse ph = PowerHouse.getInstance();
        Map<String, Map<String, Set<String>>> cohesionData = ph.getClassCohesionData();
        createGraph(cohesionData);
        setupGraphComponent();
        setExplanationText(
                "This visualization shows the cohesion within classes by illustrating the internal structure and " +
                        "dependencies within each class. " + "You can interact with the graph by moving elements around"
                        + " and zooming in/out using the + and - buttons. " + "You can also search for specific" +
                        " elements" + " using the search bar and reset the highlighted results using the reset button."
        );
    }

    @Override
    protected void createGraph(Map<String, ?> cohesionData) {
        graph = new SimpleGraph<>(DefaultEdge.class);

        for (String className : cohesionData.keySet()) {
            graph.addVertex(className);  // Add class vertex

            Map<String, Set<String>> methodFieldDependencies = (Map<String, Set<String>>) cohesionData.get(className);
            for (String methodName : methodFieldDependencies.keySet()) {
                String methodVertex = className + "::" + methodName;  // Unique identifier for the method
                graph.addVertex(methodVertex);
                graph.addEdge(className, methodVertex);  // Edge from class to method

                Set<String> fieldDependencies = methodFieldDependencies.get(methodName);
                for (String field : fieldDependencies) {
                    graph.addVertex(field);
                    graph.addEdge(methodVertex, field);
                }
            }
        }
    }
}
