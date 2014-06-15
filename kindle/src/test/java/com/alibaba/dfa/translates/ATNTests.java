/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.dfa.translates;

import org.antlr.v4.automata.ATNPrinter;
import org.antlr.v4.automata.LexerATNFactory;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ATNState;
import org.antlr.v4.tool.LexerGrammar;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/15/14
 *         Time: 2:22 AM
 */
public class ATNTests {
    /*
    private static final String GRAMMAR = "grammar Hello;\n"
            + "prog: stat+ ;\n"
            + "stat: expr NEWLINE | ID '=' expr NEWLINE | NEWLINE ;\n"
            + "expr: expr ('*' | '/') expr | expr ('+' | '-') expr | INT | ID | '(' expr ')' ;\n"
            + "ID: [a-zA-Z][a-zA-Z0-9]* ;\n"
            + "INT: [1-9][0-9]* ;\n"
            + "NEWLINE: '\r'? '\n' ;\n"
            + "WS: [ \t]+ -> skip ;";
    */

    //@Test
    public void start() throws Exception {
        LexerGrammar lexerGrammar = new LexerGrammar("grammar Arithmetic ;\n" +
                "prog: expr+ ;\n" +
                "expr: expr ('*' | '/') expr | expr ('+' | '-') expr | INT | ID | '(' expr ')' ;\n" +
                "ID: [a-zA-Z][a-zA-Z0-9]* ;\n" +
                "INT: [1-9][0-9]* ;\n" +
                "WS: [ \t]+ -> skip ;");

        System.out.println(lexerGrammar.modes);
        System.out.println(lexerGrammar.rules);
        LexerATNFactory factory = new LexerATNFactory(lexerGrammar);

        ATN atn = factory.createATN();

        for (ATNState state : atn.states) {
            ATNPrinter printer = new ATNPrinter(factory.g, state);
            System.out.println(printer.asString());
        }
    }


    //@Test
    public void rules() throws Exception {
        LexerGrammar lexerGrammar = new LexerGrammar("grammar Hello;\n" +
                "prog: call;\n" +
                "call: ID '(' args ')';\n" +
                "args: arg (',' arg)*;\n" +
                "arg: ID | INT;\n" +
                "ID: [a-zA-Z][a-zA-Z0-9]* ;\n" +
                "INT: [1-9][0-9]* ;\n" +
                "WS: [ \t]+ -> skip ;");

        System.out.println(lexerGrammar.modes);
        System.out.println(lexerGrammar.rules);
        LexerATNFactory factory = new LexerATNFactory(lexerGrammar);

        ATN atn = factory.createATN();

        for (ATNState state : atn.states) {
            ATNPrinter printer = new ATNPrinter(factory.g, state);
            System.out.println(printer.asString());
        }
    }

    public static final String CSV_ATN = "\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\7%\4\2\t\2\4\3\t"+
            "\3\4\4\t\4\4\5\t\5\3\2\3\2\6\2\r\n\2\r\2\16\2\16\3\3\3\3\3\4\3\4\3\4\7"+
            "\4\26\n\4\f\4\16\4\31\13\4\3\4\5\4\34\n\4\3\4\3\4\3\5\3\5\3\5\5\5#\n\5"+
            "\3\5\2\2\6\2\4\6\b\2\2%\2\n\3\2\2\2\4\20\3\2\2\2\6\22\3\2\2\2\b\"\3\2"+
            "\2\2\n\f\5\4\3\2\13\r\5\6\4\2\f\13\3\2\2\2\r\16\3\2\2\2\16\f\3\2\2\2\16"+
            "\17\3\2\2\2\17\3\3\2\2\2\20\21\5\6\4\2\21\5\3\2\2\2\22\27\5\b\5\2\23\24"+
            "\7\3\2\2\24\26\5\b\5\2\25\23\3\2\2\2\26\31\3\2\2\2\27\25\3\2\2\2\27\30"+
            "\3\2\2\2\30\33\3\2\2\2\31\27\3\2\2\2\32\34\7\5\2\2\33\32\3\2\2\2\33\34"+
            "\3\2\2\2\34\35\3\2\2\2\35\36\7\4\2\2\36\7\3\2\2\2\37#\7\6\2\2 #\7\7\2"+
            "\2!#\3\2\2\2\"\37\3\2\2\2\" \3\2\2\2\"!\3\2\2\2#\t\3\2\2\2\6\16\27\33"+
            "\"";

    @Test
    public void deserialize() {
        ATNDeserializer deserializer = new ATNDeserializer();
        ATN atn = deserializer.deserialize(CSV_ATN.toCharArray());
        System.out.println(atn);
    }

}
