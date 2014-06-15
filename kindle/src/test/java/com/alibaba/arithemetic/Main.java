/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.arithemetic;

import com.alibaba.arithmetic.ArithmeticLexer;
import com.alibaba.arithmetic.ArithmeticParser;
import com.alibaba.csv.CSVBaseListener;
import com.alibaba.csv.CSVLexer;
import com.alibaba.csv.CSVParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.antlr.v4.runtime.tree.TerminalNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/14/14
 *         Time: 6:16 PM
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ParseTreeWalker walker = new ParseTreeWalker();
        Loader loader = new Loader();

        CSVLexer lexer = new CSVLexer(new ANTLRInputStream("1, 2, 3, 4, 5\n5, 4, 3, 2, 1"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CSVParser parser = new CSVParser(tokens);
        ParseTree tree = parser.file().getChild(0);
        //walker.walk(walker, tree);
    }

    static class Loader extends CSVBaseListener {
        public static final String EMPTY = "";
        /** Load a list of row maps that map field name to value */
        List<Map<String,String>> rows = new ArrayList<Map<String, String>>(); /** List of column names */
        List<String> header;
        /** Build up a list of fields in current row */
        List<String> currentRowFieldValues;

        @Override
        public void enterField(@NotNull CSVParser.FieldContext ctx) {
            System.out.println("enterField" + ctx);
            super.enterField(ctx);
        }

        @Override
        public void exitField(@NotNull CSVParser.FieldContext ctx) {
            System.out.println("exitField" + ctx);
            super.exitField(ctx);
        }

        @Override
        public void enterHdr(@NotNull CSVParser.HdrContext ctx) {
            System.out.println("enterHdr" + ctx);
            super.enterHdr(ctx);
        }

        @Override
        public void exitHdr(@NotNull CSVParser.HdrContext ctx) {
            System.out.println("exitHdr" + ctx);
            super.exitHdr(ctx);
        }

        @Override
        public void enterFile(@NotNull CSVParser.FileContext ctx) {
            System.out.println("enterFile" + ctx);
            super.enterFile(ctx);
        }

        @Override
        public void exitFile(@NotNull CSVParser.FileContext ctx) {
            System.out.println("exitFile" + ctx);
            super.exitFile(ctx);
        }

        @Override
        public void enterRow(@NotNull CSVParser.RowContext ctx) {
            System.out.println("enterRow" + ctx);
            super.enterRow(ctx);
        }

        @Override
        public void exitRow(@NotNull CSVParser.RowContext ctx) {
            System.out.println("exitRow" + ctx);
            super.exitRow(ctx);
        }

        @Override
        public void enterEveryRule(@NotNull ParserRuleContext ctx) {
            System.out.println("enterEveryRule" + ctx);
            super.enterEveryRule(ctx);
        }

        @Override
        public void exitEveryRule(@NotNull ParserRuleContext ctx) {
            System.out.println("exitEveryRule" + ctx);
            super.exitEveryRule(ctx);
        }

        @Override
        public void visitTerminal(@NotNull TerminalNode node) {
            System.out.println("visitTerminal" + node);
            super.visitTerminal(node);
        }

        @Override
        public void visitErrorNode(@NotNull ErrorNode node) {
            System.out.println("visitErrorNode" + node);
            super.visitErrorNode(node);
        }
    }




}
