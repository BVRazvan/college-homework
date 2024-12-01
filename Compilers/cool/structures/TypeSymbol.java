package cool.structures;

public class TypeSymbol extends Symbol {
    public boolean isSelfType = false;
    public TypeSymbol(String name) {
        super(name);
    }
    // Symboluri aferente tipurilor, definite global
    public static final TypeSymbol INT   = new TypeSymbol("Int");
    public static final TypeSymbol STRING = new TypeSymbol("String");
    public static final TypeSymbol BOOL  = new TypeSymbol("Bool");
    public static final TypeSymbol OBJECT  = new TypeSymbol("Object");
    public static final TypeSymbol IO = new TypeSymbol("IO");
}