import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import javax.swing.*;
import java.io.File;
import java.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;


/**
 * @author christophergrigorian
 * @author Charlie Ray (Imbedded Method Calls -> MethodCallVisitor, MethodCallDetails
 */

public class PowerHouse {

    private static PowerHouse instance;
    private static File curDirectory;

    private Map<String, ClassMetrics> classMetricsMap;

    private PowerHouse() {
        super();
        this.classMetricsMap = new HashMap<>();
    }

    public static PowerHouse getInstance() {
        if (instance == null) {
            instance = new PowerHouse();
        }
        return instance;
    }

    public Map<String, ClassMetrics> getClassMetricsMap() {
        return classMetricsMap;
    }

    public File getCurDirectory() {
        return curDirectory;
    }

    public void setCurDirectory(File curDirectory) {
        PowerHouse.curDirectory = curDirectory;
    }

    public void parseDirectory(Path path) throws IOException {
        try (Stream<Path> paths = Files.walk(path)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(this::parseFile);
        } catch (IOException e) {
            throw new IOException("Error walking through directory", e);
        }
    }

    public void parseFile(Path filePath) {
        try {
            String content = Files.readString(filePath);
            JavaParser parser = new JavaParser();
            Optional<CompilationUnit> result = parser.parse(content).getResult();

            if (result.isPresent()) {
                CompilationUnit compilationUnit = result.get();

                int totalLines = getLineCount(content);
                int commentLines = getCommentLineCount(content, compilationUnit);
                int blankLines = getBlankLineCount(content);
                int standaloneBracketLines = getStandaloneBracketLineCount(content);
                int executableLines = totalLines - commentLines - blankLines - standaloneBracketLines;
                int logicalLines = getLogicalLineCount(content, compilationUnit);
                int abstractness = getAbstractness(compilationUnit);

                List<ClassOrInterfaceDeclaration> classDeclarations = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
                for (ClassOrInterfaceDeclaration classDeclaration : classDeclarations) {
                    String className = classDeclaration.getNameAsString();
                    ClassMetrics classMetrics = new ClassMetrics(className);

                    classMetrics.setTotalLines(totalLines);
                    classMetrics.setCommentLines(commentLines);
                    classMetrics.setBlankLines(blankLines);
                    classMetrics.setStandaloneBracketLines(standaloneBracketLines);
                    classMetrics.setExecutableLines(executableLines);
                    classMetrics.setLogicalLines(logicalLines);
                    classMetrics.setAbstractness(abstractness);

                    for (FieldDeclaration field : classDeclaration.getFields()) {
                        for (VariableDeclarator variable : field.getVariables()) {
                            Type fieldType = variable.getType();
                            if (fieldType.isClassOrInterfaceType()) {
                                String referencedClass = fieldType.asClassOrInterfaceType().getNameAsString();
                                classMetrics.addOutgoingDependency(referencedClass);
                            }
                        }
                    }

                    for (MethodDeclaration method : classDeclaration.getMethods()) {
                        MethodMetrics methodMetrics = new MethodMetrics(method.getNameAsString(),
                                method.getEnd().get().line - method.getBegin().get().line);

                        int cyclomaticComplexity = calculateCyclomaticComplexity(method);
                        methodMetrics.setCyclomaticComplexity(cyclomaticComplexity);

                        for (Parameter parameter : method.getParameters()) {
                            String paramName = parameter.getNameAsString();
                            Type paramType = parameter.getType();
                            methodMetrics.addParameter(new ParameterMetrics(paramName, paramType.toString()));
                            if (paramType.isClassOrInterfaceType()) {
                                String referencedClass = paramType.asClassOrInterfaceType().getNameAsString();
                                classMetrics.addOutgoingDependency(referencedClass);
                            }
                        }

                        // Collect method calls
                        MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
                        method.accept(methodCallVisitor, className);
                        methodMetrics.setMethodCalls(methodCallVisitor.getMethodCalls());

                        // Add outgoing dependencies from method calls
                        for (MethodCallDetails methodCall : methodCallVisitor.getMethodCalls()) {
                            String parentClass = methodCall.getParentClass();
                            if (parentClass != null && !parentClass.isEmpty() && classMetricsMap.containsKey(parentClass)) {
                                classMetrics.addOutgoingDependency(parentClass);
                            }
                        }


                        classMetrics.addMethod(methodMetrics);
                    }

                    trackExtendedClassAndInterfaces(classDeclaration, classMetrics);
                    classMetricsMap.put(className, classMetrics);

                }
            } else {
                System.out.println("Error parsing file: " + filePath);
            }

            populateIncomingDependencies();
            populateInstabilityAndDistance();

        } catch (IOException e) {
            throw new RuntimeException("IOException occurred while parsing file: " + filePath, e);
        }
    }

