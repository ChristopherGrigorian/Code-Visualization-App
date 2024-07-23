import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.stmt.*;

public class CyclomaticComplexityVisitor extends VoidVisitorAdapter<Void> {
    private int complexity = 1;

    @Override
    public void visit(IfStmt n, Void arg) {
        super.visit(n, arg);
        complexity++;
    }

    @Override
    public void visit(ForStmt n, Void arg) {
        super.visit(n, arg);
        complexity++;
    }

    @Override
    public void visit(ForEachStmt n, Void arg) {
        super.visit(n, arg);
        complexity++;
    }

    @Override
    public void visit(WhileStmt n, Void arg) {
        super.visit(n, arg);
        complexity++;
    }

    @Override
    public void visit(DoStmt n, Void arg) {
        super.visit(n, arg);
        complexity++;
    }

    @Override
    public void visit(SwitchStmt n, Void arg) {
        super.visit(n, arg);
        complexity += n.getEntries().size();
    }

    @Override
    public void visit(CatchClause n, Void arg) {
        super.visit(n, arg);
        complexity++;
    }

    public int getComplexity() {
        return complexity;
    }
}
