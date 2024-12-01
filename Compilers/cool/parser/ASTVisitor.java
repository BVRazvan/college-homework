package cool.parser;

import org.antlr.v4.runtime.*;

import java.lang.reflect.Parameter;
import java.util.function.Function;

public interface ASTVisitor<T> {
    T visit(ClassDef classDef);
    T visit(Program program);
    T visit(Type type);
    T visit(Id id);
    T visit(VariableDef variableDef);
    T visit(FunctionDef functionDef);
    T visit(Formal formal);
    public static void error(Token token, String message) {
        System.err.println("line " + token.getLine()
                + ":" + (token.getCharPositionInLine() + 1)
                + ", " + message);
    }

    T visit(IntLiteral intLiteral);

    T visit(BoolLiteral boolLiteral);

    T visit(StringLiteral stringLiteral);

    T visit(Relational relational);

    T visit(Arithmetic arithmetic);

    T visit(Paren paren);

    T visit(Complement complement);

    T visit(Not not);

    T visit(Assign assign);

    T visit(IsVoid isVoid);

    T visit(New aNew);

    T visit(Call call);

    T visit(CallDispatch callDispatch);

    T visit(If anIf);

    T visit(While aWhile);

    T visit(Case aCase);

    T visit(Block block);

    T visit(Let let);
}
