import java.util.ArrayList;
import java.util.List;

public class MethodMetrics {
    private String methodName;
    private int linesOfCode;
    private int cyclomaticComplexity;
    private List<ParameterMetrics> parameters;

    public MethodMetrics(String methodName, int linesOfCode) {
        this.methodName = methodName;
        this.linesOfCode = linesOfCode;
        this.cyclomaticComplexity = 1;
        this.parameters = new ArrayList<>();
    }

    public List<ParameterMetrics> getParameters() {
        return parameters;
    }

    public void addParameter(ParameterMetrics parameter) {
        parameters.add(parameter);
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public void setCyclomaticComplexity(int cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
    }
}
