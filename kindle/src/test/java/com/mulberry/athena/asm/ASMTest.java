/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.asm;

import com.mulberry.athena.compile.DynamicClassLoader;
import org.junit.Test;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintStream;
import java.io.PrintWriter;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/18/14
 *         Time: 12:17 AM
 */
public class ASMTest {

    public final static Method voidctor = Method.getMethod("void <init>()");
    public final static Method clinit = Method.getMethod("void <clinit>()");

    @Test
    public void generateClassUseClassVisitor() throws Exception {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
        cv.visit(V1_6, ACC_PUBLIC + ACC_SUPER, "Hello", null, "java/lang/Object", null);
        {
            GeneratorAdapter clinitgen = new GeneratorAdapter(ACC_PUBLIC + ACC_STATIC, clinit, null, null, cv);
            clinitgen.visitCode();
            clinitgen.visitLineNumber(1, clinitgen.mark());
            clinitgen.returnValue();
            clinitgen.endMethod();
        }

        {
            GeneratorAdapter ctorgen = new GeneratorAdapter(ACC_PUBLIC, voidctor, null, null, cv);
            Label start = ctorgen.newLabel();
            Label end = ctorgen.newLabel();
            ctorgen.visitCode();
            ctorgen.visitLineNumber(2, ctorgen.mark());
            ctorgen.visitLabel(start);
            ctorgen.loadThis();
            ctorgen.invokeConstructor(Type.getObjectType("java/lang/Object"), voidctor);
            ctorgen.visitLabel(end);
            ctorgen.returnValue();
            ctorgen.endMethod();
        }
        {
            GeneratorAdapter gen = new GeneratorAdapter(ACC_PUBLIC,
                    Method.getMethod("int getRequiredArity()"),
                    null,
                    null,
                    cv);
            gen.visitCode();
            gen.push(3);
            gen.returnValue();
            gen.endMethod();
        }

        {
            Method method = Method.getMethod("void main(java/lang/String[])", true);
            GeneratorAdapter maingen = new GeneratorAdapter(ACC_PUBLIC + ACC_STATIC, method, null, null, cv);
            Label start = maingen.newLabel();
            maingen.visitCode();
            maingen.visitLineNumber(3, maingen.mark());
            maingen.visitLabel(start);
            maingen.getStatic(Type.getType("java/lang/System"), "out", Type.getType(PrintStream.class));
            maingen.push("IT WORKS!");
            maingen.invokeVirtual(Type.getType("java/io/PrintStream"), Method.getMethod("void println(java/lang/String)", true));
            Label end = maingen.newLabel();
            maingen.visitLabel(end);
            maingen.returnValue();
            maingen.endMethod();
        }

        cv.visitEnd();

        Class<?> clazz = new DynamicClassLoader().defineClass("Hello", cw.toByteArray());
        java.lang.reflect.Method method = clazz.getMethod("main", String[].class);
        final String[] arguments = new String[]{"hello", "world"};
        method.invoke(clazz, (Object) arguments);
    }


    @Test
    public void generateClass() throws Exception {
        ClassWriter classWriter = new ClassWriter(0);
        ClassVisitor cv = new TraceClassVisitor(classWriter, new PrintWriter(System.out));
        //FieldVisitor fv;
        MethodVisitor mv;
        //AnnotationVisitor av0;

        cv.visit(V1_6, ACC_PUBLIC + ACC_SUPER, "Testing", null, "java/lang/Object", null);
        cv.visitSource("Testing.java", null);
        {
            mv = cv.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(1, l0);
            mv.visitVarInsn(ALOAD, 0);
            mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
            mv.visitInsn(RETURN);
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLocalVariable("this", "LTesting;", null, l0, l1, 0);
            mv.visitMaxs(1, 1);
            mv.visitEnd();
        }
        {
            mv = cv.visitMethod(ACC_PUBLIC + ACC_STATIC, "main", "([Ljava/lang/String;)V", null, null);
            mv.visitCode();
            Label l0 = new Label();
            mv.visitLabel(l0);
            mv.visitLineNumber(3, l0);
            mv.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            mv.visitLdcInsn("Works!");
            mv.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
            Label l1 = new Label();
            mv.visitLabel(l1);
            mv.visitLineNumber(4, l1);
            mv.visitInsn(RETURN);
            Label l2 = new Label();
            mv.visitLabel(l2);
            mv.visitLocalVariable("args", "[Ljava/lang/String;", null, l0, l2, 0);
            mv.visitMaxs(2, 1);
            mv.visitEnd();
        }
        cv.visitEnd();

        Class<?> clazz = new DynamicClassLoader().defineClass("Testing", classWriter.toByteArray());
        java.lang.reflect.Method method = clazz.getMethod("main", String[].class);
        final String[] arguments = new String[]{"hello", "world"};
        method.invoke(clazz, (Object)arguments);
    }


    public static void main(String[] args) {
        System.out.println(Method.getMethod("void main([Ljava/lang/String;)"));
    }


}
