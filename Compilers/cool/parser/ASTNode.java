package cool.parser;

import cool.structures.Scope;
import cool.structures.TypeSymbol;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public abstract class ASTNode {

    protected Token token;
    public String debugStr = null;        // used in codegen
    public ParserRuleContext parserContext;
    public Boolean hasError = false;
    ASTNode(Token token, ParserRuleContext parserContext) {
        this.token = token;
        this.parserContext = parserContext;
    }
    private boolean selfType;
    private Scope scope;
    private TypeSymbol type;

    public Token getToken() {
        return token;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public boolean isSelfType() {
        return selfType;
    }

    public void setSelfType(boolean selfType) {
        this.selfType = selfType;
    }

}

abstract class Feature extends ASTNode {
    Feature(Token token, ParserRuleContext parserContext) {
        super(token, parserContext);
    }
}

abstract class Definition extends ASTNode {
    Definition(Token token, ParserRuleContext parserContext) {
        super(token, parserContext);
    }
}

