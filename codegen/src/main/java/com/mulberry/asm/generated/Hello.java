
package com.mulberry.asm.generated;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/16/14
 *         Time: 11:16 PM
 */
public class Hello {

    final static Method voidctor = Method.getMethod("void <init>()");

    public static void main(String[] args) throws IOException{
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
        cv.visit(V1_5, ACC_PUBLIC + ACC_SUPER + ACC_FINAL, "HelloWorld", null, "java.lang.Object", new String[]{
                "java.io.Serializable"});

        GeneratorAdapter clinitgen = new GeneratorAdapter(ACC_PUBLIC + ACC_STATIC,
                Method.getMethod("void <clinit> ()"), null, null, cv);
        clinitgen.visitCode();
        clinitgen.visitLineNumber(1, clinitgen.mark());
        clinitgen.returnValue();
        clinitgen.endMethod();

        Method m = new Method("<init>", Type.VOID_TYPE, ctorTypes());
        GeneratorAdapter ctorgen = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cv);
        Label start = ctorgen.newLabel();
        Label end = ctorgen.newLabel();
        ctorgen.visitCode();
        ctorgen.visitLineNumber(2, ctorgen.mark());
        ctorgen.visitLabel(start);
        ctorgen.loadThis();
        ctorgen.invokeConstructor(Type.getObjectType("java.lang.Object"), voidctor);
        ctorgen.visitLabel(end);
        ctorgen.returnValue();
        ctorgen.endMethod();

        //end of class
        cv.visitEnd();

        byte[] bytes = cw.toByteArray();
        System.out.println(Arrays.toString(bytes));
        //writeClassFile("HelloWorld", bytecode);
    }


    static Type[] ctorTypes() {
        return new Type[0];
    }

    static public void writeClassFile(String internalName, byte[] bytecode) throws IOException {
        String genPath = "/tmp";
        if (genPath == null)
            throw new RuntimeException("*compile-path* not set");
        String[] dirs = internalName.split("/");
        String p = genPath;
        for (int i = 0; i < dirs.length - 1; i++) {
            p += File.separator + dirs[i];
            (new File(p)).mkdir();
        }
        String path = genPath + File.separator + internalName + ".class";
        File cf = new File(path);
        cf.createNewFile();
        FileOutputStream cfs = new FileOutputStream(cf);
        try {
            cfs.write(bytecode);
            cfs.flush();
            cfs.getFD().sync();
        } finally {
            cfs.close();
        }
    }


}
