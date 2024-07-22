import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

import java.io.File;
import java.util.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;
//import javafx.util.Pair;

public class PowerHouse {

    private static PowerHouse instance;
    private static File curDirectory;

    private PowerHouse() {
        super();
    }

    public static PowerHouse getInstance() {
        if (instance == null) {
            instance = new PowerHouse();
        }
        return instance;
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
/*
    public ArrayList<Pair<String, String>> getFunctions(String content, CompilationUnit compilationUnit) {
//        Returns a list of pairs of function names, and their parent class
        ArrayList<Pair<String, String>> functions = new ArrayList<>();
        compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classDeclaration -> {
            classDeclaration.getMethods().forEach(method -> {
                functions.add(new Pair<>(method.getNameAsString(), classDeclaration.getNameAsString()));
            });
        });
        return functions;
    }

    public ArrayList<Pair<String, String>> getFunctionsFromDirectory(String directory) {
        ArrayList<Pair<String, String>> functions = new ArrayList<>();
        try (Stream<Path> paths = Files.walk(Path.of(directory))) {
            paths.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(p -> {
                        try {
                            String content = Files.readString(p);
                            JavaParser parser = new JavaParser();
                            Optional<CompilationUnit> result = parser.parse(content).getResult();
                            if (result.isPresent()) {
                                CompilationUnit compilationUnit = result.get();
                                functions.addAll(getFunctions(content, compilationUnit));
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
        return functions;
    }

    */
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

    private final Set<String> classNames = new HashSet<>();
    private final Map<String, Set<String>> dependencies = new HashMap<>();

    private void advancedParse(CompilationUnit compilationUnit) {
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

    private void trackDependency(String sourceClass, String targetClass) {
        if (!sourceClass.equals(targetClass)) {
            dependencies.computeIfAbsent(sourceClass, k -> new HashSet<>()).add(targetClass);
        }
    }

    private void trackExtendedClassAndInterfaces(ClassOrInterfaceDeclaration declaration) {
        Optional<ClassOrInterfaceType> extendedClass = declaration.getExtendedTypes().getFirst();
        extendedClass.ifPresent(classOrInterfaceType -> trackDependency(declaration.getNameAsString(), classOrInterfaceType.getNameAsString()));

        List<ClassOrInterfaceType> implementedInterfaces = declaration.getImplementedTypes();
        for (ClassOrInterfaceType implementedInterface : implementedInterfaces) {
            trackDependency(declaration.getNameAsString(), implementedInterface.getNameAsString());
        }
    }

    public void printDependencies() {
        for (String className : dependencies.keySet()) {
            System.out.println("\nClass: " + className + " - Dependencies:");
            for (String dependency : dependencies.get(className)) {
                System.out.println("  - " + dependency);
            }
        }
    }
}
