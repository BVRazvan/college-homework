package cool.compiler;

import cool.asmgen.AsmGenVisitor;
import cool.structures.*;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

import cool.lexer.*;
import cool.parser.*;

import java.io.*;


public class Compiler {
    // Annotates class nodes with the names of files where they are defined.
    public static ParseTreeProperty<String> fileNames = new ParseTreeProperty<>();
    public static String fileName;
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("No file(s) given");
            return;
        }

        fileName = args[0].substring(args[0].lastIndexOf("/") + 1);

        CoolLexer lexer = null;
        CommonTokenStream tokenStream = null;
        CoolParser parser = null;
        ParserRuleContext globalTree = null;
        
        // True if any lexical or syntax errors occur.
        boolean lexicalSyntaxErrors = false;
        
        // Parse each input file and build one big parse tree out of
        // individual parse trees.
        for (var fileName : args) {
            var input = CharStreams.fromFileName(fileName);

            // Lexer
            if (lexer == null)
                lexer = new CoolLexer(input);
            else
                lexer.setInputStream(input);

            // Token stream
            if (tokenStream == null)
                tokenStream = new CommonTokenStream(lexer);
            else
                tokenStream.setTokenSource(lexer);
                
            /*
            // Test lexer only.
            tokenStream.fill();
            List<Token> tokens = tokenStream.getTokens();
            tokens.stream().forEach(token -> {
                var text = token.getText();
                var name = CoolLexer.VOCABULARY.getSymbolicName(token.getType());
                
                System.out.println(text + " : " + name);
                //System.out.println(token);
            });
            */

            // Parser
            if (parser == null)
                parser = new CoolParser(tokenStream);
            else
                parser.setTokenStream(tokenStream);

            // Customized error listener, for including file names in error
            // messages.
            var errorListener = new BaseErrorListener() {
                public boolean errors = false;

                @Override
                public void syntaxError(Recognizer<?, ?> recognizer,
                                        Object offendingSymbol,
                                        int line, int charPositionInLine,
                                        String msg,
                                        RecognitionException e) {
                    String newMsg = "\"" + new File(fileName).getName() + "\", line " +
                            line + ":" + (charPositionInLine + 1) + ", ";

                    Token token = (Token) offendingSymbol;
                    if (token.getType() == CoolLexer.ERROR)
                        newMsg += "Lexical error: " + token.getText();
                    else
                        newMsg += "Syntax error: " + msg;

                    System.err.println(newMsg);
                    errors = true;
                }
            };

            parser.removeErrorListeners();
            parser.addErrorListener(errorListener);

            // Actual parsing
            var tree = parser.program();
            if (globalTree == null)
                globalTree = tree;
            else
                // Add the current parse tree's children to the global tree.
                for (int i = 0; i < tree.getChildCount(); i++)
                    globalTree.addAnyChild(tree.getChild(i));

            // Annotate class nodes with file names, to be used later
            // in semantic error messages.
            for (int i = 0; i < tree.getChildCount(); i++) {
                var child = tree.getChild(i);
                // The only ParserRuleContext children of the program node
                // are class nodes.
                if (child instanceof ParserRuleContext)
                    fileNames.put(child, fileName);
            }

            // Record any lexical or syntax errors.
            lexicalSyntaxErrors |= errorListener.errors;

            // Stop before semantic analysis phase, in case errors occurred.
            if (lexicalSyntaxErrors) {
                System.err.println("Compilation halted");
                return;
            }

            var astConstructionVisitor = new ASTConstructionVisitor();
            var ast = astConstructionVisitor.visit(tree);

            // Populate global scope.
            SymbolTable.defineBasicClasses();

            // TODO Semantic analysis
            var classDefinitionPassVisitor = new ClassDefinitionPassVisitor();
            ast.accept(classDefinitionPassVisitor);

            var classResolutionPassVisitor = new ClassResolutionPassVisitor(classDefinitionPassVisitor.inheritanceChain, classDefinitionPassVisitor.classHeight);
            ast.accept(classResolutionPassVisitor);

            if (SymbolTable.hasSemanticErrors()) {
                System.err.println("Compilation halted");
                return;
            }

            var generalDefinitionPassVisitor = new GeneralDefinitionPassVisitor();
            ast.accept(generalDefinitionPassVisitor);

            var generalResolutionPassVisitor = new GeneralResolutionPassVisitor(classDefinitionPassVisitor.inheritanceChain, generalDefinitionPassVisitor.scopeMap, classDefinitionPassVisitor.classHeight);
            ast.accept(generalResolutionPassVisitor);

            if (SymbolTable.hasSemanticErrors()) {
                System.err.println("Compilation halted");
                return;
            }

            var asmGenVisitor = new AsmGenVisitor(classDefinitionPassVisitor.inheritanceChain, generalResolutionPassVisitor.classFunctions, generalResolutionPassVisitor.classAttrs, generalResolutionPassVisitor.classAttrsNames, generalDefinitionPassVisitor.scopeMap);
            var t = ast.accept(asmGenVisitor);
            System.out.println(t.render());
        }
    }
}
