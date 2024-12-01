package cool.structures;

import java.util.HashMap;
import java.util.Map;

public class ClassSymbol extends IdSymbol implements Scope {
    protected Scope parent;
    protected Map<String, Symbol> symbols = new HashMap<>();

    public ClassSymbol(Scope parent, String name) {
        super(name);
        this.parent = parent;
        this.isGlobal = true;
    }

    @Override
    public boolean add(Symbol sym) {
        var key = sym.getName() + sym.getSufix();
        if (symbols.containsKey(key)) {
            return false;
        }
        symbols.put(key, sym);

        return true;
    }

    @Override
    public Symbol lookup(String str) {
        var sym = symbols.get(str);

        if (sym != null) {
            return sym;
        }

        if (parent != null) {
            return parent.lookup(str);
        }

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
}
