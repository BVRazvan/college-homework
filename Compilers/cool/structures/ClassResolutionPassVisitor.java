package cool.structures;


import cool.parser.*;

import java.util.HashMap;
import java.util.Map;

public class ClassResolutionPassVisitor implements ASTVisitor<Void> {
    public Map<String, String> inheritanceChain;
    public Map<String, Integer> classHeight;
    public ClassResolutionPassVisitor(Map<String, String> inheritanceChain, Map<String, Integer> classHeight) {
        this.inheritanceChain = inheritanceChain;
        this.classHeight = classHeight;
    }
    @Override
    public Void visit(ClassDef classDef) {
        if (classDef.hasError) {
            return null;
        }
        Type inheritedType = classDef.getInheritedType();
        if (inheritedType == null) {
            return null;
        }
        if (classDef.getScope().lookup(inheritedType.getToken().getText()) == null) {
            SymbolTable.error(classDef.parserContext, classDef.getType().getToken(), "Class " + classDef.getType().getToken().getText() + " has undefined parent " + inheritedType.getToken().getText(), classDef.getInheritedType().getToken().getCharPositionInLine());
        }

        Map<String, Boolean> frequency = new HashMap<>();
        String typeName = classDef.getType().getToken().getText();
        while (inheritanceChain.containsKey(typeName)) {
            frequency.put(typeName, true);
            typeName = inheritanceChain.get(typeName);
            if (typeName.equals(classDef.getType().getToken().getText())) {
                SymbolTable.error(classDef.parserContext, classDef.getType().getToken(), "Inheritance cycle for class " + classDef.getType().getToken().getText(), classDef.getType().getToken().getCharPositionInLine());
                break;
            }
            if (frequency.containsKey(typeName)) {
                break;
            }
        }
        return null;
    }

    @Override
    public Void visit(Program program) {
        inheritanceChain.put(TypeSymbol.STRING.name, TypeSymbol.OBJECT.name);
        inheritanceChain.put(TypeSymbol.INT.name, TypeSymbol.OBJECT.name);
        inheritanceChain.put(TypeSymbol.IO.name, TypeSymbol.OBJECT.name);
        inheritanceChain.put(TypeSymbol.BOOL.name, TypeSymbol.OBJECT.name);
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