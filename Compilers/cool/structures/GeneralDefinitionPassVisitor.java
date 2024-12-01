package cool.structures;

import cool.parser.*;

import java.util.HashMap;
import java.util.Map;

public class GeneralDefinitionPassVisitor implements ASTVisitor<Void> {
    Scope currentScope = null;
    public Map<String, Scope> scopeMap = new HashMap<>();
    private int fpIndex = 0;

    @Override
    public Void visit(ClassDef classDef) {
        var classSymbol = new ClassSymbol(currentScope, classDef.getType().getToken().getText());
        classSymbol.setType(new TypeSymbol(classDef.getType().getToken().getText()));
        classDef.setSymbol(classSymbol);
        currentScope.add(classSymbol);
        currentScope = classSymbol;
        scopeMap.put(classDef.getType().getToken().getText(), currentScope);
        classDef.setScope(currentScope);

        for (var feature : classDef.getFeatures()) {
            feature.accept(this);
        }

        currentScope = currentScope.getParent();

        classDef.getType().accept(this);
        if (classDef.getInheritedType() != null) {
            classDef.getInheritedType().accept(this);
        }
        return null;
    }

    @Override
    public Void visit(Program program) {
        var classSymbol = new ClassSymbol(null, TypeSymbol.OBJECT.name);
        currentScope = classSymbol;
        currentScope.add(classSymbol);
        classSymbol.setType(TypeSymbol.OBJECT);
        currentScope = classSymbol;
        scopeMap.put(TypeSymbol.OBJECT.name, currentScope);
        var functionSymbol = new FunctionSymbol(currentScope, "abort");
        functionSymbol.setType(TypeSymbol.OBJECT);
        currentScope.add(functionSymbol);
        functionSymbol = new FunctionSymbol(currentScope, "type_name");
        functionSymbol.setType(TypeSymbol.STRING);
        currentScope.add(functionSymbol);
        functionSymbol = new FunctionSymbol(currentScope, "copy");
        functionSymbol.setType(new TypeSymbol("SELF_TYPE"));
        functionSymbol.getType().isSelfType = true;
        currentScope.add(functionSymbol);
        classSymbol = new ClassSymbol(currentScope, TypeSymbol.IO.name);
        currentScope.add(classSymbol);
        currentScope = classSymbol;
        classSymbol.setType(TypeSymbol.IO);
        currentScope = classSymbol;
        scopeMap.put(TypeSymbol.IO.name, currentScope);
        functionSymbol = new FunctionSymbol(currentScope, "out_string");
        currentScope.add(functionSymbol);
        currentScope = functionSymbol;
        functionSymbol.setType(new TypeSymbol("SELF_TYPE"));
        functionSymbol.getType().isSelfType = true;
        var fieldSymbol = new FieldSymbol("x");
        fieldSymbol.setType(TypeSymbol.STRING);
        currentScope.add(fieldSymbol);
        currentScope = currentScope.getParent();
        functionSymbol = new FunctionSymbol(currentScope, "out_int");
        currentScope.add(functionSymbol);
        currentScope = functionSymbol;
        fieldSymbol = new FieldSymbol("x");
        fieldSymbol.setType(TypeSymbol.INT);
        functionSymbol.setType(new TypeSymbol("SELF_TYPE"));
        functionSymbol.getType().isSelfType = true;
        currentScope.add(fieldSymbol);
        currentScope = currentScope.getParent();
        functionSymbol = new FunctionSymbol(currentScope, "in_string");
        currentScope.add(functionSymbol);
        functionSymbol.setType(TypeSymbol.STRING);
        functionSymbol = new FunctionSymbol(currentScope, "in_int");
        currentScope.add(functionSymbol);
        functionSymbol.setType(TypeSymbol.INT);
        currentScope = currentScope.getParent();
        classSymbol = new ClassSymbol(currentScope, TypeSymbol.INT.name);
        currentScope.add(classSymbol);
        currentScope = classSymbol;
        classSymbol.setType(TypeSymbol.INT);
        scopeMap.put(TypeSymbol.INT.name, currentScope);
        currentScope = currentScope.getParent();
        classSymbol = new ClassSymbol(currentScope, TypeSymbol.STRING.name);
        currentScope.add(classSymbol);
        currentScope = classSymbol;
        scopeMap.put(TypeSymbol.STRING.name, currentScope);
        functionSymbol = new FunctionSymbol(currentScope, "length");
        currentScope.add(functionSymbol);
        functionSymbol.setType(TypeSymbol.INT);
        functionSymbol = new FunctionSymbol(currentScope, "concat");
        currentScope.add(functionSymbol);
        functionSymbol.setType(TypeSymbol.STRING);
        currentScope = functionSymbol;
        fieldSymbol = new FieldSymbol("s");
        fieldSymbol.setType(TypeSymbol.STRING);
        currentScope.add(fieldSymbol);
        currentScope = currentScope.getParent();
        functionSymbol = new FunctionSymbol(currentScope, "substr");
        currentScope.add(functionSymbol);
        functionSymbol.setType(TypeSymbol.STRING);
        currentScope = functionSymbol;
        fieldSymbol = new FieldSymbol("i");
        fieldSymbol.setType(TypeSymbol.INT);
        currentScope.add(fieldSymbol);
        fieldSymbol = new FieldSymbol("l");
        fieldSymbol.setType(TypeSymbol.INT);
        currentScope.add(fieldSymbol);
        currentScope = currentScope.getParent();
        currentScope = currentScope.getParent();
        classSymbol = new ClassSymbol(currentScope, TypeSymbol.BOOL.name);
        currentScope.add(classSymbol);
        currentScope = classSymbol;
        classSymbol.setType(TypeSymbol.BOOL);
        scopeMap.put(TypeSymbol.BOOL.name, currentScope);
        currentScope = currentScope.getParent();
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
        id.setScope(currentScope);
        var symbol = id.getSymbol();
        if (symbol == null) {
            return null;
        }
        if (currentScope instanceof LetScope) {
            if (id.getToken().getText().equals("self")) {
                SymbolTable.error(id.parserContext, id.getToken(), "Let variable has illegal name self", id.getToken().getCharPositionInLine());
                id.hasError = true;
            }
        }

        if (currentScope instanceof ClassSymbol) {
            if (symbol.getSufix().equals("_attribute")) {
                if (id.getToken().getText().equals("self")) {
                    SymbolTable.error(id.parserContext, id.getToken(), "Class " + ((ClassSymbol) currentScope).name + " has attribute with illegal name " + id.getToken().getText(), id.getToken().getCharPositionInLine());
                    id.hasError = true;
                    return null;
                }
                if (!currentScope.add(symbol)) {
                    SymbolTable.error(id.parserContext, id.getToken(), "Class " + ((ClassSymbol)currentScope).name + " redefines attribute " + id.getToken().getText(), id.getToken().getCharPositionInLine());
                    id.hasError = true;
                    return null;
                }
            } else if (symbol.getSufix().equals("_function")) {
                if (!currentScope.add(symbol)) {
                    SymbolTable.error(id.parserContext, id.getToken(), "Class " + ((ClassSymbol)currentScope).name + " redefines method " + id.getToken().getText(), id.getToken().getCharPositionInLine());
                    id.hasError = true;
                    return null;
                }
            }
        }

        if (currentScope instanceof FunctionSymbol) {
            if (id.getToken().getText().equals("self")) {
                SymbolTable.error(id.parserContext, id.getToken(), "Method " + ((FunctionSymbol)currentScope).name + " of class " + ((ClassSymbol)currentScope.getParent()).name + " has formal parameter with illegal name self", id.getToken().getCharPositionInLine());
                id.hasError = true;
                return null;
            }
            if (!currentScope.add(symbol)) {
                SymbolTable.error(id.parserContext, id.getToken(), "Method " + ((FunctionSymbol)currentScope).name + " of class " + ((ClassSymbol)currentScope.getParent()).name + " redefines formal parameter " + id.getToken().getText(), id.getToken().getCharPositionInLine());
                id.hasError = true;
                return null;
            }
        }
        if (currentScope instanceof CaseScope) {
            if (id.getToken().getText().equals("self")) {
                SymbolTable.error(id.parserContext, id.getToken(), "Case variable has illegal name self", id.getToken().getCharPositionInLine());
                id.hasError = true;
            }
            currentScope.add(symbol);
        }
        if (currentScope instanceof BodyScope) {
        }

        return null;
    }

