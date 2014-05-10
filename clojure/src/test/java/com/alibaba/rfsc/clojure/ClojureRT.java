/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.rfsc.clojure;

import clojure.lang.RT;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 5/9/14
 *         Time: 11:21 PM
 */
public class ClojureRT {


    @Test
    public void list(){
        Assert.assertEquals(null, RT.list());
        Assert.assertEquals("(1)", RT.list(1).toString());
        Assert.assertEquals("([1 2 3])", RT.list(Arrays.asList(1, 2, 3)).toString());
        Assert.assertEquals("(#{1 2 3})", RT.list(ImmutableSet.of(1, 2, 3)).toString());
        Assert.assertEquals("({\"a\" 1, \"b\" 2})", RT.list(ImmutableMap.of("a", 1, "b", 2)).toString());


        Assert.assertEquals("(1 2)", RT.list(1, 2).toString());
    }

}
