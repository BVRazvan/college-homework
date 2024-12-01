package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

public class Formal extends Definition {
    Id id;
    Type type;
    Formal(Id id,
           Type type,
           Token token,
           ParserRuleContext parserContext) {
        super(token, parserContext);
        this.id = id;
        this.type = type;
    }

    public Id getId() {
        return id;
    }

    public Type getType() {
        return type;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
