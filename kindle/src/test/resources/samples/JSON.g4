grammar JSON ;
json:   object
    |   array
    |
    ;

object
    :   '{' pair (',' pair)* '}'
    |   '{' '}'
    ;

pair:   STRING ':' value ;

array
    :   '[' value (',' value)* ']'
    |   '[' ']'
    ;

value
    :   STRING
    |   NUMBER
    |   object
    |   array
    |   'true'
    |   'false'
    |   'null'
    ;

STRING : '"' (ESC | ~["\\])* '"' ;

fragment ESC : '\\' (["\\/bfnrt] | UNICODE) ;
fragment UNICODE : 'u' HEX HEX HEX HEX ;
fragment HEX : [0-9a-fA-F] ;

NUMBER
    : '-'? INT '.' INT EXP?
    | '-'? INT EXP
    | '-'? INT ;

fragment INT :  '0' | [1-9] [0-9]* ; // no leading zeros
fragment EXP :  [Ee] [+\-]? INT ; // \- since - means "range" inside [...]

WS  :   [ \t\n\r]+ -> skip ;