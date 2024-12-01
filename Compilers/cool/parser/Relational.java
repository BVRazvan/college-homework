package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Relational extends Expression {
    Expression left;
    Expression right;

    Relational(Expression left,
               Expression right,
               Token token,
               ParserRuleContext parserContext
                ) {
        super(token, parserContext);
        this.left = left;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public Expression getRight() {
        return right;
    }

    public <T> T accept(ASTVisitor<T> visitor) { return visitor.visit(this); }
}
