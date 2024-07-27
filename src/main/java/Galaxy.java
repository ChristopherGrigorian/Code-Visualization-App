import java.util.ArrayList;
import java.util.Map;
/**
 * @author CharlieRay668 (Charlie Ray)
 */
public class Galaxy {

    private static Galaxy instance;
    private ArrayList<Function> functions;

    private Galaxy() {
        this.functions = new ArrayList<>();
    }

    public static Galaxy getInstance() {
        if (instance == null) {
            instance = new Galaxy();
            PowerHouse ph = PowerHouse.getInstance();
            instance.initializeFunctions(ph.getClassMetricsMap());
        }
        return instance;
    }

    public void initializeFunctions(Map<String, ClassMetrics> metrics) {
        this.functions = new ArrayList<>();
        for (ClassMetrics metric : metrics.values()) {
            String className = metric.getClassName();
            for (MethodMetrics method : metric.getMethods()) {
                functions.add(new Function(method.getMethodName(), className, method.getLinesOfCode()));
            }
        }
        addRelationships(metrics);
    }

    private void addRelationships(Map<String, ClassMetrics> metrics) {
        for (ClassMetrics metric : metrics.values()) {
            String callerClassName = metric.getClassName();
            for (MethodMetrics method : metric.getMethods()) {
                String callerFunctionName = method.getMethodName();
                Function caller = findFunction(callerFunctionName, callerClassName);
                for (MethodCallDetails call : method.getMethodCalls()) {
                    Function callee = findFunction(call.methodName(), call.parentClass());
                    if (caller != null && callee != null) {
                        caller.addCall(callee);
                        callee.addCalledBy(caller);
                    }
                }
            }
        }
    }

    private Function findFunction(String functionName, String className) {
        for (Function f : functions) {
            if (f.getName().equalsIgnoreCase(functionName) && f.getParentClass().equalsIgnoreCase(className)) {
                return f;
            }
        }
        return null;
    }

    public ArrayList<Function> getFunctions() {
        return functions;
    }

}
