package cool.parser;

import cool.structures.ClassSymbol;
import cool.structures.Scope;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class ClassDef extends ASTNode {
    Type type;
    Type inheritedType;
    LinkedList<ASTNode> features;
    ClassSymbol symbol;
    Scope scope;
    ClassDef(Type type,
             Type inheritedType,
             LinkedList<ASTNode> features,
             Token token,
             ParserRuleContext parserContext) {
        super(token, parserContext);
        this.type = type;
        this.inheritedType = inheritedType;
        this.features = features;
    }

    public LinkedList<ASTNode> getFeatures() {
        return features;
    }
    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public Scope getScope() {
        return scope;
    }

    public ClassSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(ClassSymbol symbol) {
        this.symbol = symbol;
    }

    public Type getType() {
        return type;
    }

    public Type getInheritedType() {
        return inheritedType;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
