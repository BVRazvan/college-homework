package cool.parser;

import org.antlr.v4.runtime.Token;

public class PrintVisitor implements ASTVisitor<Void> {
    int indent = 0;

    @Override
    public Void visit(ClassDef classDef) {
        printIndent("class");
        ++indent;
        classDef.type.accept(this);
        if (classDef.inheritedType != null) {
            classDef.inheritedType.accept(this);
        }
        for (var feature : classDef.features) {
            feature.accept(this);
        }
        --indent;
        return null;
    }

    @Override
    public Void visit(Program program) {
        printIndent("program");
        ++indent;
        for (ASTNode classDef : program.classDefs) {
            classDef.accept(this);
        }
        --indent;
        return null;
    }

    @Override
    public Void visit(Type type) {
        if (type.token == null) {
            return null;
        }

        printIndent(type.token.getText());
        return null;
    }

    @Override
    public Void visit(Id id) {
        printIndent(id.token.getText());
        return null;
    }

    @Override
    public Void visit(VariableDef variableDef) {
        printIndent("attribute");
        indent++;
        variableDef.id.accept(this);
        variableDef.type.accept(this);
        if (variableDef.expression != null) {
            variableDef.expression.accept(this);
        }
        --indent;
        return null;
    }

    @Override
    public Void visit(FunctionDef functionDef) {
        printIndent("method");
        ++indent;
        functionDef.functionId.accept(this);
        for (var formal : functionDef.formals) {
           formal.accept(this);
        }
        functionDef.type.accept(this);
        functionDef.body.accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        printIndent("formal");
        indent++;
        formal.id.accept(this);
        formal.type.accept(this);
        indent--;
        return null;
    }

    @Override
    public Void visit(IntLiteral intLiteral) {
        printIndent(intLiteral.token.getText());
        return null;
    }

    @Override
    public Void visit(BoolLiteral boolLiteral) {
        printIndent(boolLiteral.token.getText());
        return null;
    }

    @Override
    public Void visit(StringLiteral stringLiteral) {
        printIndent(stringLiteral.token.getText());
        return null;
    }

    @Override
    public Void visit(Relational relational) {
        printIndent(relational.getToken().getText());
        ++indent;
        relational.left.accept(this);
        relational.right.accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(Arithmetic arithmetic) {
        printIndent(arithmetic.getToken().getText());
        ++indent;
        arithmetic.left.accept(this);
        arithmetic.right.accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(Paren paren) {
        paren.expression.accept(this);
        return null;
    }

    @Override
    public Void visit(Complement complement) {
        printIndent(complement.getToken().getText());
        ++indent;
        complement.expression.accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(Not not) {
        printIndent(not.getToken().getText());
        ++indent;
        not.expression.accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        printIndent(assign.getToken().getText());
        ++indent;
        assign.id.accept(this);
        assign.expression.accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(IsVoid isVoid) {
        printIndent(isVoid.getToken().getText());
        ++indent;
        isVoid.expression.accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(New aNew) {
        printIndent(aNew.getToken().getText());
        ++indent;
        aNew.type.accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(Call call) {
        printIndent("implicit dispatch");
        ++indent;
        call.id.accept(this);
        for (var parameter : call.parameters) {
            parameter.accept(this);
        }
        --indent;
        return null;
    }

    @Override
    public Void visit(CallDispatch callDispatch) {
        printIndent(callDispatch.getToken().getText());
        ++indent;
        callDispatch.e.accept(this);
        if (callDispatch.type != null) {
            callDispatch.type.accept(this);
        }
        callDispatch.id.accept(this);
        for (var expr : callDispatch.exprs) {
            expr.accept(this);
        }
        --indent;
        return null;
    }

    @Override
    public Void visit(If anIf) {
        printIndent("if");
        ++indent;
        anIf.ifBranch.accept(this);
        anIf.thenBranch.accept(this);
        anIf.elseBranch.accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(While aWhile) {
        printIndent(aWhile.getToken().getText());
        ++indent;
        aWhile.condition.accept(this);
        aWhile.body.accept(this);
        --indent;
        return null;
    }

    @Override
    public Void visit(Case aCase) {
        printIndent(aCase.getToken().getText());
        ++indent;
        aCase.expression.accept(this);
        for (int i = 0; i < aCase.types.size(); i++) {
            var type = aCase.types.get(i);
            var id = aCase.ids.get(i);
            var expr = aCase.exprs.get(i);
            printIndent("case branch");
            ++indent;
            id.accept(this);
            type.accept(this);
            expr.accept(this);
            --indent;
        }
        --indent;
        return null;
    }

    @Override
    public Void visit(Block block) {
        printIndent("block");
        ++indent;
        for (var expr : block.expressions) {
            expr.accept(this);
        }
        --indent;
        return null;
    }

    @Override
    public Void visit(Let let) {
        printIndent(let.getToken().getText());
        ++indent;
        for (int i = 0; i < let.ids.size(); ++i) {
            printIndent("local");
            ++indent;
            Id id = let.ids.get(i);
            Type type = let.types.get(i);
            Expression expression = let.expressions.get(i);
            id.accept(this);
            type.accept(this);
            if (expression != null) {
                expression.accept(this);
            }
            --indent;
        }
        let.body.accept(this);
        --indent;
        return null;
    }

    void printIndent(String str) {
        for (int i = 0; i < indent; i++)
            System.out.print("  ");
        System.out.println(str);
    }
}
