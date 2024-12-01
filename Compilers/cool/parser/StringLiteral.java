package cool.parser;

import cool.structures.Scope;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class StringLiteral extends Expression {
    public StringLiteral(Token token, ParserRuleContext parserContext) {
        super(token, parserContext);
    }

    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}
