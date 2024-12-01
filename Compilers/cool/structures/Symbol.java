package cool.structures;

public class Symbol {
    protected String name;
    public boolean isFormal() {
        return Formal;
    }

    public void setFormal(boolean formal) {
        Formal = formal;
    }

    private boolean Formal;
    private boolean stackAdded;
    
    public Symbol(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }

    public String getSufix() {
        return "";
    }

    public boolean isStackAdded() {
        return stackAdded;
    }

    public void setStackAdded(boolean stackAdded) {
        this.stackAdded = stackAdded;
    }
}
