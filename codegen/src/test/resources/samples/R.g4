grammar R;

prog    :   (expr_or_assign (';' | NL) | NL)* EOF ;

expr_or_assign
    :   expr ('<-' | '=' | '<<-' ) expr_or_assign
    |   expr
    ;

expr
    :   expr '[[' sublist ']' ']' // '[[' follows R's yacc grammar
    |   expr '[' sublist ']'
    |   expr ('::'|':::') expr
    |   expr ('$'|'@') expr
    |   expr '^'<assoc=right> expr
    |   ('-'|'+') expr
    |   expr ':' expr
    |   expr USER_OP expr // anything wrappedin %: '%' .* '%'
    |   expr ('*'|'/') expr
    |   expr ('+'|'-') expr
    |   expr ('>'|'>='|'<'|'<='|'=='|'!=') expr
    |   '!' expr
    |   expr ('&'|'&&') expr
    |   expr ('|'|'||') expr
    |   '~' expr
    |   expr '~' expr
    |   expr ('->'|'->>'|':=') expr
    |   'function' '(' formlist? ')' expr // define function
    |   expr '(' sublist ')' // call function
    ;

formlist : form (',' form)* ;
form    : ID | ID '=' expr | '...' ;

sublist : sub (',' sub)* ;
￼￼￼￼
sub     :   expr
        |   ID  '='
        |   ID  '=' expr
        |   STRING  '='
        |   STRING  '=' expr
        |   'NULL'  '='
        |   'NULL'  '=' expr
        |   '...'
        |
        ;

ID  : '.' (LETTER|'_'|'.') (LETTER|DIGIT|'_'|'.')* | LETTER (LETTER|DIGIT|'_'|'.')* ;

fragment LETTER : [a-zA-Z] ;

NL : '\r'? '\n' ;