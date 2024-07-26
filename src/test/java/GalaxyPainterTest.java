import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GalaxyPainterTest {

    private GalaxyPainter galaxyPainter;
    private ArrayList<Function> functions;

    @BeforeEach
    public void setUp() {
        functions = new ArrayList<>();
        galaxyPainter = new GalaxyPainter(functions);
    }

    @Test
    public void testGenerateClassColors() {
        galaxyPainter.generateClassColors();
        assertNotNull(galaxyPainter.getClassColors(), "Class colors should be generated");
    }

    @Test
    public void testGetFunctions() {
        assertEquals(functions, galaxyPainter.getFunctions(), "Expected functions to be the same as initialized");
    }
}
