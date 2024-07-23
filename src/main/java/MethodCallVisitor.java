import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

public class MethodCallVisitor extends VoidVisitorAdapter<Void> {
    private final List<MethodCallDetails> methodCalls = new ArrayList<>();
    private String currentClass = "Unknown";
    private String currentMethod = "Unknown";

    @Override
    public void visit(ClassOrInterfaceDeclaration classDeclaration, Void arg) {
        String previousClass = currentClass;
        currentClass = classDeclaration.getNameAsString();
        super.visit(classDeclaration, arg);
        currentClass = previousClass;
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, Void arg) {
        String previousMethod = currentMethod;
        currentMethod = methodDeclaration.getNameAsString();
        super.visit(methodDeclaration, arg);
        currentMethod = previousMethod;
    }

    @Override
    public void visit(MethodCallExpr methodCall, Void arg) {
        super.visit(methodCall, arg);
        String methodName = methodCall.getNameAsString();
        String className = methodCall.getScope()
                .map(scope -> scope.toString())
                .orElse(currentClass);
        methodCalls.add(new MethodCallDetails(methodName, className));
    }

    public List<MethodCallDetails> getMethodCalls() {
        return methodCalls;
    }
}
