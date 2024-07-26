import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GalaxyTest {

    private Galaxy galaxy;

    @BeforeEach
    public void setUp() {
        galaxy = Galaxy.getInstance();
    }

    @Test
    public void testSingletonInstance() {
        Galaxy instance = Galaxy.getInstance();
        assertSame(galaxy, instance, "Expected the same instance of Galaxy");
    }

    @Test
    public void testInitializeFunctions() {
        Map<String, ClassMetrics> metrics = new HashMap<>();
        // Initialize metrics with mock data
        galaxy.initializeFunctions(metrics);
        assertNotNull(galaxy.getFunctions(), "Functions should be initialized");
    }
}
