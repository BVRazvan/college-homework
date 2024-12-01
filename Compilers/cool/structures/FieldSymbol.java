package cool.structures;

public class FieldSymbol extends IdSymbol {
    public int getFieldIndex() {
        return fieldIndex;
    }

    public void setFieldIndex(int fieldIndex) {
        this.fieldIndex = fieldIndex;
    }

    private int fieldIndex = -1;
    public FieldSymbol(String name) {
        super(name);
    }

    public String getSufix() {
        return "_attribute";
    }
}
