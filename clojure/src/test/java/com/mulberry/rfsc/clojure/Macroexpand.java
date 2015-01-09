/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.rfsc.clojure;

import clojure.lang.LispReader;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import clojure.lang.Compiler;

import java.io.PushbackReader;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 5/14/14
 *         Time: 12:24 AM
 */
public class Macroexpand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadMacro.class);
    private static final Object EOF = new Object();

    @Test
    public void nonchangedMacroexpand1(){
        nonexpand1("fn");
        nonexpand1("'fn");
        nonexpand1("`fn");
        nonexpand1("~fn");
        nonexpand1("@fn");
        nonexpand1("~@fn");
        nonexpand1("[1 2 3]");
        nonexpand1("{:macro true}");
        nonexpand1("#{:a :b}");
        nonexpand1("(list 1 2 3)");
        nonexpand1("'(list 1 2 3)");
        nonexpand1("`(list 1 2 3)");
        nonexpand1("~(list 1 2 3)");
        nonexpand1("@(list 1 2 3)");
        nonexpand1("~@(list 1 2 3)");

        nonexpand1("(. clojure.lang.Symbol intern \"ns\" \"name\")");
        nonexpand1("(. clojure.lang.Symbol (intern \"ns\" \"name\"))");
        nonexpand1("(fn* [x] (println x))");
        nonexpand1("(fn* ([x] (println x)))");
        nonexpand1("(fn* name [x] (println x))");
        nonexpand1("(fn* name ([x] (println x)))");
        nonexpand1("(def hello (fn [x] (println x)))");
        nonexpand1("(let* [a 1] (println (+ a 2)))");
    }

    @Test
    public void macroexpand1(){
        doexpand1("(fn* ([x] (println x)))", "(fn [x] (println x))");
        doexpand1("(fn* hello ([x] (println x)))", "(fn hello [x] (println x))");
        doexpand1("(let* [a 1] (println a))", "(let [a 1] (println a))");
        doexpand1("(. clojure.lang.Symbol intern \"a\" \"b\")", "(clojure.lang.Symbol/intern \"a\" \"b\")");
        doexpand1("(. \"hello world\" substring \"a\" \"b\")", "(.substring \"hello world\" \"a\" \"b\")");
        doexpand1("(new String \"hello\")", "(String. \"hello\")");
    }

    @Test
    public void nonchangedMacroexpand(){
        nonexpand("fn");
        nonexpand("'fn");
        nonexpand("`fn");
        nonexpand("~fn");
        nonexpand("@fn");
        nonexpand("~@fn");
        nonexpand("[1 2 3]");
        nonexpand("{:macro true}");
        nonexpand("#{:a :b}");
        nonexpand("(list 1 2 3)");
        nonexpand("'(list 1 2 3)");
        nonexpand("`(list 1 2 3)");
        nonexpand("~(list 1 2 3)");
        nonexpand("@(list 1 2 3)");
        nonexpand("~@(list 1 2 3)");

        nonexpand("(. clojure.lang.Symbol intern \"ns\" \"name\")");
        nonexpand("(. clojure.lang.Symbol (intern \"ns\" \"name\"))");
        nonexpand("(fn* [x] (println x))");
        nonexpand("(fn* ([x] (println x)))");
        nonexpand("(fn* name [x] (println x))");
        nonexpand("(fn* name ([x] (println x)))");
        nonexpand("(def hello (fn [x] (println x)))");
        nonexpand("(let* [a 1] (println (+ a 2)))");
    }

    @Test
    public void macroexpand(){
        doexpand("(fn* ([x] (println x)))", "(fn [x] (println x))");
        doexpand("(fn* hello ([x] (println x)))", "(fn hello [x] (println x))");
        doexpand("(let* [a 1] (println a))", "(let [a 1] (println a))");
        doexpand("(. clojure.lang.Symbol intern \"a\" \"b\")", "(clojure.lang.Symbol/intern \"a\" \"b\")");
        doexpand("(. \"hello world\" substring \"a\" \"b\")", "(.substring \"hello world\" \"a\" \"b\")");
        doexpand("(new String \"hello\")", "(String. \"hello\")");
    }

    private void nonexpand1(String expr) {
        Object obj = read(expr);
        Object target = Compiler.macroexpand1(obj);
        LOGGER.info("{} => {}", expr, target);
        Assert.assertEquals(obj, target);
    }

    private void doexpand1(String expectedExpr, String expr) {
        Object obj = read(expr);
        Object target = Compiler.macroexpand1(obj);
        LOGGER.info("{} => {}", expr, target);

        Object obj2 = read(expectedExpr);
        Object target2 = Compiler.macroexpand1(obj2);
        LOGGER.info("{} => {}", expectedExpr, target2);

        Assert.assertEquals(target2, target);
    }

    private void nonexpand(String expr) {
        Object obj = read(expr);
        Object target = Compiler.macroexpand(obj);
        LOGGER.info("{} => {}", expr, target);
        Assert.assertEquals(obj, target);
    }

    private void doexpand(String expectedExpr, String expr) {
        Object obj = read(expr);
        Object target = Compiler.macroexpand(obj);
        LOGGER.info("{} => {}", expr, target);

        Object obj2 = read(expectedExpr);
        Object target2 = Compiler.macroexpand(obj2);
        LOGGER.info("{} => {}", expectedExpr, target2);

        Assert.assertEquals(target2, target);
    }

    private static Object read(String expr) {
        return LispReader.read(new PushbackReader(new StringReader(expr)), false, EOF, false);
    }
}
