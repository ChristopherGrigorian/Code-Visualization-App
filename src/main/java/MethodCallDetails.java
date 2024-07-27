/**
 * Carries immutable data about method calls within methods
 *
 * @author Charlie Ray
 * @author Christopher (Converted to record class because IDE suggested it)
 */

public record MethodCallDetails(String methodName, String parentClass) {

    @Override
    public String toString() {
        return "MethodCallDetails{" +
                "methodName='" + methodName + '\'' +
                ", parentClass='" + parentClass + '\'' +
                '}';
    }
}
