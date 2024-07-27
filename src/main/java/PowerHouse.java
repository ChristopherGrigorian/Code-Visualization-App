import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import java.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * This class parses Java source code directories and calculates various metrics for each class found within those directories.
 *
 * @author christophergrigorian
 * @author Charlie Ray (Imbedded Method Calls -> MethodCallVisitor, MethodCallDetails
 * @author Eric Canihuante (Cohesion, Composition, and Responsibility Parsing Data)
 */

public class PowerHouse {

    private static PowerHouse instance;

    private final Map<String, ClassMetrics> classMetricsMap;

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
            Optional<CompilationUnit> result = parseContent(content);

            if (result.isPresent()) {
                CompilationUnit compilationUnit = result.get();

                int totalLines = getLineCount(content);
                int commentLines = getCommentLineCount(content, compilationUnit);
                int blankLines = getBlankLineCount(content);
                int standaloneBracketLines = getStandaloneBracketLineCount(content);
                int executableLines = totalLines - commentLines - blankLines - standaloneBracketLines;
                int logicalLines = getLogicalLineCount(content, compilationUnit);
                int abstractness = getAbstractness(compilationUnit);

                List<ClassOrInterfaceDeclaration> classDeclarations = getClassDeclarations(compilationUnit);
                for (ClassOrInterfaceDeclaration classDeclaration : classDeclarations) {
                    ClassMetrics classMetrics = collectClassMetrics(classDeclaration, totalLines, commentLines, blankLines,
                            executableLines, logicalLines, abstractness);
                    classMetricsMap.put(classDeclaration.getNameAsString(), classMetrics);
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

    private Optional<CompilationUnit> parseContent(String content) {
        JavaParser parser = new JavaParser();
        return parser.parse(content).getResult();
    }

    private List<ClassOrInterfaceDeclaration> getClassDeclarations(CompilationUnit compilationUnit) {
        return compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
    }

    private ClassMetrics collectClassMetrics(ClassOrInterfaceDeclaration classDeclaration, int totalLines, int commentLines,
                                             int blankLines, int executableLines,
                                             int logicalLines, int abstractness) {

        ClassMetrics classMetrics = new ClassMetrics(classDeclaration.getNameAsString());
        classMetrics.setTotalLines(totalLines);
        classMetrics.setCommentLines(commentLines);
        classMetrics.setBlankLines(blankLines);
        classMetrics.setExecutableLines(executableLines);
        classMetrics.setLogicalLines(logicalLines);
        classMetrics.setAbstractness(abstractness);

        collectFieldMetrics(classDeclaration, classMetrics);
        collectConstructorMetrics(classDeclaration, classMetrics);
        collectMethodMetrics(classDeclaration, classMetrics);
        trackExtendedClassAndInterfaces(classDeclaration, classMetrics);

        return classMetrics;
    }

    private void collectFieldMetrics(ClassOrInterfaceDeclaration classDeclaration, ClassMetrics classMetrics) {
        for (FieldDeclaration field : classDeclaration.getFields()) {
            for (VariableDeclarator variable : field.getVariables()) {
                Type fieldType = variable.getType();
                if (fieldType.isClassOrInterfaceType()) {
                    String referencedClass = fieldType.asClassOrInterfaceType().getNameAsString();
                    classMetrics.addOutgoingDependency(referencedClass);
                }
            }
        }
    }

    private void collectConstructorMetrics(ClassOrInterfaceDeclaration classDeclaration, ClassMetrics classMetrics) {
        for (ConstructorDeclaration constructor : classDeclaration.getConstructors()) {
            if (constructor.getBegin().isPresent() && constructor.getEnd().isPresent()) {
                MethodMetrics methodMetrics = new MethodMetrics(constructor.getNameAsString(),
                        constructor.getEnd().get().line - constructor.getBegin().get().line);

                MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
                constructor.accept(methodCallVisitor, classDeclaration.getNameAsString());
                methodMetrics.setMethodCalls(methodCallVisitor.getMethodCalls());

                classMetrics.addMethod(methodMetrics);
            }
        }
    }

    private void collectMethodMetrics(ClassOrInterfaceDeclaration classDeclaration, ClassMetrics classMetrics) {
        for (MethodDeclaration method : classDeclaration.getMethods()) {
            if (method.getBegin().isPresent() && method.getEnd().isPresent()) {
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

                MethodCallVisitor methodCallVisitor = new MethodCallVisitor();
                method.accept(methodCallVisitor, classDeclaration.getNameAsString());
                methodMetrics.setMethodCalls(methodCallVisitor.getMethodCalls());

                for (MethodCallDetails methodCall : methodCallVisitor.getMethodCalls()) {
                    String parentClass = methodCall.getParentClass();
                    if (parentClass != null && !parentClass.isEmpty() && classMetricsMap.containsKey(parentClass)) {
                        classMetrics.addOutgoingDependency(parentClass);
                    }
                }

                classMetrics.addMethod(methodMetrics);
            }
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
            for (String dependency : metrics.getOutgoingDependencies()) {
                ClassMetrics dependentClassMetrics = classMetricsMap.get(dependency);
                if (dependentClassMetrics != null) {
                    dependentClassMetrics.addIncomingDependency(metrics.getClassName());
                }

            }
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

    public Map<String, Map<String, Set<String>>> getClassCohesionData() {
        Map<String, Map<String, Set<String>>> cohesionData = new HashMap<>();
        for (ClassMetrics classMetrics : classMetricsMap.values()) {
            Map<String, Set<String>> methodDependencies = new HashMap<>();
            for (MethodMetrics methodMetrics : classMetrics.getMethods()) {
                Set<String> filteredDependencies = methodMetrics.getMethodCalls().stream()
                        .filter(call -> !isLibraryMethod(call.getParentClass()))
                        .map(MethodCallDetails::getMethodName)
                        .collect(Collectors.toSet());
                methodDependencies.put(methodMetrics.getMethodName(), filteredDependencies);
            }
            cohesionData.put(classMetrics.getClassName(), methodDependencies);
        }
        return cohesionData;
    }

    public Map<String, Set<String>> getClassCompositionData() {
        Map<String, Set<String>> compositionData = new HashMap<>();
        for (ClassMetrics classMetrics : classMetricsMap.values()) {
            Set<String> filteredDependencies = classMetrics.getOutgoingDependencies().stream()
                    .filter(dependency -> !isLibraryClass(dependency))
                    .collect(Collectors.toSet());
            compositionData.put(classMetrics.getClassName(), filteredDependencies);
        }
        return compositionData;
    }

    private boolean isLibraryMethod(String className) {
        return className.startsWith("java.") || className.startsWith("javax.") || className.startsWith("org.");
    }

    private boolean isLibraryClass(String className) {
        return className.startsWith("java.") || className.startsWith("javax.") || className.startsWith("org.");
    }

    public Map<String, Integer> getClassResponsibilityData() {
        Map<String, Integer> responsibilityData = new HashMap<>();

        for (Map.Entry<String, ClassMetrics> entry : classMetricsMap.entrySet()) {
            String className = entry.getKey();
            ClassMetrics classMetrics = entry.getValue();
            int responsibilities = classMetrics.getMethods().size();

            responsibilityData.put(className, responsibilities);
        }

        return responsibilityData;
    }

}