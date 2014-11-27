package com.mulberry.athena.asm;

import com.google.common.base.Joiner;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Created with IntelliJ IDEA.
 *
 * @author James Zhan
 *         Date: 11/18/14
 *         Time: 5:10 PM
 */
public class DebugClassVisitor {


    public static void main(String[] args) throws IOException {
        Class<?> clazz = DebugClassVisitor.class;
        Enhancer enhancer = new Enhancer();

        enhancer.setSuperclass(ClassVisitor.class);
        enhancer.setCallback(new ClassVisitorCallback());
        ClassVisitor classVisitor = (ClassVisitor)enhancer.create(new Class[]{Integer.TYPE}, new Object[]{Opcodes.ASM5});
        new ClassReader(clazz.getResourceAsStream(clazz.getSimpleName() + ".class")).accept(classVisitor, 0);
    }


    static class ClassVisitorCallback implements MethodInterceptor {

        @Override public Object intercept(Object obj, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
            if (method.getName().startsWith("visit")) {
                System.out.println(obj);
                System.out.println(method);
                System.out.println(Joiner.on(", ").useForNull("NULL").join(arguments));
                System.out.println(methodProxy);
                System.out.println();
            }
            return methodProxy.invokeSuper(obj, arguments);
        }
    }



}
