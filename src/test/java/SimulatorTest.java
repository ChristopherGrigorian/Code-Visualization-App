import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class SimulatorTest {

    private Simulator simulator;
    private ArrayList<Function> functions;

    @BeforeEach
    public void setUp() {
        simulator = Simulator.getInstance();
        functions = new ArrayList<>();
        simulator.setFunctions(functions);
    }

    @Test
    public void testSingletonInstance() {
        Simulator instance = Simulator.getInstance();
        assertSame(simulator, instance, "Expected the same instance of Simulator");
    }

    @Test
    public void testSimulateTime() {
        // Add some mock functions to the list
        functions.add(new Function("mockFunction", "mockClass", 10));
        simulator.simulateTime();
        assertNotNull(simulator, "Simulator should run without exceptions");
    }
}
