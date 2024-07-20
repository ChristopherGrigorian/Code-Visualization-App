import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import javax.sound.sampled.Line;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
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
            JavaParser parser = new JavaParser();
            Optional<CompilationUnit> result = parser.parse(filePath).getResult();

            if (result.isPresent()) {
                CompilationUnit compilationUnit = result.get();
                List<ClassOrInterfaceDeclaration> classDeclarations = compilationUnit.findAll(ClassOrInterfaceDeclaration.class);
                int lineCount = getLineCount(filePath);

                System.out.println("File: " + filePath);
                System.out.println("LineCount: " + lineCount);
            } else {
                System.out.println("Error parsing file: " + filePath);
            }

        } catch (IOException e) {
            throw new RuntimeException("IOException occurred while parsing file: " + filePath, e);
        }
    }

    public static int getLineCount(Path filePath) throws IOException {
        int lineCount;
        try {
            lineCount = (int) Files.lines(filePath).count();
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred while counting file lines: " + filePath, e);
        }
        return lineCount;
    }
}
