package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class VariableDef extends Feature {
    Id id;
    Type type;
    Expression expression;

    VariableDef(Id id,
                Type type,
                Expression expression,
                Token token,
                ParserRuleContext parserContext) {
        super(token, parserContext);
        this.id = id;
        this.expression = expression;
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Expression getExpression() {
        return expression;
    }

    public Id getId() {
        return id;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
