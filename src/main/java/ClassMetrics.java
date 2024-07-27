import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Holds immutable data about each class within source code.
 *
 * @author christophergrigorian
 */

public class ClassMetrics {

    private final String className;
    private int totalLines;
    private int commentLines;
    private int blankLines;
    private int executableLines;
    private int logicalLines;
    private int abstractness;
    private double instability;
    private double distance;
    private int highestCyclomaticComplexity;

    private final Set<String> outgoingDependencies;
    private final Set<String> incomingDependencies;
    private final List<MethodMetrics> methods;

    public ClassMetrics(String className) {
        this.className = className;
        this.outgoingDependencies = new HashSet<>();
        this.incomingDependencies = new HashSet<>();
        this.methods = new ArrayList<>();
        this.highestCyclomaticComplexity = 0;
    }

    public void addOutgoingDependency(String dependency) {
        outgoingDependencies.add(dependency);
    }

    public void addIncomingDependency(String dependency) {
        incomingDependencies.add(dependency);
    }

    public void addMethod(MethodMetrics method) {
        methods.add(method);
        if (method.getCyclomaticComplexity() > highestCyclomaticComplexity) {
            highestCyclomaticComplexity = method.getCyclomaticComplexity();
        }
    }

    public String getClassName() {
        return className;
    }

    public int getTotalLines() {
        return totalLines;
    }

    public void setTotalLines(int totalLines) {
        this.totalLines = totalLines;
    }

    public int getCommentLines() {
        return commentLines;
    }

    public void setCommentLines(int commentLines) {
        this.commentLines = commentLines;
    }

    public int getBlankLines() {
        return blankLines;
    }

    public void setBlankLines(int blankLines) {
        this.blankLines = blankLines;
    }

    public int getExecutableLines() {
        return executableLines;
    }

    public void setExecutableLines(int executableLines) {
        this.executableLines = executableLines;
    }

    public int getLogicalLines() {
        return logicalLines;
    }

    public void setLogicalLines(int logicalLines) {
        this.logicalLines = logicalLines;
    }

    public int getAbstractness() {
        return abstractness;
    }

    public void setAbstractness(int abstractness) {
        this.abstractness = abstractness;
    }

    public Set<String> getOutgoingDependencies() {
        return outgoingDependencies;
    }

    public Set<String> getIncomingDependencies() {
        return incomingDependencies;
    }

    public List<MethodMetrics> getMethods() {
        return methods;
    }

    public double getInstability() {
        return instability;
    }

    public void setInstability(double instability) {
        this.instability = instability;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getHighestCyclomaticComplexity() {
        return highestCyclomaticComplexity;
    }
}
