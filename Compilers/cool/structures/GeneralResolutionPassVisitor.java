package cool.structures;


import cool.parser.*;
import org.antlr.v4.runtime.Token;

import java.util.*;
import java.util.List;

public class GeneralResolutionPassVisitor implements ASTVisitor<TypeSymbol> {
    public Map<String, String> inheritanceChain;
    public Map<String, Scope> scopeMap;
    public Map<String, Integer> classHeight;
    public Map<String, List<String>> classFunctions = new HashMap<>();
    public Map<String, List<String>> classAttrs = new HashMap<>();
    public Map<String, List<String>> classAttrsNames = new HashMap<>();

    public GeneralResolutionPassVisitor(Map<String, String> inheritanceChain, Map<String, Scope> scopeMap, Map<String, Integer> classHeight) {
        this.inheritanceChain = inheritanceChain;
        this.scopeMap = scopeMap;
        this.classHeight = classHeight;
    }
    @Override
    public TypeSymbol visit(ClassDef classDef) {
        var inheritedType = classDef.getInheritedType();
        if (inheritedType != null) {
            var token = inheritedType.getToken();
            if (token != null) {
                classDef.getSymbol().setParent(scopeMap.get(token.getText()));
            } else {
                classDef.getSymbol().setParent(scopeMap.get(TypeSymbol.OBJECT.name));
            }
        } else {
            classDef.getSymbol().setParent(scopeMap.get(TypeSymbol.OBJECT.name));
        }

        for (var feature : classDef.getFeatures()) {
            feature.accept(this);
        }

        return classDef.getSymbol().getType();
    }