    @Override
    public Void visit(VariableDef variableDef) {
        Id id = variableDef.getId();
        var symbol = new FieldSymbol(id.getToken().getText());
        id.setSymbol(symbol);
        id.accept(this);

        variableDef.getType().accept(this);
        if (variableDef.getExpression() != null) {
            variableDef.getExpression().accept(this);
        }

        symbol.setType(new TypeSymbol(variableDef.getType().getToken().getText()));

        return null;
    }

    @Override
    public Void visit(FunctionDef functionDef) {
        var id = functionDef.getFunctionId();
        var type = functionDef.getType();
        functionDef.setScope(currentScope);
        var functionSymbol = new FunctionSymbol(currentScope, id.getToken().getText());
        id.setSymbol(functionSymbol);
        if (type.getToken().getText().equals("SELF_TYPE")) {
            Scope scope = currentScope;
            while (!(scope instanceof ClassSymbol)) {
                scope = scope.getParent();
            }
            id.getSymbol().setType(new TypeSymbol(((ClassSymbol) scope).name));
            id.getSymbol().getType().isSelfType = true;
        } else {
            id.getSymbol().setType(new TypeSymbol(type.getToken().getText()));
        }
        id.accept(this);
        currentScope = functionSymbol;

        for (var formal : functionDef.getFormals()) {
            formal.accept(this);
            ((FieldSymbol)formal.getId().getSymbol()).setFieldIndex(functionDef.getFormals().indexOf(formal));
        }

        currentScope = new BodyScope(currentScope);
        functionDef.getBody().accept(this);
        currentScope = currentScope.getParent();
        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        var id = formal.getId();
        var type = formal.getType();
        var symbol = new FieldSymbol(id.getToken().getText());
        id.setSymbol(symbol);
        id.getSymbol().setFormal(true);

        id.accept(this);
        type.accept(this);

        currentScope.add(id.getSymbol());

        if (type.getToken().getText().equals("SELF_TYPE")) {
            SymbolTable.error(formal.parserContext, type.getToken(), "Method " + ((FunctionSymbol)currentScope).name + " of class " + ((ClassSymbol)currentScope.getParent()).name + " has formal parameter " + id.getToken().getText() + " with illegal type SELF_TYPE", type.getToken().getCharPositionInLine());
            id.hasError = true;
            return null;
        }
        symbol.setType(new TypeSymbol(type.getToken().getText()));
        return null;
    }

