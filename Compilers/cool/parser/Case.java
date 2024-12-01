package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class Case extends Expression {
    Expression expression;
    LinkedList<Id> ids;
    LinkedList<Type> types;
    LinkedList<Expression> exprs;

    Case(Expression expression,
         LinkedList<Id> ids,
         LinkedList<Type> types,
         LinkedList<Expression> exprs,
         Token token,
         ParserRuleContext parserContext) {
        super(token, parserContext);
        this.expression = expression;
        this.ids = ids;
        this.types = types;
        this.exprs = exprs;
    }

    public LinkedList<Type> getTypes() {
        return types;
    }

    public LinkedList<Id> getIds() {
        return ids;
    }

    public Expression getExpression() {
        return expression;
    }

    public LinkedList<Expression> getExpressions() {
        return exprs;
    }

    public <T> T accept(ASTVisitor<T> visitor) {return visitor.visit(this);}
}
