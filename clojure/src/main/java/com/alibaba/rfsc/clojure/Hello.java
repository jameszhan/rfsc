/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.rfsc.clojure;

import clojure.java.api.Clojure;
import clojure.lang.RT;
import clojure.lang.Symbol;
import clojure.lang.Var;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 5/8/14
 *         Time: 8:34 PM
 */
public class Hello {

    public static void main(String[] args) {
        //RT.var("clojure.core", "require").invoke(Symbol.intern("foo.ns"));
        //RT.var("foo.ns", "bar-fn").invoke(1, 2, 3);
        System.out.println(Clojure.var("clojure.core", "+").invoke(1, 2));

        Var var = (Var)Clojure.var("clojure.core/fn*");
        System.out.println(var);
    }

}
