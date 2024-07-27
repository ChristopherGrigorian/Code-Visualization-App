import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.ext.JGraphXAdapter;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

/**
 * @author Eric Canihuante
 */

public abstract class ClassAnalysisGraphPanel extends JPanel {
    protected SimpleGraph<String, DefaultEdge> graph;
    protected JGraphXAdapter<String, DefaultEdge> graphAdapter;
    protected mxGraphComponent graphComponent;
    private JLabel searchResultLabel;
    private JTextArea explanation;

    public ClassAnalysisGraphPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // Explanation blurb
        explanation = new JTextArea();
        explanation.setEditable(false);
        explanation.setLineWrap(true);
        explanation.setWrapStyleWord(true);
        explanation.setBackground(getBackground());
        explanation.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));  // Default padding for other panels

        // Search functionality
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset");
        searchResultLabel = new JLabel("");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText().trim().toLowerCase();
                graphAdapter.clearSelection();

                Object[] vertices = graphAdapter.getChildVertices(graphAdapter.getDefaultParent());
                int matchCount = 0;
                for (Object vertex : vertices) {
                    String label = graphAdapter.getLabel(vertex);
                    if (label != null && label.toLowerCase().contains(searchText)) {
                        highlightVertex(vertex);
                        matchCount++;
                    } else {
                        resetVertexStyle(vertex);
                    }
                }
                searchResultLabel.setText("(" + matchCount + " matching strings found)");
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                searchResultLabel.setText("");
                resetAllVertexStyles();
            }
        });

        controlPanel.add(new JLabel("Search:"));
        controlPanel.add(searchField);
        controlPanel.add(searchButton);
        controlPanel.add(resetButton);
        controlPanel.add(searchResultLabel);

        // Zoom functionality
        JButton zoomInButton = new JButton("+");
        JButton zoomOutButton = new JButton("-");

        zoomInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphComponent.zoomIn();
            }
        });

        zoomOutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                graphComponent.zoomOut();
            }
        });

        JPanel zoomPanel = new JPanel();
        zoomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomOutButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(controlPanel, BorderLayout.WEST);
        topPanel.add(zoomPanel, BorderLayout.EAST);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.add(explanation, BorderLayout.NORTH);
        northPanel.add(topPanel, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
    }

    // Adjust method signature to be flexible
    protected abstract void createGraph(Map<String, ?> metrics);

    protected void setupGraphComponent() {
        graphAdapter = new JGraphXAdapter<>(graph);
        graphComponent = new mxGraphComponent(graphAdapter);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphAdapter);
        layout.setIntraCellSpacing(50.0);
        layout.execute(graphAdapter.getDefaultParent());

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(graphComponent, BorderLayout.CENTER);

        add(centerPanel, BorderLayout.CENTER);

        // Hide edge labels by setting them to an empty string in the graph model
        Object[] edges = graphAdapter.getChildEdges(graphAdapter.getDefaultParent());
        for (Object edge : edges) {
            graphAdapter.getModel().setValue(edge, "");
        }
    }

    private void highlightVertex(Object vertex) {
        graphAdapter.getModel().setStyle(vertex, "fillColor=yellow");
    }

    private void resetVertexStyle(Object vertex) {
        graphAdapter.getModel().setStyle(vertex, "");
    }

    private void resetAllVertexStyles() {
        Object[] vertices = graphAdapter.getChildVertices(graphAdapter.getDefaultParent());
        for (Object vertex : vertices) {
            resetVertexStyle(vertex);
        }
    }

    protected void setExplanationText(String text) {
        explanation.setText(text);
    }
}
