package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class If extends Expression {
    Expression ifBranch;
    Expression thenBranch;
    Expression elseBranch;

    If(Expression ifBranch,
       Expression thenBranch,
       Expression elseBranch,
       Token token,
       ParserRuleContext parserContext) {
        super(token, parserContext);
        this.ifBranch = ifBranch;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public Expression getIfBranch() {
        return ifBranch;
    }

    public Expression getThenBranch() {
        return thenBranch;
    }

    public Expression getElseBranch() {
        return elseBranch;
    }

    public <T> T accept(ASTVisitor<T> visitor) {return visitor.visit(this);}
}
