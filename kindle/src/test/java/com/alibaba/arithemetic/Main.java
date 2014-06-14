/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.arithemetic;

import com.alibaba.arithmetic.ArithmeticLexer;
import com.alibaba.arithmetic.ArithmeticParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/14/14
 *         Time: 6:16 PM
 */
public class Main {

    public static void main(String[] args) throws IOException {
        ANTLRInputStream input = new ANTLRInputStream("a = 1 + 1\n b = a + 2\n c = a + b\n");
        ArithmeticLexer lexer = new ArithmeticLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        ArithmeticParser parser = new ArithmeticParser(tokens);

        ParseTree parseTree = parser.prog();
        System.out.println(parseTree.toStringTree(parser));
    }

}
