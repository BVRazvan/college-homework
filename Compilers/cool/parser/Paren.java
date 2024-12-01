package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Paren extends Expression {
    Expression expression;

    Paren(Expression expression,
           Token token,
          ParserRuleContext parserContext
    ) {
        super(token, parserContext);
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}
