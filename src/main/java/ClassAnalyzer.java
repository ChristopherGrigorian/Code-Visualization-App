import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * @author Eric Canihuante
 */

public class ClassAnalyzer {

    public static Map<String, Set<String>> analyzeClassComposition(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(in).getResult().orElseThrow(() -> new IOException("Parsing error"));

        Map<String, Set<String>> classCompositions = new HashMap<>();
        new CompositionVisitor(classCompositions).visit(cu, null);

        return classCompositions;
    }

    private static class CompositionVisitor extends VoidVisitorAdapter<Void> {
        private final Map<String, Set<String>> classCompositions;

        CompositionVisitor(Map<String, Set<String>> classCompositions) {
            this.classCompositions = classCompositions;
        }

        @Override
        public void visit(FieldDeclaration n, Void arg) {
            String className = n.getParentNode().get().findFirst(ClassOrInterfaceDeclaration.class).get().getNameAsString();
            String fieldType = n.getElementType().asString();
            if (!className.equals(fieldType)) { // Prevent self-loops
                classCompositions.computeIfAbsent(className, k -> new HashSet<>()).add(fieldType);
            }
            super.visit(n, arg);
        }
    }
}