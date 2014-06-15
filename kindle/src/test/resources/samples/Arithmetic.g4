grammar Arithmetic ;

@header {
    //package com.alibaba.arithmetic;
    import java.util.*;
}

@parser::members {
    Map<String, Integer> memory = new HashMap<String, Integer>();
    int eval(int left, int op, int right) {
        switch ( op ) {
            case MUL : return left * right;
            case DIV : return left / right;
            case ADD : return left + right;
            case SUB : return left - right;
        }
        return 0;
    }
}


options {

}

prog: stat+ ;

stat
    : expr NEWLINE          { System.out.println($expr.v); }
    | ID '=' expr NEWLINE   { memory.put($ID.text, $expr.v); }
    | NEWLINE ;

expr: expr ('*' | '/') expr | expr ('+' | '-') expr | INT | ID | '(' expr ')' ;

ID: [a-zA-Z][a-zA-Z0-9]* ;
INT: [1-9][0-9]* ;
NEWLINE: '\r'? '\n' ;
WS: [ \t]+ -> skip ;


MUL :   '*' ;
DIV :   '/' ;
ADD :   '+' ;
SUB :   '-' ;


