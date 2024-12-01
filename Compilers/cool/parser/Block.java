package cool.parser;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import java.util.LinkedList;

public class Block extends Expression {
    LinkedList<Expression> expressions;

    Block(LinkedList<Expression> expressions,
          Token token,
          ParserRuleContext parserContext) {
        super(token, parserContext);
        this.expressions = expressions;
    }

    public LinkedList<Expression> getExpressions() {
        return expressions;
    }

    public <T> T accept(ASTVisitor<T> visitor) {return visitor.visit(this);}
}
