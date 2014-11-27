package com.mulberry.athena.asm;

import com.google.common.annotations.GwtCompatible;

import javax.jws.WebService;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * javap -verbose com.mulberry.athena.asm.DemoClass
 * javap -c com.mulberry.athena.asm.DemoClass
 *
 * @author James Zhan
 *         Date: 11/19/14
 *         Time: 4:16 PM
 */
@GwtCompatible(serializable = true)
@WebService
@SuppressWarnings("unchecked")
public class DemoClass implements Serializable, Cloneable, Runnable, DemoInterface {

    public final static long CONSTANT = 1l;
    private static int s_variable;
    private short variable;

    static {
        s_variable = 100;
    }

    {
        variable = 1;
    }

    @Override public void run() {
        variable += 1;
    }

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public String hello(String message) {
        return String.format("Hello %s, this variable is %d, s_variable is %d!", message, variable, s_variable);
    }

    public static class A {

        public int add(int i, int j) {
            return i + j;
        }

    }

}
