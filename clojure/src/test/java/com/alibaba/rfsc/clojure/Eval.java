/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.rfsc.clojure;

import clojure.lang.Compiler;
import clojure.lang.LispReader;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PushbackReader;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 5/9/14
 *         Time: 11:21 PM
 */
public class Eval {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadMacro.class);
    private static final Object EOF = new Object();

    @Test
    public void analyze(){
        Compiler.Expr expr = Compiler.analyze(Compiler.C.EVAL, read("(+ 1 2)"));
        Assert.assertEquals(Compiler.StaticMethodExpr.class, expr.getClass());
        Assert.assertEquals(3l, expr.eval());
    }


    private static Object read(String expr) {
        return LispReader.read(new PushbackReader(new StringReader(expr)), false, EOF, false);
    }
}
