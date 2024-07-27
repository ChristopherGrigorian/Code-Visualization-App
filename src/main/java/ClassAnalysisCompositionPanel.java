import java.util.Map;
import java.util.Set;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

/**
 * @author Eric Canihuante
 */

public class ClassAnalysisCompositionPanel extends ClassAnalysisGraphPanel {

    public ClassAnalysisCompositionPanel() {
        super();
        PowerHouse ph = PowerHouse.getInstance();
        Map<String, Set<String>> compositionData = ph.getClassCompositionData();
        createGraph(compositionData);
        setupGraphComponent();
        setExplanationText(
                "This visualization shows the composition between classes by illustrating the relationships and" +
                        " dependencies between different classes. " + "You can interact with the graph by moving" +
                        " elements around and zooming in/out using the + and - buttons. " + "You can also search for" +
                        " specific elements using the search bar and reset the highlighted results using the reset" +
                        " button."
        );
    }

    @Override
    protected void createGraph(Map<String, ?> compositionData) {
        graph = new SimpleGraph<>(DefaultEdge.class);

        for (String className : compositionData.keySet()) {
            graph.addVertex(className);  // Add class vertex

            Set<String> outgoingDependencies = (Set<String>) compositionData.get(className);
            for (String composedClass : outgoingDependencies) {
                if (!className.equals(composedClass)) { // Avoid self-loops
                    graph.addVertex(composedClass);
                    graph.addEdge(className, composedClass);
                }
            }
        }
    }
}
