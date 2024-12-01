package cool.parser;

import cool.structures.TypeSymbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class CallDispatch extends Expression {
    Expression e;
    Type type;
    Id id;
    LinkedList<Expression> exprs;
    private TypeSymbol exprType;

    CallDispatch(Expression e,
                 Type type,
                 Id id,
                 LinkedList<Expression> exprs,
                 Token token,
                 ParserRuleContext parserContext) {
        super(token, parserContext);
        this.e = e;
        this.type = type;
        this.id = id;
        this.exprs = exprs;
    }

    public Expression getE() {
        return e;
    }

    public Type getType() {
        return type;
    }

    public Id getId() {
        return id;
    }

    public LinkedList<Expression> getExprs() {
        return exprs;
    }

    public <T> T accept(ASTVisitor<T> visitor) {return visitor.visit(this);}

    public TypeSymbol getExprType() {
        return exprType;
    }

    public void setExprType(TypeSymbol exprType) {
        this.exprType = exprType;
    }
}
