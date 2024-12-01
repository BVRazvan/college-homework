package cool.structures;


import cool.parser.*;

import java.util.HashMap;
import java.util.Map;

public class ClassDefinitionPassVisitor implements ASTVisitor<Void> {
    Scope currentScope = null;
    public Map<String, String> inheritanceChain = new HashMap<>();
    public Map<String, Integer> classHeight = new HashMap<>();

    @Override
    public Void visit(ClassDef classDef) {
        Type type = classDef.getType();
        classDef.setScope(currentScope);
        if (SymbolTable.globals.lookup(type.getToken().getText()) != null) {
            SymbolTable.error(classDef.parserContext, classDef.getType().getToken(), "Class " + type.getToken().getText() + " is redefined", classDef.getType().getToken().getCharPositionInLine());
            classDef.hasError = true;
            return null;
        }
        Symbol symbol = new ClassSymbol(currentScope, classDef.getType().getToken().getText());
        if (!currentScope.add(symbol)) {
            SymbolTable.error(classDef.parserContext, type.getToken(), "Class " + type.getToken().getText() + " is redefined", classDef.getType().getToken().getCharPositionInLine());
            classDef.hasError = true;
            return null;
        }
        classDef.setScope(currentScope);
        if (type.getToken().getText().equals("SELF_TYPE")) {
            SymbolTable.error(classDef.parserContext, type.getToken(), "Class has illegal name SELF_TYPE", classDef.getType().getToken().getCharPositionInLine());
            classDef.hasError = true;
            return null;
        }
        Type inheritedType = classDef.getInheritedType();
        if (inheritedType == null) {
            classDef.hasError = true;
            inheritanceChain.put(classDef.getType().getToken().getText(), TypeSymbol.OBJECT.name);
            return null;
        }
        if (inheritedType.getToken() == null) {
            inheritanceChain.put(classDef.getType().getToken().getText(), TypeSymbol.OBJECT.name);
            classDef.hasError = true;
            return null;
        }
        String typeName = inheritedType.getToken().getText();

        if (typeName.equals("Int") || typeName.equals("Bool") || typeName.equals("String") ||  typeName.equals("SELF_TYPE")) {
            SymbolTable.error(classDef.parserContext, classDef.getType().getToken(), "Class " + type.getToken().getText() + " has illegal parent " + typeName, classDef.getInheritedType().getToken().getCharPositionInLine());
            classDef.hasError = true;
        }
        inheritanceChain.put(classDef.getType().getToken().getText(), classDef.getInheritedType().getToken().getText());
        return null;
    }

    @Override
    public Void visit(Program program) {
        currentScope = new DefaultScope(null);
        currentScope.add(TypeSymbol.OBJECT);
        currentScope.add(TypeSymbol.INT);
        currentScope.add(TypeSymbol.IO);
        currentScope.add(TypeSymbol.INT);
        currentScope.add(TypeSymbol.BOOL);
        currentScope.add(TypeSymbol.STRING);
        inheritanceChain.put(TypeSymbol.BOOL.name, TypeSymbol.OBJECT.name);
        inheritanceChain.put(TypeSymbol.STRING.name, TypeSymbol.OBJECT.name);
        inheritanceChain.put(TypeSymbol.INT.name, TypeSymbol.OBJECT.name);
        inheritanceChain.put(TypeSymbol.IO.name, TypeSymbol.OBJECT.name);

        for (var stmt : program.getClassDefs()) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Type type) {
        return null;
    }

    @Override
    public Void visit(Id id) {
        return null;
    }

    @Override
    public Void visit(VariableDef variableDef) {
        return null;
    }

    @Override
    public Void visit(FunctionDef functionDef) {
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        return null;
    }

    @Override
    public Void visit(IntLiteral intLiteral) {
        return null;
    }

    @Override
    public Void visit(BoolLiteral boolLiteral) {
        return null;
    }

    @Override
    public Void visit(StringLiteral stringLiteral) {
        return null;
    }

    @Override
    public Void visit(Relational relational) {
        return null;
    }

    @Override
    public Void visit(Arithmetic arithmetic) {
        return null;
    }

    @Override
    public Void visit(Paren paren) {
        return null;
    }

    @Override
    public Void visit(Complement complement) {
        return null;
    }

    @Override
    public Void visit(Not not) {
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        return null;
    }

    @Override
    public Void visit(IsVoid isVoid) {
        return null;
    }

    @Override
    public Void visit(New aNew) {
        return null;
    }

    @Override
    public Void visit(Call call) {
        return null;
    }

    @Override
    public Void visit(CallDispatch callDispatch) {
        return null;
    }

    @Override
    public Void visit(If anIf) {
        return null;
    }

    @Override
    public Void visit(While aWhile) {
        return null;
    }

    @Override
    public Void visit(Case aCase) {
        return null;
    }

    @Override
    public Void visit(Block block) {
        return null;
    }

    @Override
    public Void visit(Let let) {
        return null;
    }
}