grammar Clojure ;

prog        : (s_exp)* ; //-> ^(PROGRAM sexpr*);

s_exp       : QUOTE?(list|atom) ;

list        : '(' ')' | '(' members ')' ; // -> ^(LIST members);

members     : (s_exp)+ ;

atom        : OPERATOR | ID | num | STRING ;

num         : (n=INT|n=FLOAT) ; //-> ^(NUM $n);

WS          : [ \t\r\n]+ -> skip  ;

INT         : [0-9]+ ;
FLOAT       : [0-9]+ '.' [0-9]+ ;
ID          : [a-zA-Z][a-zA-Z0-9]* ;
STRING      : '"' [a-zA-Z0-9]* '"' ;
OPERATOR    : '+' | '-' | '*' | '/' | '%' ;
QUOTE       : '`' | '\'' ;
