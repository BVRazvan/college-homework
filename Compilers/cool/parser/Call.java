package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class Call extends Expression {
    Id id;
    LinkedList<Expression> parameters;

    public Call(Id id,
         LinkedList<Expression> parameters,
         Token token,
         ParserRuleContext parserContext) {
        super(token, parserContext);
        this.id = id;
        this.parameters = parameters;
    }

    public Id getId() {
        return id;
    }

    public LinkedList<Expression> getParameters() {
        return parameters;
    }

    public <T> T accept(ASTVisitor<T> visitor) {return visitor.visit(this);}
}
