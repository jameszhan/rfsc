/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.rfsc.clojure;

import clojure.lang.*;
import clojure.lang.Compiler;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 5/16/14
 *         Time: 5:22 PM
 */
public class Expr {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadMacro.class);
    private static final Object EOF = new Object();

    @BeforeClass
    public static void loadRT() {
        RT.init();   //Load RT for testing
    }

    @Test
    public void exprForKeyword(){
        String expressionString = ":hello";
        Keyword keyword = (Keyword)read(expressionString);
        Compiler.KeywordExpr expr = Compiler.registerKeyword(keyword);
        LOGGER.info("{} => {}", expressionString, expr);
        Assert.assertTrue(Keyword.class.isAssignableFrom(expr.getJavaClass()));
        LOGGER.info("{k: {}, KEYWORDS: {}}", expr.k, Compiler.KEYWORDS);
        Assert.assertEquals(expr.k, expr.eval());
    }

    @Test
    public void exprForSymbol() {
        Symbol symbol = Symbol.intern("list");
        Compiler.Expr expr = Compiler.analyzeSymbol(symbol);
        LOGGER.info("{} => {}", "list", expr);
        Assert.assertTrue(expr instanceof Compiler.VarExpr);
        LOGGER.info("eval :{}", expr.eval());

        symbol = Symbol.intern("clojure.lang.Compiler/LOCAL_ENV");
        expr = Compiler.analyzeSymbol(symbol);
        LOGGER.info("{} => {}", "list", expr);
        Assert.assertTrue(expr instanceof Compiler.StaticFieldExpr);
        LOGGER.info("eval :{}", expr.eval());

        symbol = Symbol.intern("clojure.lang.Compiler");
        expr = Compiler.analyzeSymbol(symbol);
        LOGGER.info("{} => {}", "list", expr);
        Assert.assertTrue(expr instanceof Compiler.ConstantExpr);
        LOGGER.info("eval :{}", expr.eval());
    }

    @Test
    public void exprForVector(){
        String expressionString = "[:a 1 :b 2 :c 3]";
        IPersistentVector vector = (IPersistentVector)read(expressionString);
        Compiler.Expr expr = Compiler.VectorExpr.parse(Compiler.C.EXPRESSION, vector);
        LOGGER.info("{} => {}", expressionString, expr);
        Assert.assertTrue(expr instanceof Compiler.ConstantExpr);
        Compiler.ConstantExpr constantExpr = (Compiler.ConstantExpr)expr;
        Assert.assertTrue(APersistentVector.class.isAssignableFrom(expr.getJavaClass()));
        LOGGER.info("{id: {}, v: {}}", constantExpr.id, constantExpr.v);
        Assert.assertEquals(constantExpr.v, expr.eval());

        expressionString = "[1 (- 8 2) 2 (+ 1 2) 3 (/ 12 6)]";
        vector = (IPersistentVector)read(expressionString);
        expr = Compiler.VectorExpr.parse(Compiler.C.EVAL, vector);
        LOGGER.info("{} => {}", expressionString, expr);
        Assert.assertTrue(expr instanceof Compiler.VectorExpr);
        Compiler.VectorExpr vectorExpr = (Compiler.VectorExpr)expr;
        Assert.assertTrue(IPersistentVector.class.isAssignableFrom(expr.getJavaClass()));
        LOGGER.info("{args: {}, eval: {}}", vectorExpr.args, vectorExpr.eval());
        Assert.assertEquals(PersistentVector.EMPTY.cons(1l).cons(6l).cons(2l).cons(3l).cons(3l).cons(2l), vectorExpr.eval());
    }

    @Test
    public void exprForSet(){
        String expressionString = "#{:a :b :c}";
        IPersistentSet set = (IPersistentSet)read(expressionString);
        Compiler.Expr expr = Compiler.SetExpr.parse(Compiler.C.EXPRESSION, set);
        LOGGER.info("{} => {}", expressionString, expr);
        Assert.assertTrue(expr instanceof Compiler.ConstantExpr);
        Compiler.ConstantExpr constantExpr = (Compiler.ConstantExpr)expr;
        Assert.assertTrue(APersistentSet.class.isAssignableFrom(expr.getJavaClass()));
        LOGGER.info("{id: {}, v: {}}", constantExpr.id, constantExpr.v);
        Assert.assertEquals(constantExpr.v, expr.eval());

        expressionString = "#{(- 8 2) (+ 1 2) (/ 12 6)}";
        set = (IPersistentSet)read(expressionString);
        expr = Compiler.SetExpr.parse(Compiler.C.EVAL, set);
        LOGGER.info("{} => {}", expressionString, expr);
        Assert.assertTrue(expr instanceof Compiler.SetExpr);
        Compiler.SetExpr setExpr = (Compiler.SetExpr)expr;
        Assert.assertTrue(IPersistentSet.class.isAssignableFrom(expr.getJavaClass()));
        LOGGER.info("{keys: {}, eval: {}}", setExpr.keys, setExpr.eval());
        Assert.assertEquals(PersistentHashSet.EMPTY.cons(6l).cons(3l).cons(2l), setExpr.eval());
    }

    @Test
    public void exprForMap(){
        String expressionString = "{:a 1 :b 2 :c 3}";
        IPersistentMap map = (IPersistentMap)read(expressionString);
        Compiler.Expr expr = Compiler.MapExpr.parse(Compiler.C.EXPRESSION, map);
        LOGGER.info("{} => {}", expressionString, expr);
        Assert.assertTrue(expr instanceof Compiler.ConstantExpr);
        Compiler.ConstantExpr constantExpr = (Compiler.ConstantExpr)expr;
        Assert.assertTrue(APersistentMap.class.isAssignableFrom(expr.getJavaClass()));
        LOGGER.info("{id: {}, v: {}}", constantExpr.id, constantExpr.v);
        Assert.assertEquals(constantExpr.v, expr.eval());

        expressionString = "{1 (- 8 2) 2 (+ 1 2) 3 (/ 12 6)}";
        map = (IPersistentMap)read(expressionString);
        expr = Compiler.MapExpr.parse(Compiler.C.EVAL, map);
        LOGGER.info("{} => {}", expressionString, expr);
        Assert.assertTrue(expr instanceof Compiler.MapExpr);
        Compiler.MapExpr mapExpr = (Compiler.MapExpr)expr;
        Assert.assertTrue(IPersistentMap.class.isAssignableFrom(expr.getJavaClass()));
        LOGGER.info("{keyvals: {}, eval: {}}", mapExpr.keyvals, mapExpr.eval());
        Assert.assertEquals(PersistentHashMap.EMPTY.assoc(1l, 6l).assoc(2l, 3l).assoc(3l, 2l), mapExpr.eval());
    }


    @Test
    public void exprForFn(){
        Var.pushThreadBindings(RT.map(Compiler.LOADER, RT.makeClassLoader()));
        try {
            String expressionString = "(fn* [x] (+ x 1))";
            ISeq list = (ISeq)read(expressionString);
            Compiler.Expr expr = Compiler.FnExpr.parse(Compiler.C.EXPRESSION, list, "hello");
            LOGGER.info("{} => {}", expressionString, expr);
            Assert.assertEquals(AFunction.class, expr.getJavaClass());
            Assert.assertTrue(expr instanceof Compiler.FnExpr);
            Compiler.FnExpr fnExpr = (Compiler.FnExpr)expr;
            LOGGER.info("{}, {}, {}, {}, {}, {}, {}, {}, {}", fnExpr.thisName(), fnExpr.name(), fnExpr.internalName(),
                    fnExpr.vars(), fnExpr.constants(), fnExpr.closes(), fnExpr.keywords(), fnExpr.objtype(), fnExpr.variadicMethod());
            Assert.assertEquals("clojure.core$hello", fnExpr.name());
            Assert.assertEquals(1, fnExpr.methods().count());
            Assert.assertEquals(3l, ((AFunction)expr.eval()).invoke(2l));


            expressionString = "(fn* hello1 [&form &env & decl] (cons &form decl))";
            list = (ISeq)read(expressionString);
            expr = Compiler.FnExpr.parse(Compiler.C.EXPRESSION, list, "hello");
            LOGGER.info("{} => {}", expressionString, expr);
            Assert.assertEquals(AFunction.class, expr.getJavaClass());
            Assert.assertTrue(expr instanceof Compiler.FnExpr);
            fnExpr = (Compiler.FnExpr)expr;
            LOGGER.info("{}, {}, {}, {}, {}, {}, {}, {}, {}", fnExpr.thisName(), fnExpr.name(), fnExpr.internalName(),
                    fnExpr.vars(), fnExpr.constants(), fnExpr.closes(), fnExpr.keywords(), fnExpr.objtype(), fnExpr.variadicMethod());
            Assert.assertEquals("clojure.core$hello1", fnExpr.name());
            Assert.assertEquals(1, fnExpr.methods().count());
            Assert.assertEquals("(2 (3 1))", ((AFunction)expr.eval()).invoke(2, PersistentArrayMap.EMPTY, PersistentList.create(Arrays.asList(3, 1))).toString());


            expressionString = "(fn* hello2 ([x] (+ x 1)) ([x y] (+ x y 3)))";
            list = (ISeq)read(expressionString);
            expr = Compiler.FnExpr.parse(Compiler.C.EXPRESSION, list, "hello");
            LOGGER.info("{} => {}", expressionString, expr);
            Assert.assertEquals(AFunction.class, expr.getJavaClass());
            Assert.assertTrue(expr instanceof Compiler.FnExpr);
            fnExpr = (Compiler.FnExpr)expr;
            LOGGER.info("{}, {}, {}, {}, {}, {}, {}, {}, {}", fnExpr.thisName(), fnExpr.name(), fnExpr.internalName(),
                    fnExpr.vars(), fnExpr.constants(), fnExpr.closes(), fnExpr.keywords(), fnExpr.objtype(), fnExpr.variadicMethod());
            Assert.assertEquals("clojure.core$hello2", fnExpr.name());
            Assert.assertEquals(2, fnExpr.methods().count());
            Assert.assertEquals(3l, ((AFunction)expr.eval()).invoke(2l));
            Assert.assertEquals(8l, ((AFunction)expr.eval()).invoke(2l, 3l));
        } finally {
            Var.popThreadBindings();
        }
    }

    @Test
    public void exprForDef(){
        Compiler.IParser parser = (Compiler.IParser)Compiler.specials.valAt(Symbol.intern("def"));
        Var.pushThreadBindings(RT.map(Compiler.LOADER, RT.makeClassLoader()));
        try {
            String expressionString = "(def hello (fn [x] (+ x 1)))";
            ISeq list = (ISeq)read(expressionString);
            Compiler.Expr expr = parser.parse(Compiler.C.EVAL, list);
            LOGGER.info("{} => {}", expressionString, expr);
            Assert.assertEquals(Var.class, expr.getJavaClass());
            Assert.assertTrue(expr instanceof Compiler.DefExpr);
            Compiler.DefExpr defExpr = (Compiler.DefExpr)expr;
            LOGGER.info("{}, {}, {}, {}, {}, {}, {}, {}, {}", defExpr.meta, defExpr.var, defExpr.init,
                    defExpr.initProvided, defExpr.isDynamic, defExpr.source);
            Assert.assertEquals(3l, ((Var)expr.eval()).invoke(2l));
        } finally {
            Var.popThreadBindings();
        }
    }

    //@Test
    public void exprForLet(){
        Compiler.IParser parser = (Compiler.IParser)Compiler.specials.valAt(Symbol.intern("let*"));
        Var.pushThreadBindings(RT.map(Compiler.LOADER, RT.makeClassLoader()));
        try {
            String expressionString = "(let* [y 3] (+ y 6))";
            ISeq list = (ISeq)read(expressionString);
            Var.pushThreadBindings(RT.map(Compiler.LINE, 0, Compiler.COLUMN, 0));
            try {
                Compiler.Expr expr = parser.parse(Compiler.C.EVAL, list);
                LOGGER.info("{} => {}", expressionString, expr);
                Assert.assertEquals(IFn.class, expr.getJavaClass());
                Assert.assertTrue(expr instanceof Compiler.LetExpr);
                Compiler.LetExpr letExpr = (Compiler.LetExpr)expr;
                LOGGER.info("{}, {}, {}, {}, {}, {}, {}, {}, {}", letExpr.bindingInits, letExpr.body, letExpr.isLoop);
                Assert.assertEquals(9l, ((IFn)expr.eval()).invoke());
            } finally {
                Var.popThreadBindings();
            }
        } finally {
            Var.popThreadBindings();
        }
    }

    @Test
    public void exprForInvoker(){
        Var.pushThreadBindings(RT.map(Compiler.LOADER, RT.makeClassLoader()));
        try {
            String expressionString = "^int (+ 1 2)";
            ISeq list = (ISeq)read(expressionString);
            Var.pushThreadBindings(RT.map(Compiler.LINE, 0, Compiler.COLUMN, 0));
            try {
                Compiler.Expr expr = Compiler.InvokeExpr.parse(Compiler.C.EVAL, list);
                LOGGER.info("{} => {}", expressionString, expr);
                Assert.assertTrue(expr instanceof Compiler.InvokeExpr);
                Compiler.InvokeExpr invokeExpr = (Compiler.InvokeExpr)expr;
                LOGGER.info("{}, {}, {}, {}, {}, {}, {}, {}, {}", invokeExpr.args, invokeExpr.fexpr, invokeExpr.tag,
                        invokeExpr.isDirect, invokeExpr.isProtocol, invokeExpr.onMethod, invokeExpr.protocolOn);
                Assert.assertEquals(3l, expr.eval());
            } finally {
                Var.popThreadBindings();
            }
        } finally {
            Var.popThreadBindings();
        }
    }

    private static Object read(String expr) {
        return LispReader.read(new PushbackReader(new StringReader(expr)), false, EOF, false);
    }


}
