lexer grammar CoolLexer;

tokens { ERROR } 

@header{
    package cool.lexer;
    import java.util.Stack;
}

@members{
    Stack<Pair> commentsStack = new Stack<>();
    Boolean eofMet = false;
    Boolean unmatchedMet = false;
    private void raiseError(String msg) {
        setText(msg);
        setType(ERROR);
    }
}

// Keywords
CLASS: 'class';
ELSE: 'else';
FI: 'fi';
IF: 'if';
IN: 'in';
INHERITS: 'inherits';
ISVOID: 'isvoid';
LET: 'let';
LOOP: 'loop';
POOL: 'pool';
THEN: 'then';
WHILE: 'while';
CASE: 'case';
ESAC: 'esac';
NEW: 'new';
OF: 'of';
NOT: 'not';
COLON: ':';
TRUE: 'true';
FALSE: 'false';
TYPE: 'Int' | 'Bool' | 'String' | 'SELF_TYPE' | UPPER_LETTER (LETTER | '_' | DIGIT)*;

fragment LOWER_LETTER: [a-z];
fragment UPPER_LETTER: [A-Z];
fragment LETTER: [a-zA-Z];
fragment EXTRA_LETTER: [ \b\t\n\f] | '\\';

ID: LOWER_LETTER(LETTER | '_' | DIGIT)* | 'self';

// Int
fragment DIGIT: [0-9];
INT: DIGIT+;

// String
fragment ANY_LETTER: LETTER | EXTRA_LETTER;
STRING: '"' .*? ('"' | (~'\\' NEW_LINE) | EOF)  {
    Boolean terminate = false;
    String string = getText();
    if (string.contains("\u0000")) {
        raiseError("String contains null character");
        break;
    }
    if (string.contains("\r\n")) {
        raiseError("Unterminated string constant");
        break;
    }
    if (string.charAt(string.length() - 1) - '\0' != 34) {
        raiseError("EOF in string constant");
        break;
    }
    string = string.substring(1);
    if (string.length() == 0) {
        setText(string);
        break;
    }
    string = string.substring(0, string.length() - 1);
    if (string.length() == 0) {
        setText(string);
        break;
    }
    for (int i = 0; i < string.length() && terminate == false; ++i) {
        char currentChar = string.charAt(i);
        if (i > 0) {
            char prevChar = string.charAt(i - 1);
            if (currentChar - '\0' == 10 && prevChar - '\0' != 92) {
                raiseError("Unterminated string constant");
                terminate = true;
            }
        }
    }
    if (terminate == true) {
        break;
    }
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < string.length(); i++) {
        char currentChar = string.charAt(i);
        if (i > 0) {
            char prevChar = string.charAt(i - 1);
            if (prevChar == '\\' && currentChar == 'n') {
                result.append('\n');
                ++i;
            }
            else if (prevChar == '\\' && currentChar == 't') {
                result.append('\t');
                ++i;
            }
            else if (prevChar == '\\' && currentChar == 'f') {
                result.append('\f');
                ++i;
            }
            else if (prevChar == '\\' && currentChar == 'b') {
                result.append('\b');
                ++i;
            } else {
                result.append(prevChar);
                if (i + 1 == string.length()) {
                    result.append(currentChar);
                }
            }
        }
    }
    if (string.length() == 1) {
        result.append(string.charAt(0));
    }
    string = result.toString();
    result = new StringBuilder();
    for (int i = 0; i < string.length(); ++i) {
        char currentChar = string.charAt(i);
        if (currentChar == '\\' && i + 1 < string.length()) {
            result.append(string.charAt(i + 1));
            i++;
        } else {
            result.append(currentChar);
        }
    }
    string = result.toString();
    if (string.length() > 1024) {
        raiseError("String constant too long");
        break;
    }
    setText(string);
    };

// Extras
SEMI: ';';
COMMA: ',';
ASSIGN: '<-';
LPAREN: '(';
RPAREN: ')';
LBRACE: '{';
RBRACE: '}';
PLUS: '+';
MINUS: '-';
MULT: '*';
DIV: '/';
LT: '<';
LE: '<=';
EQUAL: '=';
RESULT: '=>';
DOT: '.';
AT: '@';
COMPLEMENT: '~';

fragment NEW_LINE: '\r'? '\n';
LINE_COMMENT: '--' (~'\n')* -> skip;

BLOCK_COMMENT
    : '(*'
      (BLOCK_COMMENT | .)*?
      '*)' -> skip
    ;

BLOCK_COMMENT_EOF
    : '(*' ~[*)]*? BLOCK_COMMENT* ~[*)]*? EOF {raiseError("EOF in comment"); };

UNMATCHED
    : '*)' {raiseError("Unmatched *)");}
    ;

WS
    :   [ \n\f\r\t]+ -> skip
    ;

INVALID_CHAR
    : . {raiseError("Invalid character: " + getText());}
    ;
