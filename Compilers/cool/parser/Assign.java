package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Assign extends Expression {
    Id id;
    Expression expression;

    Assign(Id id,
           Expression expression,
           Token token,
           ParserRuleContext parserContext) {
        super(token, parserContext);
        this.id = id;
        this.expression = expression;
    }

    public Id getId() {
        return id;
    }

    public Expression getExpression() {
        return expression;
    }

    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}
