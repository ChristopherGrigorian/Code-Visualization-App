import java.util.ArrayList;

/**
 * @author CharlieRay668 (Charlie Ray)
 */
public class Function {
    private int length;
    private String name;
    private String parentClass;

    private int x;
    private int y;

    private boolean selected;


    private ArrayList<Function> calls = new ArrayList<>();
    private ArrayList<Function> calledBy = new ArrayList<>();

    public Function(String name, String parentClass, int length) {
        this.name = name;
        this.parentClass = parentClass;
        this.length = length;
        this.x = (int) (Math.random() * 500);
        this.y = (int) (Math.random() * 500);
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    public String getParentClass() {
        return parentClass;
    }

    public void addCall(Function f) {
        calls.add(f);
    }

    public void addCalledBy(Function f) {
        calledBy.add(f);
    }

    public ArrayList<Function> getCalls() {
        return calls;
    }

    public ArrayList<Function> getCalledBy() {
        return calledBy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean contains(int x, int y) {
        return x >= this.x && x <= this.x + 20 && y >= this.y && y <= this.y + 20;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isSelected() {
        return selected;
    }
}
