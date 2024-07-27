/**
 * Hold immutable data about parameters for method calls.
 *
 * @author christophergrigorian
 */

public class ParameterMetrics {
    private final String paramName;
    private final String paramType;

    public ParameterMetrics(String paramName, String paramType) {
        this.paramName = paramName;
        this.paramType = paramType;
    }

    public String getParamName() {
        return paramName;
    }

    public String getParamType() {
        return paramType;
    }
}
