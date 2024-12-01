package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class IntLiteral extends Expression {
    public IntLiteral(Token token, ParserRuleContext parserContext) {
        super(token, parserContext);
    }

    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}