    @Override
    public Void visit(IntLiteral intLiteral) {
        intLiteral.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(BoolLiteral boolLiteral) {
        boolLiteral.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(StringLiteral stringLiteral) {
        stringLiteral.setScope(currentScope);
        return null;
    }

    @Override
    public Void visit(Relational relational) {
        var left = relational.getLeft();
        var right = relational.getRight();

        if (left instanceof Id idLeft) {
            var symbol = new FieldSymbol(idLeft.getToken().getText());
            idLeft.setSymbol(symbol);
        }
        if (right instanceof Id idRight) {
            var symbol = new FieldSymbol(idRight.getToken().getText());
            idRight.setSymbol(symbol);
        }

        left.accept(this);
        right.accept(this);

        return null;
    }

    @Override
    public Void visit(Arithmetic arithmetic) {
        var left = arithmetic.getLeft();
        var right = arithmetic.getRight();

        left.accept(this);
        right.accept(this);

        return null;
    }

    @Override
    public Void visit(Paren paren) {
        paren.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visit(Complement complement) {
        complement.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visit(Not not) {
        not.getExpression().accept(this);
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        Id id = assign.getId();
        Expression expression = assign.getExpression();
        id.accept(this);
        assign.getExpression().accept(this);

        if (id.getToken().getText().equals("self")) {
            SymbolTable.error(id.parserContext, id.getToken(), "Cannot assign to self", id.getToken().getCharPositionInLine());
            id.hasError = true;
        }

        return null;
    }

    @Override
    public Void visit(IsVoid isVoid) {
        var expression = isVoid.getExpression();
        expression.accept(this);

        return null;
    }

    @Override
    public Void visit(New aNew) {
        aNew.setScope(currentScope);
        aNew.getType().accept(this);
        return null;
    }

    @Override
    public Void visit(Call call) {
        var id = call.getId();
        var parameters = call.getParameters();

        id.accept(this);
        for (var parameter : parameters) {
            parameter.accept(this);
        }

        return null;
    }

    @Override
    public Void visit(CallDispatch callDispatch) {
        var id = callDispatch.getId();
        var e = callDispatch.getE();
        var exprs = callDispatch.getExprs();
        var type = callDispatch.getType();
        if (type != null && type.getToken() != null) {
            if (type.getToken().getText().equals("SELF_TYPE")) {
                SymbolTable.error(callDispatch.parserContext, type.getToken(), "Type of static dispatch cannot be SELF_TYPE", type.getToken().getCharPositionInLine());
                id.hasError = true;
                return null;
            }
            type.accept(this);
        }
        e.accept(this);
        id.accept(this);
        for (var expr : exprs) {
            expr.accept(this);
        }

        if (e.getToken().getText().equals("self")) {
            visit(new Call(id, exprs, null, callDispatch.parserContext));
        }
        return null;
    }

    @Override
    public Void visit(If anIf) {
        anIf.getIfBranch().accept(this);
        anIf.getThenBranch().accept(this);
        anIf.getElseBranch().accept(this);

        return null;
    }

    @Override
    public Void visit(While aWhile) {
        currentScope = new BodyScope(currentScope);
        aWhile.getCondition().accept(this);
        aWhile.getBody().accept(this);

        currentScope = currentScope.getParent();
        return null;
    }

    @Override
    public Void visit(Case aCase) {
        var ids = aCase.getIds();
        var types = aCase.getTypes();
        var expressions = aCase.getExpressions();

        aCase.getExpression().accept(this);

        CaseScope caseScope = new CaseScope(currentScope);
        currentScope = caseScope;

        for (int i = 0; i < ids.size(); i++) {
            var id = ids.get(i);
            var type = types.get(i);
            var expression = expressions.get(i);

            var symbol = new FieldSymbol(id.getToken().getText());
            id.setSymbol(symbol);

            if (type.getToken().getText().equals("SELF_TYPE")) {
                SymbolTable.error(aCase.parserContext, type.getToken(), "Case variable " + id.getToken().getText() + " has illegal type SELF_TYPE", type.getToken().getCharPositionInLine());
                id.hasError = true;
            }

            id.accept(this);
            type.accept(this);
            expression.accept(this);

            id.getSymbol().setType(new TypeSymbol(type.getToken().getText()));
        }

        currentScope = caseScope.getParent();

        return null;
    }

    @Override
    public Void visit(Block block) {
        var expressions = block.getExpressions();

        for (var expression : expressions) {
            expression.accept(this);
        }

        return null;
    }

    @Override
    public Void visit(Let let) {
        var ids = let.getIds();
        var types = let.getTypes();
        var expressions = let.getExpressions();
        var body = let.getBody();

        for (int i = 0; i < ids.size(); i++) {
            currentScope = new LetScope(currentScope);

            var id = ids.get(i);
            var type = types.get(i);
            var expression = expressions.get(i);

            var symbol = new FieldSymbol(id.getToken().getText());
            symbol.setType(new TypeSymbol(type.getToken().getText()));
            id.setSymbol(symbol);
            id.getSymbol().setStackAdded(true);
            ((FieldSymbol)id.getSymbol()).setFieldIndex(fpIndex++);

            id.accept(this);
            type.accept(this);

            if (expression != null) {
                expression.accept(this);
            }
        }

        currentScope = new DefaultScope(currentScope);
        body.accept(this);

        do {
            currentScope = currentScope.getParent();
        } while (currentScope instanceof LetScope);

        fpIndex -= ids.size();
        return null;
    }
}