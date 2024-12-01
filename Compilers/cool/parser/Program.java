package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class Program extends Expression {
    LinkedList<ASTNode> classDefs;

    Program(Token token,
            LinkedList<ASTNode> classDefs,
            ParserRuleContext parserContext) {
        super(token, parserContext);
        this.classDefs = classDefs;
    }

    public LinkedList<ASTNode> getClassDefs() {
        return classDefs;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
