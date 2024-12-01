package cool.parser;

import java.util.LinkedList;

public class ASTConstructionVisitor extends CoolParserBaseVisitor<ASTNode> {
    @Override
    public ASTNode visitProgram(CoolParser.ProgramContext ctx) {
        LinkedList<ASTNode> nodes = new LinkedList<>();
        for (var child : ctx.children) {
            ASTNode node = visit(child);
            if (node != null) {
                node.debugStr = child.getText();
                nodes.add(node);
            }
        }
        return new Program(ctx.start, nodes, ctx);
    }

    @Override
    public ASTNode visitClass(CoolParser.ClassContext ctx) {
        LinkedList<ASTNode> nodes = new LinkedList<>();
        for (var child : ctx.features) {
            ASTNode node = visit(child);
            if (node != null) {
                node.debugStr = child.getText();
                nodes.add(node);
            }
        }
        return new ClassDef(new Type(ctx.type, ctx),
                new Type(ctx.inheritedType, ctx),
                nodes,
                ctx.start,
                ctx);
    }

    @Override
    public ASTNode visitFuncDef(CoolParser.FuncDefContext ctx) {
        LinkedList<Formal> nodes = new LinkedList<>();
        for (var child : ctx.formals) {
            Formal node = (Formal)visit(child);
            if (node != null) {
                node.debugStr = child.getText();
                nodes.add(node);
            }
        }
        return new FunctionDef(new Id(ctx.id, ctx), new Type(ctx.type, ctx), nodes, (Expression)visit(ctx.e), ctx.start, ctx);
    }

    @Override
    public ASTNode visitVarDef(CoolParser.VarDefContext ctx) {
        return new VariableDef(new Id(ctx.id, ctx), new Type(ctx.type, ctx), ctx.e != null ? (Expression)visit(ctx.e) : null, ctx.start, ctx);
    }

    @Override
    public ASTNode visitFormal(CoolParser.FormalContext ctx) {
        return new Formal(new Id(ctx.id, ctx), new Type(ctx.type, ctx), ctx.start, ctx);
    }

    @Override
    public ASTNode visitNew(CoolParser.NewContext ctx) {
        return new New(new Type(ctx.type, ctx), ctx.op, ctx);
    }

    @Override
    public ASTNode visitPlusMinus(CoolParser.PlusMinusContext ctx) {
        return new Arithmetic((Expression) visit(ctx.left), (Expression) visit(ctx.right), ctx.op, ctx);
    }

    @Override
    public ASTNode visitDispatch(CoolParser.DispatchContext ctx) {
        LinkedList<Expression> nodes = new LinkedList<>();
        for (var child : ctx.exprs) {
            Expression node = (Expression)visit(child);
            if (node != null) {
                node.debugStr = child.getText();
                nodes.add(node);
            }
        }
        return new CallDispatch((Expression) visit(ctx.e), ctx.type == null ? null : new Type(ctx.type, ctx), new Id(ctx.id, ctx), nodes, ctx.op, ctx);
    }

    @Override
    public ASTNode visitBool(CoolParser.BoolContext ctx) {
        return new BoolLiteral(ctx.bool, ctx);
    }

    @Override
    public ASTNode visitString(CoolParser.StringContext ctx) {
        return new StringLiteral(ctx.STRING().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitIsvoid(CoolParser.IsvoidContext ctx) {
        return new IsVoid((Expression) visit(ctx.e), ctx.op, ctx);
    }

    @Override
    public ASTNode visitWhile(CoolParser.WhileContext ctx) {
        return new While((Expression) visit(ctx.cond), (Expression) visit(ctx.body), ctx.op, ctx);
    }

    @Override
    public ASTNode visitInt(CoolParser.IntContext ctx) {
        return new IntLiteral(ctx.INT().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitCall(CoolParser.CallContext ctx) {
        LinkedList<Expression> nodes = new LinkedList<>();
        for (var child : ctx.exprs) {
            Expression node = (Expression)visit(child);
            if (node != null) {
                node.debugStr = child.getText();
                nodes.add(node);
            }
        }
        return new Call(new Id(ctx.id, ctx), nodes, ctx.start, ctx);
    }

    @Override
    public ASTNode visitParen(CoolParser.ParenContext ctx) {
        return new Paren((Expression) visit(ctx.e), ctx.start, ctx);
    }

    @Override
    public ASTNode visitNot(CoolParser.NotContext ctx) {
        return new Not((Expression) visit(ctx.e), ctx.not, ctx);
    }

    @Override
    public ASTNode visitMultDiv(CoolParser.MultDivContext ctx) {
        return new Arithmetic((Expression) visit(ctx.left), (Expression) visit(ctx.right), ctx.op, ctx);
    }

    @Override
    public ASTNode visitBlock(CoolParser.BlockContext ctx) {
        LinkedList<Expression> nodes = new LinkedList<>();
        for (var child : ctx.multi_body) {
            Expression node = (Expression) visit(child);
            if (node != null) {
                node.debugStr = child.getText();
                nodes.add(node);
            }
        }
        return new Block(nodes, ctx.start, ctx);
    }

    @Override
    public ASTNode visitLet(CoolParser.LetContext ctx) {
        LinkedList<Id> ids = new LinkedList<>();
        LinkedList<Type> types = new LinkedList<>();
        LinkedList<Expression> expressions = new LinkedList<>();
        for (int i = 0; i < ctx.ids.size(); ++i) {
            Id id = new Id(ctx.ids.get(i), ctx);
            Type type = new Type(ctx.types.get(i), ctx);
            Expression expr = null;
            if (ctx.assign_exprs.get(i) != null) {
                expr = (Expression) visit(ctx.assign_exprs.get(i));
            }
            ids.add(id);
            types.add(type);
            expressions.add(expr);
        }
        return new Let(ids, types, expressions, (Expression) visit(ctx.body), ctx.op, ctx);
    }

    @Override
    public ASTNode visitRelational(CoolParser.RelationalContext ctx) {
        return new Relational((Expression) visit(ctx.left), (Expression) visit(ctx.right), ctx.op, ctx);
    }

    @Override
    public ASTNode visitId(CoolParser.IdContext ctx) {
        return new Id(ctx.ID().getSymbol(), ctx);
    }

    @Override
    public ASTNode visitComplement(CoolParser.ComplementContext ctx) {
        return new Complement((Expression) visit(ctx.e), ctx.op, ctx);
    }

    @Override
    public ASTNode visitIf(CoolParser.IfContext ctx) {
        return new If((Expression) visit(ctx.cond), (Expression) visit(ctx.thenBranch), (Expression) visit(ctx.elseBranch), ctx.op, ctx);
    }

    @Override
    public ASTNode visitCase(CoolParser.CaseContext ctx) {
        LinkedList<Id> ids = new LinkedList<>();
        LinkedList<Type> types = new LinkedList<>();
        LinkedList<Expression> expressions = new LinkedList<>();
        for (var id : ctx.ids) {
            ids.add(new Id(id, ctx));
        }
        for (var type : ctx.types) {
            types.add(new Type(type, ctx));
        }
        for (var expr : ctx.options) {
            expressions.add((Expression) visit(expr));
        }
        return new Case((Expression) visit(ctx.cond), ids, types, expressions, ctx.op, ctx);
    }

    @Override
    public ASTNode visitAssign(CoolParser.AssignContext ctx) {
        return new Assign(new Id(ctx.id, ctx), (Expression) visit(ctx.e), ctx.op, ctx);
    }

}
