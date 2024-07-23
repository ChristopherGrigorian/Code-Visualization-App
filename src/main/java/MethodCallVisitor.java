import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class MethodCallVisitor extends VoidVisitorAdapter<String> {
    private final List<MethodCallDetails> methodCalls = new ArrayList<>();
    private String currentClass;

    @Override
    public void visit(MethodCallExpr methodCall, String currentClass) {
        super.visit(methodCall, currentClass);
        this.currentClass = currentClass;
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
        this.currentClass = currentClass;
        String className = creationExpr.getType().asString();
        methodCalls.add(new MethodCallDetails("<init>", className));
    }

    public List<MethodCallDetails> getMethodCalls() {
        return methodCalls;
    }
}
