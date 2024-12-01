package cool.structures;

import java.util.*;

public class BlockScope implements Scope {

    private Map<String, Symbol> symbols = new LinkedHashMap<>();

    private Scope parent;

    public BlockScope(Scope parent) {
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol sym) {
        var key = sym.getName() + sym.getSufix();
        if (symbols.containsKey(key))
            return false;

        symbols.put(key, sym);

        return true;
    }

    @Override
    public Symbol lookup(String name) {
        var sym = symbols.get(name);

        if (sym != null)
            return sym;

        if (parent != null)
            return parent.lookup(name);

        return null;
    }

    @Override
    public Scope getParent() {
        return parent;
    }

    @Override
    public void setParent(Scope scope) {
        this.parent = scope;
    }

    @Override
    public String toString() {
        return symbols.values().toString();
    }

}
