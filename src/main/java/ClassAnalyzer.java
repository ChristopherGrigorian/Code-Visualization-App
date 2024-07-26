import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ClassAnalyzer {

    public static Map<String, Set<String>> analyzeClassComposition(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(in).getResult().orElseThrow(() -> new IOException("Parsing error"));

        Map<String, Set<String>> classCompositions = new HashMap<>();
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(classDeclaration -> {
            String className = classDeclaration.getNameAsString();
            Set<String> fields = new HashSet<>();
            for (FieldDeclaration field : classDeclaration.getFields()) {
                fields.add(field.getElementType().asString());
            }
            classCompositions.put(className, fields);
        });

        return classCompositions;
    }

    public static int getResponsibilities(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        JavaParser parser = new JavaParser();
        CompilationUnit cu = parser.parse(in).getResult().orElseThrow(() -> new IOException("Parsing error"));

        int responsibilities = 0;
        for (ClassOrInterfaceDeclaration classDeclaration : cu.findAll(ClassOrInterfaceDeclaration.class)) {
            for (MethodDeclaration method : classDeclaration.getMethods()) {
                responsibilities++;
            }
        }

        return responsibilities;
    }
}
