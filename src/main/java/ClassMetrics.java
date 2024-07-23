import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassMetrics {

    private String className;
    private int totalLines;
    private int commentLines;
    private int blankLines;
    private int standaloneBracketLines;
    private int executableLines;
    private int logicalLines;
    private int abstractness;

    private Set<String> dependencies;
    private List<MethodMetrics> methods;

    public ClassMetrics(String className) {
        this.className = className;
        this.dependencies = new HashSet<>();
        this.methods = new ArrayList<>();
    }

    public void addDependency(String dependency) {
        dependencies.add(dependency);
    }

    public void addMethod(MethodMetrics method) {
        methods.add(method);
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

    public int getStandaloneBracketLines() {
        return standaloneBracketLines;
    }

    public void setStandaloneBracketLines(int standaloneBracketLines) {
        this.standaloneBracketLines = standaloneBracketLines;
    }

    public Set<String> getDependencies() {
        return dependencies;
    }

    public List<MethodMetrics> getMethods() {
        return methods;
    }

}
