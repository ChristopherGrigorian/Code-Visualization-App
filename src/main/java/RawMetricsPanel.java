import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Map;

/**
 * @author christophergrigorian
 */

public class RawMetricsPanel extends JPanel {

    private final JTable westTable;
    private final JTable eastTable;
    private final DecimalFormat decimalFormat;

    public RawMetricsPanel() {
        setLayout(new BorderLayout());

        JPanel northPanel = new JPanel();
        northPanel.setPreferredSize(new Dimension(1600, 25)); // Increase width to accommodate more data
        northPanel.setLayout(new BorderLayout());
        northPanel.add(new JLabel("Individual Class Data"), BorderLayout.WEST);
        northPanel.add(new JLabel("Average Data"), BorderLayout.EAST);

        // Create tables and add to panel
        westTable = new JTable();
        westTable.setModel(new DefaultTableModel(new Object[]{"Class", "LOC", "eLOC", "lLOC", "A", "I", "D", "Outgoing", "Incoming", "CC"}, 0));
        setColumnWidths(westTable, 275, 35, 35, 35, 20, 20, 20, 50, 50, 25);

        JScrollPane westScrollPane = new JScrollPane(westTable);
        westScrollPane.setPreferredSize(new Dimension(775, 800));
        westScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        eastTable = new JTable();
        eastTable.setModel(new DefaultTableModel(new Object[]{"Metric", "Average Value"}, 0));

        JScrollPane eastScrollPane = new JScrollPane(eastTable);
        eastScrollPane.setPreferredSize(new Dimension(400, 800));
        eastScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        add(westScrollPane, BorderLayout.WEST);
        add(eastScrollPane, BorderLayout.EAST);
        add(northPanel, BorderLayout.NORTH);

        // Initialize DecimalFormat for 3 decimal places
        decimalFormat = new DecimalFormat("#.###");

        // Populate the west table with metrics and the east table with averages
        populateWestTable();
        populateEastTable();
    }

    private void setColumnWidths(JTable table, int... widths) {
        TableColumnModel columnModel = table.getColumnModel();
        for (int i = 0; i < widths.length; i++) {
            if (i < columnModel.getColumnCount()) {
                columnModel.getColumn(i).setPreferredWidth(widths[i]);
            }
        }
    }

    private void populateWestTable() {
        DefaultTableModel model = (DefaultTableModel) westTable.getModel();
        Map<String, ClassMetrics> classMetricsMap = PowerHouse.getInstance().getClassMetricsMap();

        for (ClassMetrics metrics : classMetricsMap.values()) {
            model.addRow(new Object[]{
                    metrics.getClassName(),
                    metrics.getTotalLines(),
                    metrics.getExecutableLines(),
                    metrics.getLogicalLines(),
                    metrics.getAbstractness(),
                    metrics.getInstability(),
                    metrics.getDistance(),
                    metrics.getOutgoingDependencies().size(),
                    metrics.getIncomingDependencies().size(),
                    metrics.getHighestCyclomaticComplexity()
            });

            for (MethodMetrics method : metrics.getMethods()) {
                model.addRow(new Object[]{
                        "  Method: " + method.getMethodName(),
                        method.getLinesOfCode(),
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        "",
                        method.getCyclomaticComplexity()
                });

                for (ParameterMetrics param : method.getParameters()) {
                    model.addRow(new Object[]{
                            "    Parameter: " + param.getParamName() + " (" + param.getParamType() + ")",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            "",
                            ""
                    });
                }
            }

            // Add an empty row as a gap between classes
            model.addRow(new Object[]{"", "", "", "", "", "", "", "", "", ""});
        }
    }

    private void populateEastTable() {
        DefaultTableModel model = (DefaultTableModel) eastTable.getModel();
        Map<String, ClassMetrics> classMetricsMap = PowerHouse.getInstance().getClassMetricsMap();

        MetricsAveragesCalculator calculator = new MetricsAveragesCalculator(classMetricsMap);

        model.addRow(new Object[]{"Average LOC", decimalFormat.format(calculator.calculateAverageLOC())});
        model.addRow(new Object[]{"Median LOC", decimalFormat.format(calculator.calculateMedianLOC())});
        model.addRow(new Object[]{"Standard Deviation LOC", decimalFormat.format(calculator.calculateStandardDeviationLOC())});
        model.addRow(new Object[]{"Max LOC", decimalFormat.format(calculator.calculateMaxLOC())});
        model.addRow(new Object[]{"Min LOC", decimalFormat.format(calculator.calculateMinLOC())});

        model.addRow(new Object[]{"Average eLOC", decimalFormat.format(calculator.calculateAverageeLOC())});
        model.addRow(new Object[]{"Median eLOC", decimalFormat.format(calculator.calculateMedianeLOC())});
        model.addRow(new Object[]{"Standard Deviation eLOC", decimalFormat.format(calculator.calculateStandardDeviationeLOC())});
        model.addRow(new Object[]{"Max eLOC", decimalFormat.format(calculator.calculateMaxeLOC())});
        model.addRow(new Object[]{"Min eLOC", decimalFormat.format(calculator.calculateMineLOC())});

        model.addRow(new Object[]{"Average lLOC", decimalFormat.format(calculator.calculateAveragelLOC())});
        model.addRow(new Object[]{"Median lLOC", decimalFormat.format(calculator.calculateMedianlLOC())});
        model.addRow(new Object[]{"Standard Deviation lLOC", decimalFormat.format(calculator.calculateStandardDeviationlLOC())});
        model.addRow(new Object[]{"Max lLOC", decimalFormat.format(calculator.calculateMaxlLOC())});
        model.addRow(new Object[]{"Min lLOC", decimalFormat.format(calculator.calculateMinlLOC())});

        model.addRow(new Object[]{"Average Abstractness", decimalFormat.format(calculator.calculateAverageAbstractness())});
        model.addRow(new Object[]{"Average Instability", decimalFormat.format(calculator.calculateAverageInstability())});
        model.addRow(new Object[]{"Average Distance", decimalFormat.format(calculator.calculateAverageDistance())});
        model.addRow(new Object[]{"Average CC", decimalFormat.format(calculator.calculateAverageCyclomaticComplexity())});
        model.addRow(new Object[]{"Average Methods per Class", decimalFormat.format(calculator.calculateAverageMethodsPerClass())});
        model.addRow(new Object[]{"Average Lines per Method", decimalFormat.format(calculator.calculateAverageLinesPerMethod())});
        model.addRow(new Object[]{"Average Parameters per Method", decimalFormat.format(calculator.calculateAverageParametersPerMethod())});
        model.addRow(new Object[]{"Comment Density", decimalFormat.format(calculator.calculateCommentDensity())});
    }


}
