
grammar Athena ;

options {
}


prog                : assign ;
assign              : ID '=' expression ';' ;
expression          : INT | array;

array               : '[' value ( ',' value )* ']' ;
value               : array | INT ;

ID                  : [a-zA-Z][a-zA-Z0-9]* ;
INT                 : [0-9]+ ;


WS                  : [ \t\r\n]+ -> skip  ;