import java.util.ArrayList;
import java.util.List;

/**
 * Holds immutable data about methods within classes.
 *
 * @author christophergrigorian
 * @author Charlie Ray (Method Calls)
 */

public class MethodMetrics {
    private final String methodName;
    private final int linesOfCode;
    private int cyclomaticComplexity;
    private final List<ParameterMetrics> parameters;
    private List<MethodCallDetails> methodCalls;

    public MethodMetrics(String methodName, int linesOfCode) {
        this.methodName = methodName;
        this.linesOfCode = linesOfCode;
        this.cyclomaticComplexity = 1;
        this.parameters = new ArrayList<>();
        this.methodCalls = new ArrayList<>();
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

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public int getCyclomaticComplexity() {
        return cyclomaticComplexity;
    }

    public void setCyclomaticComplexity(int cyclomaticComplexity) {
        this.cyclomaticComplexity = cyclomaticComplexity;
    }

    public List<MethodCallDetails> getMethodCalls() { return methodCalls; }

    public void setMethodCalls(List<MethodCallDetails> methodCalls) { this.methodCalls = methodCalls; }
}
