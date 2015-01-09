/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.rfsc.clojure;

import clojure.lang.*;
import clojure.lang.Compiler;
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
public class Analyze {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadMacro.class);
    private static final Object EOF = new Object();

    @Test
    public void withTag(){
        withTagTest(Compiler.C.EXPRESSION);
        withTagTest(Compiler.C.EVAL);
        withTagTest(Compiler.C.RETURN);
        withTagTest(Compiler.C.STATEMENT);
    }

    private void withTagTest(Compiler.C type) {
        String exprString = "^int (+ 1 2)";
        Compiler.Expr expr = Compiler.analyze(type, read(exprString));
        LOGGER.info("{} => {}", exprString, expr);

        Assert.assertEquals(3l, expr.eval());
        Assert.assertTrue(expr.hasJavaClass());
        Assert.assertEquals(int.class, expr.getJavaClass());

        Assert.assertTrue(expr instanceof Compiler.StaticMethodExpr);
        Compiler.StaticMethodExpr expression = (Compiler.StaticMethodExpr)expr;

        Assert.assertEquals("NO_SOURCE_FILE", expression.source);
        Assert.assertEquals(0, expression.column);
        Assert.assertEquals(0, expression.line);
        Assert.assertEquals(Symbol.intern("int"), expression.tag);
        Assert.assertEquals(Numbers.class, expression.c);
        Assert.assertEquals("add", expression.methodName);
        Assert.assertEquals(2, expression.args.count());
        Assert.assertEquals("add", expression.method.getName());
        Assert.assertEquals(long.class, expression.method.getReturnType());
        Assert.assertArrayEquals(new Class<?>[]{long.class, long.class}, expression.method.getParameterTypes());
    }

    @Test
    public void staticMethod(){
        staticMethodTest(Compiler.C.EXPRESSION);
        staticMethodTest(Compiler.C.EVAL);
        staticMethodTest(Compiler.C.RETURN);
        staticMethodTest(Compiler.C.STATEMENT);
    }

    private void staticMethodTest(Compiler.C type){
        String exprString = "(. clojure.lang.Symbol intern \"a\" \"b\")";
        Compiler.Expr expr = Compiler.analyze(type, read(exprString));
        LOGGER.info("{} => {}", exprString, expr);

        Assert.assertEquals(Symbol.intern("a", "b"), expr.eval());
        Assert.assertTrue(expr.hasJavaClass());
        Assert.assertEquals(Symbol.class, expr.getJavaClass());

        Assert.assertTrue(expr instanceof Compiler.StaticMethodExpr);
        Compiler.StaticMethodExpr expression = (Compiler.StaticMethodExpr)expr;

        Assert.assertEquals("NO_SOURCE_FILE", expression.source);
        Assert.assertEquals(0, expression.column);
        Assert.assertEquals(0, expression.line);
        Assert.assertEquals(null, expression.tag);
        Assert.assertEquals(Symbol.class, expression.c);
        Assert.assertEquals("intern", expression.methodName);
        Assert.assertEquals(2, expression.args.count());
        Assert.assertEquals("intern", expression.method.getName());
        Assert.assertEquals(Symbol.class, expression.method.getReturnType());
        Assert.assertArrayEquals(new Class<?>[]{String.class, String.class}, expression.method.getParameterTypes());
    }

    @Test
    public void instanceMethod(){
        instanceMethodTest(Compiler.C.EXPRESSION);
        instanceMethodTest(Compiler.C.EVAL);
        instanceMethodTest(Compiler.C.RETURN);
        instanceMethodTest(Compiler.C.STATEMENT);
    }

    private void instanceMethodTest(Compiler.C type){
        String exprString = "(.substring \"hello world\" 2 5)";
        Compiler.Expr expr = Compiler.analyze(type, read(exprString));
        LOGGER.info("{} => {}", exprString, expr);

        Assert.assertEquals("llo", expr.eval());
        Assert.assertTrue(expr.hasJavaClass());
        Assert.assertEquals(String.class, expr.getJavaClass());

        Assert.assertTrue(expr instanceof Compiler.InstanceMethodExpr);
        Compiler.InstanceMethodExpr expression = (Compiler.InstanceMethodExpr)expr;

        Assert.assertEquals("NO_SOURCE_FILE", expression.source);
        Assert.assertEquals(0, expression.column);
        Assert.assertEquals(0, expression.line);
        Assert.assertEquals(null, expression.tag);
        Assert.assertTrue(expression.target instanceof Compiler.StringExpr);
        Assert.assertEquals("substring", expression.methodName);
        Assert.assertEquals(2, expression.args.count());
        Assert.assertEquals("substring", expression.method.getName());
        Assert.assertEquals(String.class, expression.method.getReturnType());
        Assert.assertArrayEquals(new Class<?>[]{int.class, int.class}, expression.method.getParameterTypes());
    }

    @Test
    public void def(){
        defTest(Compiler.C.EXPRESSION);
        defTest(Compiler.C.EVAL);
        defTest(Compiler.C.RETURN);
        defTest(Compiler.C.STATEMENT);
    }

    private void defTest(Compiler.C type){
        Var.pushThreadBindings(RT.map(Compiler.LOADER, RT.makeClassLoader()));
        try {
            String exprString = "(def ^:dynamic hello (fn* add1 ([x] (+ x 1))))";
            Compiler.Expr expr = Compiler.analyze(type, read(exprString));
            LOGGER.info("{} => {}", exprString, expr);

            Symbol hello = Symbol.intern("clojure.core", "hello");
            Assert.assertEquals(Var.find(hello), expr.eval());
            Assert.assertTrue(expr.hasJavaClass());
            Assert.assertEquals(Var.class, expr.getJavaClass());

            Assert.assertTrue(expr instanceof Compiler.DefExpr);
            Compiler.DefExpr expression = (Compiler.DefExpr)expr;

            Assert.assertEquals("NO_SOURCE_FILE", expression.source);
            Assert.assertEquals(0, expression.column);
            Assert.assertEquals(0, expression.line);
            Assert.assertEquals(Var.find(hello), expression.var);

            Assert.assertTrue(expression.initProvided);
            Assert.assertTrue(expression.isDynamic);
            Assert.assertTrue(expression.init instanceof Compiler.FnExpr);

            Assert.assertEquals("clojure.core$add1", ((Compiler.FnExpr) expression.init).name());
            Assert.assertEquals("clojure/core$add1", ((Compiler.FnExpr) expression.init).internalName());
            Assert.assertEquals("add1", ((Compiler.FnExpr) expression.init).thisName());
            Assert.assertArrayEquals(PersistentVector.create(Var.find(Symbol.intern("clojure.core", "+")), 1l).toArray(), ((Compiler.FnExpr) expression.init).constants().toArray());
            Assert.assertEquals(AFunction.class, expression.init.getJavaClass());
        } finally {
            Var.popThreadBindings();
        }
    }

    @Test
    public void assign(){
        assignTest(Compiler.C.EXPRESSION);
        assignTest(Compiler.C.EVAL);
        assignTest(Compiler.C.RETURN);
        assignTest(Compiler.C.STATEMENT);
    }

    private void assignTest(Compiler.C type){
        String exprString = "(set! clojure.lang.Agent/soloExecutor nil)";
        Compiler.Expr expr = Compiler.analyze(type, read(exprString));
        LOGGER.info("{} => {}", exprString, expr);

        Symbol hello = Symbol.intern("clojure.core", "hello");
        Assert.assertEquals(Var.find(hello), expr.eval());
        Assert.assertTrue(expr.hasJavaClass());
        Assert.assertEquals(null, expr.getJavaClass());

        Assert.assertTrue(expr instanceof Compiler.AssignExpr);
        Compiler.AssignExpr expression = (Compiler.AssignExpr)expr;

        Assert.assertEquals(Compiler.StaticFieldExpr.class, expression.target.getClass());
        Assert.assertEquals(null, expression.val.eval());
    }

    @Test
    public void let(){
        //letTest(Compiler.C.EXPRESSION);
        letTest(Compiler.C.EVAL);
        //letTest(Compiler.C.RETURN);
        //letTest(Compiler.C.STATEMENT);
    }

    private void letTest(Compiler.C type){
        Var.pushThreadBindings(RT.map(Compiler.LOADER, RT.makeClassLoader()));
        try {
            String exprString = "(let* [i 3] (.substring \"hello world\", i, 8))";
            Compiler.Expr expr = Compiler.analyze(type, read(exprString));
            LOGGER.info("{} => {}", exprString, expr);

            Assert.assertEquals("lo wo", expr.eval());
            Assert.assertFalse(expr.hasJavaClass());

            Assert.assertTrue(expr instanceof Compiler.InvokeExpr);
            Compiler.InvokeExpr expression = (Compiler.InvokeExpr)expr;

            Assert.assertEquals(null, expression.tag);
            Assert.assertEquals(null, expression.onMethod);
            Assert.assertEquals(null, expression.protocolOn);
            Assert.assertEquals("NO_SOURCE_FILE", expression.source);
            Assert.assertEquals(PersistentVector.create(), expression.args);

            Compiler.FnExpr fexpr = (Compiler.FnExpr)expression.fexpr;

            Assert.assertTrue(fexpr.name().startsWith("clojure.core$fn__"));
            Assert.assertTrue(fexpr.internalName().startsWith("clojure/core$fn__"));
            Assert.assertEquals(null, fexpr.thisName());
            Assert.assertArrayEquals(PersistentVector.create(3l, 8l).toArray(), fexpr.constants().toArray());

            AFunction fn = (AFunction) fexpr.eval();
            Assert.assertEquals("lo wo", fn.invoke());
        } finally {
            Var.popThreadBindings();
        }
    }


    private static Object read(String expr) {
        return LispReader.read(new PushbackReader(new StringReader(expr)), false, EOF, false);
    }
}
