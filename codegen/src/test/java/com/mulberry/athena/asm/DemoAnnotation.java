package com.mulberry.athena.asm;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 12/9/14
 *         Time: 3:07 PM
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD })
@Documented
public @interface DemoAnnotation {

    String[] strings();

    int[] ints();

    long[] longs();

    ElementType type();

}
