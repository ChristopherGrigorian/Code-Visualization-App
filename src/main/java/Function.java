import java.util.ArrayList;

public class Function {
    private int length;
    private String name;
    private String parentClass;

    private ArrayList<Function> calls = new ArrayList<>();

    public Function(String name, String parentClass, int length) {
        this.name = name;
        this.parentClass = parentClass;
        this.length = length;
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
}
