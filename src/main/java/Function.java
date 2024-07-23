import java.util.ArrayList;

public class Function {
    private int length;
    private String name;
    private String parentClass;

    private int x;
    private int y;


    private ArrayList<Function> calls = new ArrayList<>();

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

    public ArrayList<Function> getCalls() {
        return calls;
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
}
