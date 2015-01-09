/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.dfa.translates;

import com.mulberry.athena.AthenaLexer;
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

    @Test
    public void atnLexer() {
        ATN atn = new ATNDeserializer().deserialize(AthenaLexer._serializedATN.toCharArray());
        System.out.println(atn);
    }

}
