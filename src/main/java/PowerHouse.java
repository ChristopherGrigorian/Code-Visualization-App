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
import java.util.stream.Stream;

public class PowerHouse {

    private static PowerHouse instance;

    private PowerHouse() {
        super();
    }

    public static PowerHouse getInstance() {
        if (instance == null) {
            instance = new PowerHouse();
        }
        return instance;
    }

    public void parseDirectory(Path path) throws IOException {
        try (Stream<Path> paths = Files.walk(path)) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(PowerHouse::parseFile);
        } catch (IOException e) {
            throw new IOException("Error walking through directory", e);
        }
    }

    public static void parseFile(Path filePath) {
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


                //ClassOrInterfaceDeclaration classDeclaration = compilationUnit.findFirst(ClassOrInterfaceDeclaration.class).orElse(null);

                System.out.println("File: " + filePath);
                System.out.println("Total Lines (LOC): " + totalLines);
                System.out.println("Executable Lines (eLOC): " + executableLines);
                System.out.println("Comment Lines (lLOC): " + commentLines);
                System.out.println("Logical Lines of Code (lLOC): " + logicalLines);
                System.out.println("Abstractness: " + abstractness);
                advancedParse(compilationUnit);
            } else {
                System.out.println("Error parsing file: " + filePath);
            }

        } catch (IOException e) {
            throw new RuntimeException("IOException occurred while parsing file: " + filePath, e);
        }
    }

    public static int getLineCount(String content) throws IOException {
        return (int) content.lines().count();
    }

    public static int getBlankLineCount(String content) {
        return (int) content.lines().filter(String::isBlank).count();
    }

    public static int getStandaloneBracketLineCount(String content) {
        return (int) content.lines()
                .map(String::trim)
                .filter(line -> line.equals("{") || line.equals("}"))
                .count();
    }

    public static int getCommentLineCount(String content, CompilationUnit compilationUnit) {
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

    public static int getLogicalLineCount(String content, CompilationUnit compilationUnit) {
        long semiColonLines = Stream.of(content.split("\r\n|\r|\n"))
                .map(line -> removeCommentsFromLine(line).trim())
                .filter(line -> line.endsWith(";"))
                .count();

        long forLoops = compilationUnit.findAll(ForStmt.class).size();
        return (int) (semiColonLines + forLoops);
    }

    public static String removeCommentsFromLine(String line) {
        line = line.replaceAll("//.*", "");
        line = line.replaceAll("/\\*.*?\\*/", "");
        return line;
    }

    private static int getAbstractness(CompilationUnit compilationUnit) {
        return compilationUnit.findFirst(ClassOrInterfaceDeclaration.class)
                .map(declaration -> {
                    if (declaration.isInterface() || declaration.isAbstract()) {
                        return 1;
                    }
                    return 0;
                })
                .orElse(0);
    }


    private static final Set<String> classNames = new HashSet<>();
    private static final Map<String, Set<String>> dependencies = new HashMap<>();

    private static void advancedParse(CompilationUnit compilationUnit) {
        List<ClassOrInterfaceDeclaration> classDeclaration = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
        for (ClassOrInterfaceDeclaration c : classDeclaration) {
            classNames.add(c.getNameAsString());

            System.out.println("\nClass: " + c.getName());
            for (FieldDeclaration f : c.getFields()) {
                for (VariableDeclarator v : f.getVariables()) {
                    Type fieldType = v.getType();
                    String fieldName = v.getNameAsString();

                    if (fieldType.isClassOrInterfaceType()) {
                        String referencedClass = fieldType.asClassOrInterfaceType().getNameAsString();
                        trackDependency(c.getNameAsString(), referencedClass);
                    }

                    System.out.println("Field: " + fieldName + "Type: " + fieldType);
                }
            }

            System.out.println("\nMethods:");
            for (MethodDeclaration m : c.getMethods()) {
                System.out.println("> > > Method: " + m.getName());
                System.out.println("Lines of code: " + (m.getEnd().get().line - m.getBegin().get().line));

                for (Parameter p : m.getParameters()) {
                    Type paramType = p.getType();
                    String paramName = p.getNameAsString();

                    if (paramType.isClassOrInterfaceType()) {
                        String referencedClass = paramType.asClassOrInterfaceType().getNameAsString();
                        trackDependency(c.getNameAsString(), referencedClass);
                    }

                    System.out.println("Parameter: " + paramName + "Type: " + paramType);
                }
            }
            trackExtendedClassAndInterfaces(c);
        }
    }

    private static void trackDependency(String sourceClass, String targetClass) {
        if (!sourceClass.equals(targetClass)) {
            dependencies.computeIfAbsent(sourceClass, k -> new HashSet<>()).add(targetClass);
        }
    }

    private static void trackExtendedClassAndInterfaces(ClassOrInterfaceDeclaration declaration) {
        Optional<ClassOrInterfaceType> extendedClass = declaration.getExtendedTypes().getFirst();
        extendedClass.ifPresent(classOrInterfaceType -> trackDependency(declaration.getNameAsString(), classOrInterfaceType.getNameAsString()));

        List<ClassOrInterfaceType> implementedInterfaces = declaration.getImplementedTypes();
        for (ClassOrInterfaceType implementedInterface : implementedInterfaces) {
            trackDependency(declaration.getNameAsString(), implementedInterface.getNameAsString());
        }
    }

    public static void printDependencies() {
        for (String className : dependencies.keySet()) {
            System.out.println("\nClass: " + className + " - Dependencies:");
            for (String dependency : dependencies.get(className)) {
                System.out.println("  - " + dependency);
            }
        }
    }
}
