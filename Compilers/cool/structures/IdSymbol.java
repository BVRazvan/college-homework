package cool.structures;

public class IdSymbol extends Symbol {
    protected TypeSymbol type;
    public boolean isGlobal;

    public IdSymbol(String name) {
        super(name);
    }

    public void setType(TypeSymbol type) {
        this.type = type;
    }

    public TypeSymbol getType() {
        if (type == null) {
            return null;
        }
        return switch (type.name) {
            case "Int" -> TypeSymbol.INT;
            case "String" -> TypeSymbol.STRING;
            case "Bool" -> TypeSymbol.BOOL;
            default -> type;
        };

    }
}
