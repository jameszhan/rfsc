package com.mulberry.athena.asm;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import javax.jws.WebService;
import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.util.Collection;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;

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
@DemoAnnotation(strings = {"a", "b", "c", "d", "e"}, ints = {1, 2, 3}, longs = {}, type = ElementType.ANNOTATION_TYPE)
@GwtCompatible(serializable = true)
@WebService(name = "name", serviceName = "serviceName")
@SuppressWarnings("unchecked")
public class DemoClass<T extends Collection> implements Serializable, Cloneable, Runnable, DemoInterface {

    @Deprecated
    public final static long CONSTANT = 1l;
    @Deprecated
    private final static float FLOAT = 1.0f;
    private final static double DOUBLE = 2.0;
    private static int s_variable = 2;
    private List<List<List<?>>> s_list2;
    private List<List<List<T>>> s_list;
    private List<Map<?, T>> s_listmap;
    @Deprecated
    @DemoAnnotation(strings = {"a", "b", "c", "d", "e"}, ints = {1, 2, 3}, longs = {}, type = ElementType.ANNOTATION_TYPE)
    private static Map<? extends List<String>, ? super Map<String, Map<String, List<String>>>> LIST_MAP;
    private final static Map<? extends Number, ? super String> MAP = ImmutableMap.of(1l, "String", 2l, "Double");
    private short variable;

    private T hello_world;
    private List<String> list;
    private int[][] matrix;

    static {
        s_variable = 100;
    }

    {
        variable = 1;
    }

    DemoClass() {

    }

    public int plus(@JA int a, int b, @JA int c, int... args) {
        int sum = a + b + c;
        if (args != null) {
            for (int i : args) {
                sum += i;
            }
        }
        return sum;
    }

    public void aaa(T x) {

    }

    @Override public void run() {
        variable += 1;
    }

    @Override
    @Deprecated
    @DemoAnnotation(strings = {"a", "b", "c", "d", "e"}, ints = {1, 2, 3}, longs = {}, type = ElementType.ANNOTATION_TYPE)
    public int add(@JA int a, @JA int b) throws IllegalArgumentException {
        return a + b;
    }

    @Override
    @Deprecated
    @DemoAnnotation(strings = {"a", "b", "c", "d", "e"}, ints = {1, 2, 3}, longs = {}, type = ElementType.ANNOTATION_TYPE)
    public String hello(String message){
        return String.format("Hello %s, this variable is %d, s_variable is %d!", message, variable, s_variable);
    }

    void exceptions() throws IOException, IllegalArgumentException, WebServiceException {

    }

    public static class A {

        public int add(int i, int j) {
            return i + j;
        }

    }
}
