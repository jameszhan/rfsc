grammar Arithmetic ;

options {

}

prog: stat+ ;

stat: expr NEWLINE | ID '=' expr NEWLINE | NEWLINE ;

expr: expr ('*' | '/') expr | expr ('+' | '-') expr | INT | ID | '(' expr ')' ;

ID: [a-zA-Z][a-zA-Z0-9]* ;
INT: [1-9][0-9]* ;
NEWLINE: '\r'? '\n' ;
WS: [ \t]+ -> skip ;