    @Override
    public TypeSymbol visit(Program program) {
        for (var stmt : program.getClassDefs()) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public TypeSymbol visit(Type type) {
        if (type.getToken().getText().equals("Int")) {
            return TypeSymbol.INT;
        } else if (type.getToken().getText().equals("String")) {
            return TypeSymbol.STRING;
        } else if (type.getToken().getText().equals("Bool")) {
            return TypeSymbol.BOOL;
        }
        return null;
    }

    @Override
    public TypeSymbol visit(Id id) {
        Scope scope = id.getScope();
        var symbol = id.getSymbol();
        if (id.hasError) {
            return null;
        }
        if (id.getToken().getText().equals("self")) {
            while (!(scope instanceof ClassSymbol)) {
                scope = scope.getParent();
            }
            ((ClassSymbol) scope).getType().isSelfType = true;
            return ((ClassSymbol) scope).getType();
        }
        if (scope instanceof LetScope) {
            if (symbol == null) {
                symbol = (IdSymbol)scope.lookup(id.getToken().getText() + "_attribute");
                if (symbol == null) {
                    SymbolTable.error(id.parserContext, id.getToken(), "Undefined identifier " + id.getToken().getText(), id.getToken().getCharPositionInLine());
                    id.hasError = true;
                    return null;
                } else {
                    id.setSymbol(symbol);
                }
            }
        } else if (scope instanceof ClassSymbol) {
            if (symbol == null) {
                symbol = (IdSymbol)scope.lookup(id.getToken().getText() + "_attribute");
                if (symbol == null) {
                    SymbolTable.error(id.parserContext, id.getToken(), "Undefined identifier " + id.getToken().getText(), id.getToken().getCharPositionInLine());
                    id.hasError = true;
                    return null;
                } else {
                    id.setSymbol(symbol);
                }
            }
        } else if (scope instanceof BodyScope) {
            if (symbol == null) {
                symbol = (IdSymbol) scope.lookup(id.getToken().getText() + "_attribute");
                if (symbol == null) {
                    SymbolTable.error(id.parserContext, id.getToken(), "Undefined identifier " + id.getToken().getText(), id.getToken().getCharPositionInLine());
                    id.hasError = true;
                    return null;
                } else {
                    id.setSymbol(symbol);
                }
            }
        } else if (scope instanceof CaseScope) {
            if (symbol == null) {
                symbol = (IdSymbol) scope.lookup(id.getToken().getText() + "_attribute");
                id.setSymbol(symbol);
            }
        } else if (scope instanceof DefaultScope) {
            if (symbol == null) {
                symbol = (IdSymbol) scope.lookup(id.getToken().getText() + "_attribute");
                id.setSymbol(symbol);
            }
        }
        if (symbol != null) {
            return symbol.getType();
        }
        return null;
    }

    @Override
    public TypeSymbol visit(VariableDef variableDef) {
        if (variableDef.hasError) {
            return null;
        }

        Id id = variableDef.getId();
        if (id.hasError) {
            return null;
        }

        ClassSymbol currentScope = (ClassSymbol)id.getScope();
        Token idToken = id.getToken();
        String idTokenText = idToken.getText();
        if (currentScope.getParent().lookup(idTokenText + id.getSymbol().getSufix()) != null) {
            SymbolTable.error(variableDef.parserContext, idToken, "Class " + currentScope.name + " redefines inherited attribute " + idTokenText, idToken.getCharPositionInLine());
        }

        Token typeToken = variableDef.getType().getToken();
        if (typeToken.getText().equals("SELF_TYPE")) {
            Scope scope = id.getScope();
            while (!(scope instanceof ClassSymbol)) {
                scope = scope.getParent();
            }
            id.getSymbol().setType(new TypeSymbol(((ClassSymbol) scope).name));
            id.getSymbol().getType().isSelfType = true;
        } else if (currentScope.lookup(typeToken.getText()) == null) {
            SymbolTable.error(variableDef.parserContext, idToken, "Class " + currentScope.name + " has attribute " + idTokenText + " with undefined type " + typeToken.getText(), typeToken.getCharPositionInLine());
        }

        var parent = ((ClassSymbol)variableDef.getId().getScope()).name;
        if (!classAttrs.containsKey(parent)) {
            classAttrs.put(parent, new ArrayList<>());
        }
        var listAttrs = classAttrs.get(parent);
        if (variableDef.getExpression() == null) {
            if (variableDef.getType().getToken().getText().equals("Int")) {
                listAttrs.add("0");
            } else if (variableDef.getType().getToken().getText().equals("Bool")) {
                listAttrs.add("false");
            } else if (variableDef.getType().getToken().getText().equals("String")) {
                listAttrs.add("");
            } else {
                listAttrs.add("null");
            }
        } else {
            listAttrs.add(variableDef.getExpression().getToken().getText());
        }

        if (!classAttrsNames.containsKey(parent)) {
            classAttrsNames.put(parent, new ArrayList<>());
        }
        var listAttrsNames = classAttrsNames.get(parent);
        listAttrsNames.add(variableDef.getId().getToken().getText());

        var expression = variableDef.getExpression();
        if (expression != null) {
            var expressionType = expression.accept(this);
            if (expressionType == null) {
                return null;
            }
            String idTypeName = id.getSymbol().getType().name;
            String expressionTypeName = expressionType.name;
            if (idTypeName.equals(expressionTypeName)) {
                return null;
            }
            String actType = expressionType.name;
            boolean found = false;
            while (inheritanceChain.containsKey(actType)) {
                var value = inheritanceChain.get(actType);
                if (value.equals(idTypeName)) {
                    found = true;
                    break;
                }
                actType = value;
            }
            if (!found) {
                int offset = 0;
                if (expression instanceof CallDispatch) {
                    offset = expression.parserContext.getStart().getCharPositionInLine();
                } else {
                    offset = expression.getToken().getCharPositionInLine();
                }
                SymbolTable.error(id.parserContext, id.getToken(), "Type " + (expressionType.isSelfType ? "SELF_TYPE" : expressionType.name) + " of initialization expression of attribute " + id.getToken().getText() + " is incompatible with declared type " + variableDef.getType().getToken().getText(), offset);
            }
        }

        return id.getSymbol().getType();
    }

    @Override
    public TypeSymbol visit(FunctionDef functionDef) {
        Id functionId = functionDef.getFunctionId();
        if (functionId.hasError) {
            return null;
        }
        var parent = ((ClassSymbol)functionId.getScope()).name;
        if (!classFunctions.containsKey(parent)) {
            classFunctions.put(parent, new ArrayList<>());
        }
        var listFunction = classFunctions.get(parent);
        listFunction.add(parent + "." + functionDef.getFunctionId().getToken().getText());

        var formals = functionDef.getFormals();
        for (var formal : functionDef.getFormals()) {
            formal.accept(this);
        }

        Token idToken = functionId.getToken();
        IdSymbol functionIdSymbol = functionId.getSymbol();
        var symbol = functionId.getScope().getParent().lookup(idToken.getText() + functionIdSymbol.getSufix());
        if (symbol != null) {
            var parentScope = (ClassSymbol)functionId.getScope();
            FunctionSymbol functionSymbolCast = (FunctionSymbol) symbol;
            var symbolsMap = functionSymbolCast.getSymbols();
            if (symbolsMap.size() != formals.size()) {
                SymbolTable.error(functionDef.parserContext, idToken, "Class " + parentScope.name + " overrides method " + idToken.getText() + " with different number of formal parameters", idToken.getCharPositionInLine());
                return functionIdSymbol.getType();
            }
            var symbolFunction = functionDef.getFunctionId().getSymbol().getType();
            if (!symbolFunction.name.equals(((IdSymbol) symbol).getType().name) && !(symbolFunction.isSelfType | ((FunctionSymbol) symbol).getType().isSelfType)) {
                SymbolTable.error(functionDef.parserContext, idToken, "Class " + parentScope.name + " overrides method " + idToken.getText() + " but changes return type from " + functionSymbolCast.getType().name + " to " + functionDef.getType().getToken().getText(), functionDef.getType().getToken().getCharPositionInLine());
                return functionIdSymbol.getType();
            }


            Iterator<Map.Entry<String, Symbol>> symbolIterator = symbolsMap.entrySet().iterator();
            for (Formal formal : formals) {
                var entry = symbolIterator.next();
                Id id = formal.getId();
                Type type = formal.getType();
                var typeToken = type.getToken();

                if (id.hasError) {
                    continue;
                }
                if (!typeToken.getText().equals(((IdSymbol) entry.getValue()).getType().name)) {
                    SymbolTable.error(type.parserContext, formal.getToken(), "Class " + parentScope.name + " overrides method f but changes type of formal parameter " + formal.getId().getToken().getText() + " from " + ((IdSymbol) entry.getValue()).getType().name + " to " + typeToken.getText(), typeToken.getCharPositionInLine());
                    return functionIdSymbol.getType();
                }
            }
        }
        TypeSymbol retSymbol = functionDef.getBody().accept(this);
        if (retSymbol == null) {
            return null;
        }

        String idTypeName = functionId.getSymbol().getType().name;
        String expressionTypeName = retSymbol.name;
        if (expressionTypeName.equals("SELF_TYPE")) {
            expressionTypeName = functionId.getSymbol().getType().name;
        }
        if (idTypeName.equals(expressionTypeName)) {
            if (functionId.getSymbol().getType().isSelfType && !retSymbol.isSelfType) {
                SymbolTable.error(functionDef.getBody().parserContext, functionDef.getBody().getToken(), "Type " + (retSymbol.isSelfType ? "SELF_TYPE" : expressionTypeName) + " of the body of method " + functionId.getToken().getText() + " is incompatible with declared return type " + (functionId.getSymbol().getType().isSelfType ? "SELF_TYPE" : functionId.getSymbol().getType().name), functionDef.getBody().getToken().getCharPositionInLine());
                return retSymbol;
            }
            return retSymbol;
        }

        String actType = expressionTypeName;
        boolean found = false;
        while (inheritanceChain.containsKey(actType)) {
            var value = inheritanceChain.get(actType);
            if (value.equals(idTypeName)) {
                found = true;
                break;
            }
            actType = value;
        }
        if (!found) {
            SymbolTable.error(functionDef.getBody().parserContext, functionDef.getBody().getToken(), "Type " + expressionTypeName + " of the body of method " + functionId.getToken().getText() + " is incompatible with declared return type " + (functionId.getSymbol().getType().isSelfType ? "SELF_TYPE" : functionId.getSymbol().getType().name), functionDef.getBody().getToken().getCharPositionInLine());
            return null;
        }
        return retSymbol;
    }

    @Override
    public TypeSymbol visit(Formal formal) {
        var id = formal.getId();
        if (id.hasError) {
            return null;
        }

        var type = formal.getType();
        var currentScope = id.getScope();

        Token typeToken = type.getToken();
        if (currentScope.lookup(typeToken.getText()) == null) {
            SymbolTable.error(formal.parserContext, id.getToken(), "Method " + ((FunctionSymbol)currentScope).name + " of class " + ((ClassSymbol)currentScope.getParent()).name + " has formal parameter " + id.getToken().getText() + " with undefined type " + typeToken.getText(), typeToken.getCharPositionInLine());
            return null;
        }

        return id.getSymbol().getType();
    }

    @Override
    public TypeSymbol visit(IntLiteral intLiteral) {
        return TypeSymbol.INT;
    }

    @Override
    public TypeSymbol visit(BoolLiteral boolLiteral) {
        return TypeSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(StringLiteral stringLiteral) {
        return TypeSymbol.STRING;
    }

    @Override
    public TypeSymbol visit(Relational relational) {
        var left = relational.getLeft();
        var right = relational.getRight();

        var leftResult = left.accept(this);
        var rightResult = right.accept(this);

        if (left instanceof Id idLeft) {
            idLeft.getSymbol().setType(leftResult);
        }
        if (right instanceof Id idRight) {
            idRight.getSymbol().setType(rightResult);
        }


        if (relational.getToken().getText().equals("<") || relational.getToken().getText().equals("<=")) {
            if (leftResult != TypeSymbol.INT && leftResult != null) {
                SymbolTable.error(left.parserContext, left.getToken(), "Operand of " + relational.getToken().getText() + " has type " + leftResult.name + " instead of Int", left.getToken().getCharPositionInLine());
            }
            if (rightResult != TypeSymbol.INT && rightResult != null) {
                SymbolTable.error(right.parserContext, right.getToken(), "Operand of " + relational.getToken().getText() + " has type " + rightResult.name + " instead of Int", right.getToken().getCharPositionInLine());
            }

            return TypeSymbol.BOOL;
        }

        if (leftResult != null && rightResult != null) {
            if ((leftResult == TypeSymbol.INT || leftResult == TypeSymbol.BOOL || leftResult == TypeSymbol.STRING) && leftResult != rightResult) {
                SymbolTable.error(left.parserContext, left.getToken(), "Cannot compare " + leftResult.name + " with " + rightResult.name, relational.getToken().getCharPositionInLine());
            } else if ((rightResult == TypeSymbol.INT || rightResult == TypeSymbol.BOOL || rightResult == TypeSymbol.STRING) && rightResult != leftResult) {
                SymbolTable.error(right.parserContext, right.getToken(), "Cannot compare " + leftResult.name + " with " + rightResult.name, relational.getToken().getCharPositionInLine());
            }
        }

        return TypeSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(Arithmetic arithmetic) {
        var left = arithmetic.getLeft();
        var right = arithmetic.getRight();

        var leftResult = left.accept(this);
        var rightResult = right.accept(this);

        if (leftResult != TypeSymbol.INT && leftResult != null) {
            SymbolTable.error(left.parserContext, left.getToken(), "Operand of " + arithmetic.getToken().getText() + " has type " + (leftResult.isSelfType ? "SELF_TYPE" : leftResult.name) + " instead of Int", left.getToken().getCharPositionInLine());
        }
        if (rightResult != TypeSymbol.INT && rightResult != null) {
            SymbolTable.error(right.parserContext, right.getToken(), "Operand of " + arithmetic.getToken().getText() + " has type " + (rightResult.isSelfType ? "SELF_TYPE" : rightResult.name) + " instead of Int", right.getToken().getCharPositionInLine());
        }
        return TypeSymbol.INT;
    }

    @Override
    public TypeSymbol visit(Paren paren) {
        return paren.getExpression().accept(this);
    }

    @Override
    public TypeSymbol visit(Complement complement) {
        var result = complement.getExpression().accept(this);
        if (result != TypeSymbol.INT) {
            SymbolTable.error(complement.parserContext, complement.getToken(), "Operand of " + complement.getToken().getText() + " has type " + result.name + " instead of Int", complement.getExpression().getToken().getCharPositionInLine());
        }

        return TypeSymbol.INT;
    }

    @Override
    public TypeSymbol visit(Not not) {
        var result = not.getExpression().accept(this);

        if (result != TypeSymbol.BOOL) {
            SymbolTable.error(not.parserContext, not.getToken(), "Operand of " + not.getToken().getText() + " has type " + result.name + " instead of Bool", not.getExpression().getToken().getCharPositionInLine());
        }

        return TypeSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(Assign assign) {
        Id id = assign.getId();
        Expression expression = assign.getExpression();
        TypeSymbol idType = id.accept(this);
        TypeSymbol expressionType = assign.getExpression().accept(this);
        if (expressionType == null || idType == null) {
            return null;
        }
        String idTypeName = idType.name;
        String expressionTypeName = expressionType.name;
        if (idTypeName.equals(expressionTypeName)) {
            if (idType.isSelfType && !expressionType.isSelfType) {
                SymbolTable.error(expression.parserContext, expression.getToken(), "Type " + (expressionType.isSelfType ? "SELF_TYPE" : expressionTypeName) + " of assigned expression is incompatible with declared type " + (id.getSymbol().getType().isSelfType ? "SELF_TYPE" : id.getSymbol().getType()) + " of identifier " + id.getToken().getText(), expression.getToken().getCharPositionInLine());
                return expressionType;
            }
            return expressionType;
        }
        String actType = expressionType.name;
        boolean found = false;
        while (inheritanceChain.containsKey(actType)) {
            var value = inheritanceChain.get(actType);
            if (value.equals(idTypeName)) {
                found = true;
                break;
            }
            actType = value;
        }
        if (!found || (idType.isSelfType && !expressionType.isSelfType)) {
            var offset = 0;
            if (expression instanceof CallDispatch) {
                offset = ((CallDispatch)expression).getE().getToken().getCharPositionInLine();
            } else {
                offset = expression.getToken().getCharPositionInLine();
            }
            SymbolTable.error(expression.parserContext, expression.getToken(), "Type " + (expressionType.isSelfType ? "SELF_TYPE" : expressionTypeName) + " of assigned expression is incompatible with declared type " + (id.getSymbol().getType().isSelfType ? "SELF_TYPE" : id.getSymbol().getType()) + " of identifier " + id.getToken().getText(), offset);
        }
        return expressionType;
    }

    @Override
    public TypeSymbol visit(IsVoid isVoid) {
        var returnType = isVoid.getExpression().accept(this);
        return TypeSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(New aNew) {
        Scope scope = aNew.getScope();
        Type type = aNew.getType();
        if (type.getToken().getText().equals("SELF_TYPE")) {
            while (!(scope instanceof ClassSymbol)) {
                scope = scope.getParent();
            }
            var retType = ((ClassSymbol) scope).getType();
            retType.isSelfType = true;
            return retType;
        }

        if (scope.lookup(type.getToken().getText()) == null) {
            SymbolTable.error(aNew.parserContext, aNew.getToken(), "new is used with undefined type " + type.getToken().getText(), aNew.getType().getToken().getCharPositionInLine());
            return null;
        }
        return new TypeSymbol(type.getToken().getText());
    }

    @Override
    public TypeSymbol visit(Call call) {
        var id = call.getId();
        var symbol = (id.getScope().lookup(id.getToken().getText() + "_function"));
        if (symbol == null) {
            SymbolTable.error(id.parserContext, id.getToken(), "Undefined identifier " + id.getToken().getText(), id.getToken().getCharPositionInLine());
            id.hasError = true;
            return null;
        } else {
            id.setSymbol((IdSymbol)symbol);
            if (id.getSymbol().getType().isSelfType) {
                Scope scope = id.getScope();
                while (!(scope instanceof ClassSymbol)) {
                    scope = scope.getParent();
                }
                id.getSymbol().setType(new TypeSymbol(((ClassSymbol) scope).name));
                id.getSymbol().getType().isSelfType = true;
            }
        }
        var parameters = call.getParameters();
        LinkedHashMap<String, Symbol> definesParameters = (LinkedHashMap<String, Symbol>) ((FunctionSymbol)symbol).getSymbols();
        var parameterSymbols = definesParameters.entrySet();
        if (parameterSymbols.size() != parameters.size()) {

            // TO DO
        }

        List<TypeSymbol> types = new ArrayList<>();
        for (var parameter : parameters) {
            types.add(parameter.accept(this));
        }

        Scope classScope = id.getScope();
        while (!(classScope instanceof ClassSymbol)) {
            classScope = classScope.getParent();
        }

        List<Map.Entry<?, ?>> entryList = new ArrayList<>(definesParameters.entrySet());
        for (int i = 0; i < entryList.size(); ++i) {
            var entry = entryList.get(i);
            var parameter = types.get(i);
            var expression = parameters.get(i);

            Symbol symbolParameter = (Symbol) entry.getValue();
            TypeSymbol expressionType = ((IdSymbol)symbolParameter).getType();
            String idTypeName = expressionType.name; // CU CE APELEZ
            String actType = parameter.name; // CUM AM DECLARAT
            boolean found = actType.equals(idTypeName);
            if (found) {
                continue;
            }
            while (inheritanceChain.containsKey(actType)) {
                var value = inheritanceChain.get(actType);
                if (value.equals(idTypeName)) {
                    found = true;
                    break;
                }
                actType = value;
            }
            if (!found) {
                SymbolTable.error(expression.parserContext, expression.getToken(), "In call to method " + id.getToken().getText() + " of class " + ((ClassSymbol) classScope).name + ", actual type " + parameter.name + " of formal parameter " + symbolParameter.name + " is incompatible with declared type " + expressionType.name, expression.getToken().getCharPositionInLine());
                return ((FunctionSymbol) symbol).getType();
            }
        }

        return ((FunctionSymbol) symbol).getType();
    }

    @Override
    public TypeSymbol visit(CallDispatch callDispatch) {
        var id = callDispatch.getId();
        if (id.hasError) {
            return null;
        }
        var e = callDispatch.getE();
        var exprs = callDispatch.getExprs();
        var type = callDispatch.getType();

        if (type != null && type.getToken() != null) {
            if (type.getToken().getText().equals("SELF_TYPE")) {
                SymbolTable.error(callDispatch.parserContext, type.getToken(), "Type of static dispatch cannot be SELF_TYPE", type.getToken().getCharPositionInLine());
                return null;
            }
            type.accept(this);
            var classSymbol = id.getScope().lookup(type.getToken().getText());
            if (classSymbol == null) {
                SymbolTable.error(callDispatch.parserContext, type.getToken(), "Type " + type.getToken().getText() + " of static dispatch is undefined", type.getToken().getCharPositionInLine());
                return null;
            }
            var exprSymbol = e.accept(this);
            if (exprSymbol == null) {
                return null;
            }
            callDispatch.setExprType(exprSymbol);
            if (!exprSymbol.isSelfType && !(LCA(exprSymbol, ((ClassSymbol)classSymbol).getType()).name.equals(((ClassSymbol)classSymbol).getType().name))) {
                SymbolTable.error(type.parserContext, type.getToken(), "Type " + ((ClassSymbol) classSymbol).getType().name + " of static dispatch is not a superclass of type " + exprSymbol.name, type.getToken().getCharPositionInLine());
                return null;
            }

            FunctionSymbol functionSymbol = (FunctionSymbol) ((ClassSymbol) classSymbol).lookup(id.getToken().getText() + "_function");
            if (functionSymbol == null) {
                SymbolTable.error(id.parserContext, id.getToken(), "Undefined method " + id.getToken().getText() + " in class " + ((ClassSymbol)classSymbol).name, id.getToken().getCharPositionInLine());
                return null;
            }

            var symbolsMap = functionSymbol.getSymbols();
            if (symbolsMap.size() != exprs.size()) {
                SymbolTable.error(id.parserContext, id.getToken(), "Method " + id.getToken().getText() + " of class " + classSymbol.name + " is applied to wrong number of parameters", id.getToken().getCharPositionInLine());
                return functionSymbol.getType();
            }

            Iterator<Map.Entry<String, Symbol>> symbolIterator = symbolsMap.entrySet().iterator();
            for (var expr : exprs) {
                var entry = symbolIterator.next();
                TypeSymbol parameterType = expr.accept(this);

                if (id.hasError) {
                    continue;
                }
                if (!parameterType.name.equals(((IdSymbol) entry.getValue()).getType().name)) {
                    SymbolTable.error(type.parserContext, expr.getToken(), "In call to method " + id.getToken().getText() + " of class " + classSymbol.name + ", actual type " + parameterType + " of formal parameter " + entry.getKey() + " is incompatible with declared type " + entry.getValue().name, expr.getToken().getCharPositionInLine());
                    return functionSymbol.getType();
                }
            }

            id.setSymbol(new IdSymbol(functionSymbol.name));
            return functionSymbol.getType();
        }

        if (e.getToken().getText().equals("self")) {
            return visit(new Call(id, exprs, null, callDispatch.parserContext));
        }

        var exprSymbol = e.accept(this);
        if (exprSymbol == null) {
            return null;
        }
        callDispatch.setExprType(exprSymbol);
        var scopeString = exprSymbol.name;
        var exprScope = (ClassSymbol)id.getScope().lookup(scopeString);
        var functionSymbol = exprScope.lookup(id.getToken().getText() + "_function");
        while (inheritanceChain.containsKey(scopeString) && functionSymbol == null) {
            scopeString = inheritanceChain.get(scopeString);
            exprScope = (ClassSymbol) scopeMap.get(scopeString);
            if (exprScope == null) {
                break;
            }
            functionSymbol = exprScope.lookup(id.getToken().getText() + "_function");
        }
        if (functionSymbol == null) {
            SymbolTable.error(id.parserContext, id.getToken(), "Undefined method " + id.getToken().getText() + " in class " + exprSymbol.name, id.getToken().getCharPositionInLine());
            id.hasError = true;
            return null;
        }

        var symbol = functionSymbol;
        var parameters = callDispatch.getExprs();
        LinkedHashMap<String, Symbol> definesParameters = (LinkedHashMap<String, Symbol>) ((FunctionSymbol)symbol).getSymbols();
        var parameterSymbols = definesParameters.entrySet();
        if (parameterSymbols.size() != parameters.size()) {
            SymbolTable.error(id.parserContext, id.getToken(), "Method " + id.getToken().getText() + " of class " + exprSymbol.name + " is applied to wrong number of arguments", id.getToken().getCharPositionInLine());
            return ((FunctionSymbol) symbol).getType();
        }

        List<TypeSymbol> types = new ArrayList<>();
        for (var parameter : parameters) {
            types.add(parameter.accept(this));
        }

        Scope classScope = scopeMap.get(exprSymbol.name);
        while (!(classScope instanceof ClassSymbol)) {
            classScope = classScope.getParent();
        }

        List<Map.Entry<?, ?>> entryList = new ArrayList<>(definesParameters.entrySet());
        for (int i = 0; i < entryList.size(); ++i) {
            var entry = entryList.get(i);
            var parameter = types.get(i);
            var expression = parameters.get(i);

            Symbol symbolParameter = (Symbol) entry.getValue();
            TypeSymbol expressionType = ((IdSymbol)symbolParameter).getType();
            String idTypeName = expressionType.name; // CU CE APELEZ
            String actType = parameter.name; // CUM AM DECLARAT
            boolean found = actType.equals(idTypeName);
            if (found) {
                continue;
            }
            while (inheritanceChain.containsKey(actType)) {
                var value = inheritanceChain.get(actType);
                if (value.equals(idTypeName)) {
                    found = true;
                    break;
                }
                actType = value;
            }
            if (!found) {
                SymbolTable.error(expression.parserContext, expression.getToken(), "In call to method " + id.getToken().getText() + " of class " + ((ClassSymbol) classScope).name + ", actual type " + parameter.name + " of formal parameter " + symbolParameter.name + " is incompatible with declared type " + expressionType.name, expression.getToken().getCharPositionInLine());
                return ((FunctionSymbol) symbol).getType();
            }
        }
        if (((FunctionSymbol)symbol).getType().isSelfType) {
            TypeSymbol retSymbol = ((ClassSymbol) classScope).getType();
            if (e instanceof New) {
                retSymbol = new TypeSymbol(((ClassSymbol) classScope).getType().name);
                return retSymbol;
            }
            retSymbol.isSelfType = true;
            return retSymbol ;
        }

        return ((FunctionSymbol)symbol).getType();
    }

    @Override
    public TypeSymbol visit(If anIf) {
        Expression ifBranch = anIf.getIfBranch();
        TypeSymbol ifBranchType = ifBranch.accept(this);
        Expression thenBranch = anIf.getThenBranch();
        TypeSymbol thenBranchType = thenBranch.accept(this);
        Expression elseBranch = anIf.getElseBranch();
        TypeSymbol elseBranchType = elseBranch.accept(this);
        if (thenBranchType == null || elseBranchType == null) {
            return TypeSymbol.OBJECT;
        }
        if (ifBranchType != TypeSymbol.BOOL) {
            SymbolTable.error(anIf.parserContext, ifBranch.getToken(), "If condition has type " + ifBranchType.name + " instead of Bool", ifBranch.getToken().getCharPositionInLine());
            return TypeSymbol.OBJECT;
        }
        var res = LCA(thenBranchType, elseBranchType);
        res.isSelfType = thenBranchType.isSelfType & elseBranchType.isSelfType;
        return res;
    }

    private TypeSymbol LCA(TypeSymbol thenBranchType, TypeSymbol elseBranchType) {
        var thenName = thenBranchType.name;
        var elseName = elseBranchType.name;
        while (!thenName.equals(elseName)) {
            var thenHeight = getHeight(thenName);
            var elseHeight = getHeight(elseName);
            if (thenHeight >= elseHeight) {
                thenName = inheritanceChain.get(thenName);
            } else {
                elseName = inheritanceChain.get(elseName);
            }
        }
        if (thenName.equals("Object")) {
            return TypeSymbol.OBJECT;
        } else if (thenName.equals("Bool")) {
            return TypeSymbol.BOOL;
        } else if (elseName.equals("Int")) {
            return TypeSymbol.INT;
        } else if (elseName.equals("String")) {
            return TypeSymbol.STRING;
        } else {
            return new TypeSymbol(thenName);
        }
    }

    private Integer getHeight(String typeName) {
        int height = 0;
        while (!typeName.equals(TypeSymbol.OBJECT.name)) {
            ++height;
            typeName = inheritanceChain.get(typeName);
        }
        return height;
    }

    @Override
    public TypeSymbol visit(While aWhile) {
        var typeCondition = aWhile.getCondition().accept(this);
        if (typeCondition != TypeSymbol.BOOL) {
            SymbolTable.error(aWhile.parserContext, aWhile.getToken(), "While condition has type " + typeCondition.name + " instead of Bool", aWhile.getCondition().getToken().getCharPositionInLine());
        }

        aWhile.getBody().accept(this);
        return TypeSymbol.OBJECT;
    }

    @Override
    public TypeSymbol visit(Case aCase) {
        var ids = aCase.getIds();
        var types = aCase.getTypes();
        var expressions = aCase.getExpressions();

        TypeSymbol first = null, second = null;
        for (int i = 0; i < ids.size(); ++i) {
            var id = ids.get(i);
            var type = types.get(i);
            var expression = expressions.get(i);
            var expressionType = expression.accept(this);
            if (id.hasError) {
                continue;
            }

            var symbol = id.getScope().lookup(type.getToken().getText());
            if (symbol == null) {
                SymbolTable.error(aCase.parserContext, id.getToken(), "Case variable " + id.getToken().getText() + " has undefined type " + type.getToken().getText(), type.getToken().getCharPositionInLine());
                continue;
            }

            if (expressionType == null) {
                continue;
            }
            if (first == null) {
                first = expressionType;
            } else {
                second = expressionType;
                first = LCA(first, second);
            }
        }
        return first;
    }

    @Override
    public TypeSymbol visit(Block block) {
        var expressions = block.getExpressions();
        var types = new ArrayList<TypeSymbol>();

        for (Expression expression : expressions) {
            types.add(expression.accept(this));
        }

        return types.get(types.size() - 1);
    }

    @Override
    public TypeSymbol visit(Let let) {
        if (let.hasError) {
            return null;
        }
        var ids = let.getIds();
        var types = let.getTypes();
        var expressions = let.getExpressions();

        for (int i = 0; i < ids.size(); ++i) {
            var id = ids.get(i);
            var type = types.get(i);
            var expression = expressions.get(i);
            TypeSymbol expressionType = null;

            if (expression != null) {
                expressionType = expression.accept(this);
                if (expressionType == null) {
                    return null;
                }
            }

            Token typeToken = type.getToken();
            var symbol = id.getScope().lookup(typeToken.getText());
            if (typeToken.getText().equals("SELF_TYPE")) {
                Scope scope = id.getScope();
                while (!(scope instanceof ClassSymbol)) {
                    scope = scope.getParent();
                }
                id.getSymbol().setType(new TypeSymbol(((ClassSymbol) scope).name));
                id.getSymbol().getType().isSelfType = true;
            } else if (symbol == null) {
                SymbolTable.error(id.parserContext, id.getToken(), "Let variable " + id.getToken().getText() + " has undefined type " + typeToken.getText(), typeToken.getCharPositionInLine());
                continue;
            }

            if (expression != null) {
                String idTypeName = id.getSymbol().getType().name;
                String actType = expressionType.name;
                boolean found = actType.equals(idTypeName);
                while (inheritanceChain.containsKey(actType)) {
                    var value = inheritanceChain.get(actType);
                    if (value.equals(idTypeName)) {
                        found = true;
                        break;
                    }
                    actType = value;
                }
                if (!found) {
                    SymbolTable.error(id.parserContext, id.getToken(), "Type " + (expressionType.isSelfType ? "SELF_TYPE" : expressionType.name)+ " of initialization expression of identifier " + id.getToken().getText() + " is incompatible with declared type " + idTypeName, expression.getToken().getCharPositionInLine());
                }
            }

            id.getScope().add(id.getSymbol());
        }

        return let.getBody().accept(this);
    }
}