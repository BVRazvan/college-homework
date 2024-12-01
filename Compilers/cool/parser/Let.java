package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class Let extends Expression {
    LinkedList<Id> ids;
    LinkedList<Type> types;
    LinkedList<Expression> expressions;
    Expression body;

    Let(LinkedList<Id> ids,
        LinkedList<Type> types,
        LinkedList<Expression> expressions,
        Expression body,
        Token token,
        ParserRuleContext parserContext) {
        super(token, parserContext);
        this.ids = ids;
        this.types = types;
        this.expressions = expressions;
        this.body = body;
    }

    public LinkedList<Id> getIds() {
        return ids;
    }

    public LinkedList<Type> getTypes() {
        return types;
    }

    public LinkedList<Expression> getExpressions() {
        return expressions;
    }

    public Expression getBody() {
        return body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {return visitor.visit(this);}
}
