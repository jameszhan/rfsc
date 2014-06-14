grammar Athena ;

r           : assign ;
assign      : ID '=' expr ';' ;
expr        : INT ;

ID          : [a-zA-Z][a-zA-Z0-9]* ;
INT         : [0-9]+ ;