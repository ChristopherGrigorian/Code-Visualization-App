import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Visitor that walks through methods to parse method calls within said methods.
 * Means to create call graph.
 *
 * @author Charlie Ray
 */

public class MethodCallVisitor extends VoidVisitorAdapter<String> {
    private final List<MethodCallDetails> methodCalls = new ArrayList<>();

    @Override
    public void visit(MethodCallExpr methodCall, String currentClass) {
        super.visit(methodCall, currentClass);
        String methodName = methodCall.getNameAsString();
        String className = methodCall.getScope()
                .map(scope -> {
                    String scopeString = scope.toString();
                    if (scopeString.equals("this") || scopeString.equals("super")) {
                        return currentClass;
                    }
                    return scopeString;
                })
                .orElse(currentClass); // Use currentClass if scope is absent
        methodCalls.add(new MethodCallDetails(methodName, className));
    }

    @Override
    public void visit(ObjectCreationExpr creationExpr, String currentClass) {
        super.visit(creationExpr, currentClass);
        String className = creationExpr.getType().asString();
        methodCalls.add(new MethodCallDetails(className, className));
    }

    @Override
    public void visit(ConstructorDeclaration constructorDeclaration, String currentClass) {
        super.visit(constructorDeclaration, currentClass);
        String constructorName = constructorDeclaration.getNameAsString();
        methodCalls.add(new MethodCallDetails(constructorName, currentClass));
    }

    public List<MethodCallDetails> getMethodCalls() {
        return methodCalls;
    }
}