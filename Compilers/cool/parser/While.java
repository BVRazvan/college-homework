package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class While extends Expression {
    Expression condition;
    Expression body;

    While(Expression condition,
          Expression body,
          Token token,
          ParserRuleContext parserContext) {
        super(token, parserContext);
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public Expression getBody() {
        return body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {return visitor.visit(this);}
}
