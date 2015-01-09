/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.rfsc.clojure;

import clojure.lang.*;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.regex.Pattern;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 5/13/14
 *         Time: 2:29 PM
 */
public class ReadMacro {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadMacro.class);
    private static final Object EOF = new Object();

    @Test
    public void varAbbr() {
        String var = "#'a";
        Object obj = read(var);
        LOGGER.info("{} => {}", var, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("var"), ((Cons) obj).first());
        Assert.assertEquals(Symbol.intern("a"), ((Cons) obj).next().first());
        Assert.assertNull(((Cons) obj).next().next());
    }

    @Test
    public void patternAbbr() {
        String pattern = ".+?";
        Object obj = read("#\"" + pattern + "\"");
        LOGGER.info("{} => {}", pattern, obj);
        Assert.assertTrue(obj instanceof Pattern);
        Assert.assertEquals(pattern, ((Pattern) obj).pattern());
    }

    @Test
    public void fnAbbr() {
        String fn = "#(+ %1 %2)";
        Object obj = read(fn);
        LOGGER.info("{} => {}", fn, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(3, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("fn*"), ((Cons) obj).first());

        Assert.assertEquals(2, ((Cons) obj).next().count());
        Assert.assertTrue(((Cons) obj).next().first() instanceof PersistentVector);
        Assert.assertEquals(2, ((PersistentVector)((Cons) obj).next().first()).count());

        Assert.assertEquals(3, ((PersistentList) ((Cons) obj).next().next().first()).count());
    }

    @Test
    public void setAbbr() {
        String set = "#{1 2 3}";
        Object obj = read(set);
        LOGGER.info("{} => {}", set, obj);
        Assert.assertTrue(obj instanceof PersistentHashSet);
        Assert.assertEquals(3, ((PersistentHashSet) obj).count());
    }

    @Test
    public void evalAbbr() {
        String eval = "#=java.lang.String";
        Object obj = read(eval);
        LOGGER.info("{} => {}", eval, obj);
        Assert.assertTrue(obj instanceof Class);
        Assert.assertEquals(String.class, obj);
    }

    @Test
    public void quote(){
        String quote = "'fn";
        Object obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("quote"), ((Cons) obj).first());
        Assert.assertEquals(Symbol.intern("fn"), ((Cons) obj).next().first());
        Assert.assertNull(((Cons) obj).next().next());

        quote = "'(1 2 3)";
        obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("quote"), ((Cons) obj).first());
        Assert.assertEquals("(1 2 3)", ((Cons) obj).next().first().toString());
        Assert.assertNull(((Cons) obj).next().next());
    }

    @Test
    public void deref(){
        String deref = "@fn";
        Object obj = read(deref);
        LOGGER.info("{} => {}", deref, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("clojure.core/deref"), ((Cons) obj).first());
        Assert.assertEquals(Symbol.intern("fn"), ((Cons) obj).next().first());
        Assert.assertNull(((Cons) obj).next().next());
    }

    @Test
    public void meta(){
        String meta = "^{:macro true} hello";
        Object obj = read(meta);
        LOGGER.info("{} => {}", meta, obj);
        Assert.assertTrue(obj instanceof Symbol);
        Assert.assertEquals(1, ((Symbol) obj).meta().count());
        Assert.assertEquals(true, ((Symbol) obj).meta().containsKey(Keyword.intern("macro")));
        Assert.assertEquals(true, ((Symbol) obj).meta().entryAt(Keyword.intern("macro")).getValue());

        meta = "^:dynamic hello";
        obj = read(meta);
        LOGGER.info("{} => {}", meta, obj);
        Assert.assertTrue(obj instanceof Symbol);
        Assert.assertEquals(1, ((Symbol) obj).meta().count());
        Assert.assertEquals(true, ((Symbol) obj).meta().containsKey(Keyword.intern("dynamic")));
        Assert.assertEquals(true, ((Symbol) obj).meta().entryAt(Keyword.intern("dynamic")).getValue());

        meta = "^String hello";
        obj = read(meta);
        LOGGER.info("{} => {}", meta, obj);
        Assert.assertTrue(obj instanceof Symbol);
        Assert.assertEquals(1, ((Symbol) obj).meta().count());
        Assert.assertEquals(true, ((Symbol) obj).meta().containsKey(Keyword.intern("tag")));
        Assert.assertEquals(Symbol.intern("String"), ((Symbol) obj).meta().entryAt(Keyword.intern("tag")).getValue());

        meta = "^{:tag java.lang.String} hello";
        obj = read(meta);
        LOGGER.info("{} => {}", meta, obj);
        Assert.assertTrue(obj instanceof Symbol);
        Assert.assertEquals(1, ((Symbol) obj).meta().count());
        Assert.assertEquals(true, ((Symbol) obj).meta().containsKey(Keyword.intern("tag")));
        Assert.assertEquals(Symbol.intern("java.lang.String"), ((Symbol) obj).meta().entryAt(Keyword.intern("tag")).getValue());
    }

    @Test
    public void syntaxQuote(){
        String quote = "`fn";
        Object obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("quote"), ((Cons) obj).first());
        Assert.assertEquals(Symbol.intern("clojure.core/fn"), ((Cons) obj).next().first());
        Assert.assertNull(((Cons) obj).next().next());

        quote = "`.";
        obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("quote"), ((Cons) obj).first());
        Assert.assertEquals(Symbol.intern("."), ((Cons) obj).next().first());
        Assert.assertNull(((Cons) obj).next().next());

        quote = "`.intern";
        obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("quote"), ((Cons) obj).first());
        Assert.assertEquals(Symbol.intern(".intern"), ((Cons) obj).next().first());
        Assert.assertNull(((Cons) obj).next().next());

        quote = "`[:a :b]";
        obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(3, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("clojure.core/apply"), ((Cons) obj).first());
        Assert.assertEquals(Symbol.intern("clojure.core/vector"), ((Cons) obj).next().first());
        Assert.assertTrue(((Cons) obj).next().next().first() instanceof Cons);

        quote = "`{:macro true}";
        obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(3, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("clojure.core/apply"), ((Cons) obj).first());
        Assert.assertEquals(Symbol.intern("clojure.core/hash-map"), ((Cons) obj).next().first());
        Assert.assertTrue(((Cons) obj).next().next().first() instanceof Cons);

        quote = "`#{:a :b}";
        obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(3, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("clojure.core/apply"), ((Cons) obj).first());
        Assert.assertEquals(Symbol.intern("clojure.core/hash-set"), ((Cons) obj).next().first());
        Assert.assertTrue(((Cons) obj).next().next().first() instanceof Cons);

        quote = "`(source fn)";
        obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("clojure.core/seq"), ((Cons) obj).first());
        Assert.assertEquals(Cons.class, ((Cons) obj).next().first().getClass());

        quote = "`(. System getProperties)";
        obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("clojure.core/seq"), ((Cons) obj).first());
        Assert.assertEquals(Cons.class, ((Cons) obj).next().first().getClass());

        quote = "`(.getProperties System)";
        obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("clojure.core/seq"), ((Cons) obj).first());
        Assert.assertEquals(Cons.class, ((Cons) obj).next().first().getClass());

        quote = "`(System/getProperties)";
        obj = read(quote);
        LOGGER.info("{} => {}", quote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("clojure.core/seq"), ((Cons) obj).first());
        Assert.assertEquals(Cons.class, ((Cons) obj).next().first().getClass());
    }

    @Test
    public void unquote(){
        String unquote = "~fn";
        Object obj = read(unquote);
        LOGGER.info("{} => {}", unquote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("clojure.core/unquote"), ((Cons) obj).first());
        Assert.assertEquals(Symbol.intern("fn"), ((Cons) obj).next().first());
        Assert.assertNull(((Cons) obj).next().next());

        unquote = "~(list 1 2 3)";
        obj = read(unquote);
        LOGGER.info("{} => {}", unquote, obj);
        Assert.assertTrue(obj instanceof Cons);
        Assert.assertEquals(2, ((Cons) obj).count());
        Assert.assertEquals(Symbol.intern("clojure.core/unquote"), ((Cons) obj).first());
        Assert.assertEquals("(list 1 2 3)", ((Cons) obj).next().first().toString());
        Assert.assertNull(((Cons) obj).next().next());
    }

    private static Object read(String expr) {
        return LispReader.read(new PushbackReader(new StringReader(expr)), false, EOF, false);
    }
}
