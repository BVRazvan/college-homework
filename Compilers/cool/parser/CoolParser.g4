parser grammar CoolParser;

options {
    tokenVocab = CoolLexer;
}

@header{
    package cool.parser;
}

program
    :   (class SEMI)+ EOF
    ;

class
    :   CLASS type=TYPE (INHERITS inheritedType=TYPE)? LBRACE (features+=feature SEMI)* RBRACE
    ;

feature
    :   id=ID LPAREN (formals+=formal (COMMA formals+=formal)*)? RPAREN COLON type=TYPE LBRACE e=expr RBRACE                                                # funcDef
    |   id=ID COLON type=TYPE (ASSIGN e=expr)?                                                                                                              # varDef
    ;

formal
    :   id=ID COLON type=TYPE
    ;
expr
    :   id=ID LPAREN (exprs+=expr (COMMA exprs+=expr)*)? RPAREN                                                                 # call
    |   e=expr (AT type=TYPE)? op=DOT id=ID LPAREN (exprs+=expr (COMMA exprs+=expr)*)? RPAREN                                                                                            # dispatch
    |   op=IF cond=expr THEN thenBranch=expr ELSE elseBranch=expr FI                                                                                           # if
    |   op=WHILE cond=expr LOOP body=expr POOL                                                                                                                 # while
    |   LBRACE (multi_body+=expr SEMI)+ RBRACE                                                                                                              # block
    |   op=LET ids+=ID COLON types+=TYPE ((ASSIGN assign_exprs+=expr) | {((LetContext)_localctx).assign_exprs.add(null);}) (COMMA ids+=ID COLON types+=TYPE ((ASSIGN assign_exprs+=expr) | {((LetContext)_localctx).assign_exprs.add(null);}))* IN body=expr              # let
    |   op=CASE cond=expr OF (ids+=ID COLON types+=TYPE RESULT options+=expr SEMI)+ ESAC                                                                      # case
    |   op=NEW type=TYPE                                                                                                                                       # new
    |   op=COMPLEMENT e=expr                                                                                                                                   # complement
    |   op=ISVOID e=expr                                                                                                                                       # isvoid
    |   left=expr op=(MULT | DIV) right=expr                                                                                                                # multDiv
    |   left=expr op=(PLUS | MINUS) right=expr                                                                                                              # plusMinus
    |   left=expr op=(LT | LE | EQUAL) right=expr                                                                                                           # relational
    |   LPAREN e=expr RPAREN                                                                                                                                # paren
    |   not=NOT e=expr                                                                                                                                          # not
    |   id=ID                                                                                                                                               # id
    |   bool=(TRUE | FALSE)                                                                                                                                           # bool
    |   int=INT                                                                                                                                             # int
    |   string=STRING                                                                                                                                       # string
    |   id=ID op=ASSIGN e=expr                                                                                                                              # assign
    ;
