package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class FunctionDef extends Feature {
    Id functionId;
    Type type;
    LinkedList<Formal> formals;
    Expression body;

    FunctionDef(Id functionId,
                Type type,
                LinkedList<Formal> formals,
                Expression body,
                Token token,
                ParserRuleContext parserContext) {
        super(token, parserContext);
        this.functionId = functionId;
        this.type = type;
        this.formals = formals;
        this.body = body;
    }

    public Id getFunctionId() {
        return functionId;
    }

    public Type getType() {
        return type;
    }

    public Expression getBody() {
        return body;
    }

    public LinkedList<Formal> getFormals() {
        return formals;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
