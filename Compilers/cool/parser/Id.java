package cool.parser;

import cool.structures.IdSymbol;
import cool.structures.Scope;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.lang.reflect.Array;

public class Id extends Expression {
    Id(Token token, ParserRuleContext parserContext) {
        super(token, parserContext);
    }

    IdSymbol symbol;
    Scope scope;

    public void setSymbol(IdSymbol symbol) {
        this.symbol = symbol;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public IdSymbol getSymbol() {
        return this.symbol;
    }

    public Scope getScope() {
        return this.scope;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
