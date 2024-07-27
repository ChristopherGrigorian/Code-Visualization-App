public class MethodCallDetails {
    private final String methodName;
    private final String parentClass;

    public MethodCallDetails(String methodName, String parentClass) {
        this.methodName = methodName;
        this.parentClass = parentClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getParentClass() {
        return parentClass;
    }

    @Override
    public String toString() {
        return "MethodCallDetails{" +
                "methodName='" + methodName + '\'' +
                ", parentClass='" + parentClass + '\'' +
                '}';
    }
}
