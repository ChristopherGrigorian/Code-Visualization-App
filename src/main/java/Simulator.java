import java.util.ArrayList;


/**
 * This class is responsible for simulating the movement of functions in the galaxy plot.
 * It calculates the forces acting on each function and updates their positions accordingly.
 * The forces include link forces, vertex forces, and wall forces.
 * The simulation is run in discrete time steps.
 *
 * @author CharlieRay668 (Charlie Ray)
 */
public class Simulator {

    // Singleton pattern
    private static Simulator instance = null;
    private ArrayList<Function> functions;

    private static final double LINK_FORCE = 0.5;
    private static final double VERTEX_FORCE = 5000;
    private static final double WALL_FORCE = 1;
    private static final int WALL_RIGHT = 950;
    private static final int WALL_BOTTOM = 725;
    private static final double EQUILIBRIUM_DISTANCE = 100;
    private static final int WALL_MARGIN = 50;

    private Simulator() {
        this.functions = new ArrayList<>();
    }

    public static Simulator getInstance() {
        if (instance == null) {
            instance = new Simulator();
        }
        return instance;
    }

    public void setFunctions(ArrayList<Function> functions) {
        this.functions = functions;
    }

    public void simulateTime() {
        if (functions == null || functions.isEmpty()) {
            return; // No functions to simulate
        }

        for (Function f : functions) {
            double[] totalForces = calculateTotalForces(f);
            applyForces(f, totalForces[0], totalForces[1]);
        }
    }

    private double[] calculateTotalForces(Function f) {
        double totalXforce = 0;
        double totalYforce = 0;

        double[] linkForces = calculateLinkForces(f);
        totalXforce += linkForces[0];
        totalYforce += linkForces[1];

        double[] vertexForces = calculateVertexForces(f);
        totalXforce += vertexForces[0];
        totalYforce += vertexForces[1];

        double[] wallForces = calculateWallForces(f);
        totalXforce += wallForces[0];
        totalYforce += wallForces[1];

        return new double[]{totalXforce, totalYforce};
    }

    private double[] calculateLinkForces(Function f) {
        double totalXforce = 0;
        double totalYforce = 0;

        for (Function call : f.getCalls()) {
            int dx = call.getX() - f.getX();
            int dy = call.getY() - f.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);
            double force = (distance - EQUILIBRIUM_DISTANCE) * LINK_FORCE;
            if (distance != 0) {
                totalXforce += force * (dx / distance);
                totalYforce += force * (dy / distance);
            }
        }

        return new double[]{totalXforce, totalYforce};
    }

    private double[] calculateVertexForces(Function f) {
        double totalXforce = 0;
        double totalYforce = 0;

        for (Function other : functions) {
            if (other != f) {
                int dx = other.getX() - f.getX();
                int dy = other.getY() - f.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);
                if (distance != 0) {
                    double force = VERTEX_FORCE / (distance * distance);
                    totalXforce -= force * (dx / distance);
                    totalYforce -= force * (dy / distance);
                }
            }
        }

        return new double[]{totalXforce, totalYforce};
    }

    private double[] calculateWallForces(Function f) {
        double totalXforce = 0;
        double totalYforce = 0;

        if (f.getX() < WALL_MARGIN) {
            totalXforce += WALL_FORCE * (WALL_MARGIN - f.getX());
        }
        if (f.getX() > WALL_RIGHT - WALL_MARGIN) {
            totalXforce -= WALL_FORCE * (f.getX() - (WALL_RIGHT - WALL_MARGIN));
        }

        if (f.getY() < WALL_MARGIN) {
            totalYforce += WALL_FORCE * (WALL_MARGIN - f.getY());
        }
        if (f.getY() > WALL_BOTTOM - WALL_MARGIN) {
            totalYforce -= WALL_FORCE * (f.getY() - (WALL_BOTTOM - WALL_MARGIN));
        }

        return new double[]{totalXforce, totalYforce};
    }

    private void applyForces(Function f, double totalXforce, double totalYforce) {
        if (Double.isNaN(totalXforce) || Double.isNaN(totalYforce)) {
            totalXforce = 0;
            totalYforce = 0;
        }

        double timeStep = 1.0;
        int newX = (int) Math.round(f.getX() + totalXforce * timeStep);
        int newY = (int) Math.round(f.getY() + totalYforce * timeStep);

        // Ensure functions do not phase through walls
        newX = Math.max(0, Math.min(newX, WALL_RIGHT));
        newY = Math.max(0, Math.min(newY, WALL_BOTTOM));

        f.setX(newX);
        f.setY(newY);
    }
}