    private int calculateCyclomaticComplexity(MethodDeclaration method) {
        CyclomaticComplexityVisitor visitor = new CyclomaticComplexityVisitor();
        method.accept(visitor, null);
        return visitor.getComplexity();
    }

    public int getLineCount(String content) throws IOException {
        return (int) content.lines().count();
    }

    public int getBlankLineCount(String content) {
        return (int) content.lines().filter(String::isBlank).count();
    }

    public int getStandaloneBracketLineCount(String content) {
        return (int) content.lines()
                .map(String::trim)
                .filter(line -> line.equals("{") || line.equals("}"))
                .count();
    }

    public int getCommentLineCount(String content, CompilationUnit compilationUnit) {
        int commentLines = compilationUnit.getAllComments().stream()
                .mapToInt(comment -> {
                    if (comment instanceof BlockComment || comment instanceof JavadocComment) {
                        return (int) comment.getContent().lines().count();
                    }
                    return 0;
                })
                .sum();

        commentLines += (int) content.lines()
                .filter(line -> line.trim().startsWith("//"))
                .count();


        return commentLines;
    }

    public int getLogicalLineCount(String content, CompilationUnit compilationUnit) {
        long semiColonLines = Stream.of(content.split("\r\n|\r|\n"))
                .map(line -> removeCommentsFromLine(line).trim())
                .filter(line -> line.endsWith(";"))
                .count();

        long forLoops = compilationUnit.findAll(ForStmt.class).size();
        return (int) (semiColonLines + forLoops);
    }

    public String removeCommentsFromLine(String line) {
        line = line.replaceAll("//.*", "");
        line = line.replaceAll("/\\*.*?\\*/", "");
        return line;
    }
    private int getAbstractness(CompilationUnit compilationUnit) {
        return compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .map(declaration -> {
                    if (declaration.isInterface() || declaration.isAbstract()) {
                        return 1;
                    }
                    return 0;
                })
                .orElse(0);
    }

    private void trackExtendedClassAndInterfaces(ClassOrInterfaceDeclaration declaration, ClassMetrics classMetrics) {
        Optional<ClassOrInterfaceType> extendedClass = declaration.getExtendedTypes().getFirst();
        extendedClass.ifPresent(classOrInterfaceType -> classMetrics.addOutgoingDependency(classOrInterfaceType.getNameAsString()));

        List<ClassOrInterfaceType> implementedInterfaces = declaration.getImplementedTypes();
        for (ClassOrInterfaceType implementedInterface : implementedInterfaces) {
            classMetrics.addOutgoingDependency(implementedInterface.getNameAsString());
        }
    }

    private void populateIncomingDependencies() {
        for (ClassMetrics metrics : classMetricsMap.values()) {
            System.out.println(metrics.getClassName());
            System.out.println(metrics.getOutgoingDependencies());
            for (String dependency : metrics.getOutgoingDependencies()) {
                ClassMetrics dependentClassMetrics = classMetricsMap.get(dependency);
                if (dependentClassMetrics != null) {
                    dependentClassMetrics.addIncomingDependency(metrics.getClassName());
                }

            }
            System.out.println(metrics.getIncomingDependencies());
        }
    }

    private void populateInstabilityAndDistance() {
        for (ClassMetrics metrics : classMetricsMap.values()) {
            int outgoing = metrics.getOutgoingDependencies().size();
            int incoming = metrics.getIncomingDependencies().size();
            metrics.setInstability((double) outgoing / (incoming + outgoing));
            metrics.setDistance(Math.abs(metrics.getAbstractness() + metrics.getInstability() - 1));
        }
    }
}