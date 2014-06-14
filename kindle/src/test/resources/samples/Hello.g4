grammar Hello;
r       :   'Hello' ID;
ID      :   [a-zA-Z]+;
WS      :   [ \t\r\n]+ -> skip;