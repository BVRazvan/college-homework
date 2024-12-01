package cool.parser;

import cool.structures.Scope;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class New extends Expression {
    Type type;
    Scope scope;

    New(Type type,
        Token token,
        ParserRuleContext parserContext) {
        super(token, parserContext);
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }
    public <T> T accept(ASTVisitor<T> visitor) {return visitor.visit(this);}
}
