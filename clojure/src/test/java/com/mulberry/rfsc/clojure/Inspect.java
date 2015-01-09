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

import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 5/8/14
 *         Time: 10:18 PM
 */
public class Inspect {

    @BeforeClass
    public static void loadRT() {
        RT.init();   //Load RT for testing
    }

    //@Test
    public void var(){
        Var fn = Var.intern(Symbol.intern(null, "clojure.core"), Symbol.intern(null, "fn*"));
        Assert.assertEquals("#'clojure.core/fn*", fn.toString());
    }

    //@Test
    public void namespace() throws Exception {
        Field f = getField(Namespace.class, "namespaces");
        Assert.assertNotNull(f);
        ConcurrentHashMap<Symbol, Namespace> namespaces = (ConcurrentHashMap<Symbol, Namespace>)f.get(null);

        Symbol core = Symbol.intern(null, "clojure.core");
        Namespace ns = Namespace.find(core);
        Assert.assertEquals(ns, namespaces.get(core));

        Var plus = ns.intern(Symbol.intern(null, "+"));
        Long ret = (Long)plus.invoke(1, 2, 3, 4, 5);
        Assert.assertEquals(15, ret.intValue());

        for (Object entry : ns.getMappings()) {
            System.out.println(entry);
        }
    }

    //@Test
    public void fn(){
        Namespace ns = Namespace.find(Symbol.intern(null, "clojure.core"));
        Var fn = ns.intern(Symbol.intern("fn*"));
        System.out.println(fn.deref());
    }

    //@Test
    public void loadFn(){
        // Load the Clojure script -- as a side effect this initializes the runtime.
        String script = "(ns user) (defn foo [a b]   (str a \" \" b))";

        //RT.loadResourceScript("foo.clj");
        Compiler.load(new StringReader(script));

        // Get a reference to the foo function.
        Var foo = RT.var("user", "foo");

        // Call it!
        Object result = foo.invoke("Hi", "there");
        System.out.println(result);
    }

    @Test
    public void loadFnStar(){
        String script = "(ns user) (def\n" +
                "  ^{:macro true\n" +
                "    :added \"1.0\"}\n" +
                "  let1 (fn* let1 [&form &env & decl] (cons 'let* decl)))";
        //RT.loadResourceScript("foo.clj");
        Compiler.load(new StringReader(script));
        Var foo = RT.var("user", "let1");
        System.out.println(foo);
        System.out.println(Compiler.load(new StringReader("(ns user) (let1 [a 1] (println a))")));
    }

    //@Test
    public void loadFnStar1(){
        String script = "(fn* let1 [&form &env & decl] (cons 'let* decl))";
        //RT.loadResourceScript("foo.clj");
        Compiler.load(new StringReader(script));
        Var foo = RT.var("user", "let1");

    }


    private static Object getFieldValue(Object target, String fieldName) {
        Field f = getField(target.getClass(), fieldName);
        try {
            return f.get(target);
        } catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return null;
    }

    private static Field getField(Class<?> targetClass, String fieldName) {
        if (targetClass == null || fieldName == null){
            return null;
        }
        Class<?> clazz = targetClass;
        for (; clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field f = clazz.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
