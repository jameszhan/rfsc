/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.asm.test;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/17/14
 *         Time: 4:46 PM
 */
@Resource
@WebService
public class Hello<T> extends ArrayList<String> implements Cloneable, Serializable {

    @Resource
    public static final String A = "a";
    @Resource
    private final String b = "b";

    static {
        System.out.println("Test the asm handle the class");
    }

    {
        System.out.println("Test the asm handle the class");
    }

    public Hello() {
        System.out.println("Test the asm handle the class");
    }

    @WebMethod
    public String index(String name, int count) throws IOException{
        return String.format("%d: hello %s", count, name);
    }

    @WebMethod
    public Integer[] hello() throws IOException{
        return new Integer[0];
    }

    @WebMethod
    public int[] world() throws IOException, RuntimeException {
        return new int[0];
    }

    public T hi() {
        return null;
    }


    public static class World1 {

    }

    public class World2 {

    }

    final class World3 {

    }

    static class World4 {

    }

    class World5{

    }
}
