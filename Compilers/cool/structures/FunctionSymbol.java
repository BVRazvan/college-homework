package cool.structures;

import cool.parser.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FunctionSymbol extends IdSymbol implements Scope {
    protected Scope parent;
    protected Map<String, Symbol> symbols = new LinkedHashMap<>();

    public FunctionSymbol(Scope parent, String name) {
        super(name);
        this.parent = parent;
        this.isGlobal = true;
    }

    @Override
    public TypeSymbol getType() {
        return super.getType();
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

    public Map<String, Symbol> getSymbols() {
        return symbols;
    }

    public String getSufix() {
        return "_function";
    }
}
