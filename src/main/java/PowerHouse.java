import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

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
            JavaParser parser = new JavaParser();
            CompilationUnit compilationUnit = parser.parse(filePath).getResult().orElse(null);
            if (compilationUnit != null) {
                System.out.println("File: " + filePath);
            } else {
                System.out.println("Error parsing file: " + filePath);
            }

        } catch (IOException e) {
            throw new RuntimeException("IOException occurred while parsing file: " + filePath, e);
        }
    }
}
