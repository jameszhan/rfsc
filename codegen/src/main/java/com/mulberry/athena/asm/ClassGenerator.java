
package com.mulberry.athena.asm;

import com.mulberry.athena.compile.DynamicClassLoader;
import org.objectweb.asm.*;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import static org.objectweb.asm.Opcodes.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/17/14
 *         Time: 3:27 PM
 */
public class ClassGenerator {

    private final static String generatedPath = "/tmp";


    public final static Method voidctor = Method.getMethod("void <init>()");
    public final static Method clinit = Method.getMethod("void <clinit>()");

    public static void generateClass(String name, String signature, String superClass, String[] interfaceNames) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
        cv.visit(V1_6, ACC_PUBLIC + ACC_SUPER + ACC_FINAL, name, signature, superClass, interfaceNames);

        generateClinit(cv);
        generateInit(cv, superClass, new Type[0]);

        cv.visitEnd();
        Class<?> clazz =new DynamicClassLoader().defineClass(name, cw.toByteArray());
        System.out.println(clazz);
    }

    public static void main(String[] args) throws Exception {
        generateClass("Hello", null, "java/lang/Object", null);
    }

    public static void generateClinit(ClassVisitor cv){
        GeneratorAdapter clinitgen = new GeneratorAdapter(ACC_PUBLIC + ACC_STATIC, clinit, null, null, cv);
        clinitgen.visitCode();
        clinitgen.visitLineNumber(1, clinitgen.mark());
        clinitgen.returnValue();
        clinitgen.endMethod();
    }

    public static void generateInit(ClassVisitor cv, String superName, Type[] argumentTypes){
        Method m = new Method("<init>", Type.VOID_TYPE, argumentTypes);
        GeneratorAdapter ctorgen = new GeneratorAdapter(ACC_PUBLIC, m, null, null, cv);
        Label start = ctorgen.newLabel();
        Label end = ctorgen.newLabel();
        ctorgen.visitCode();
        ctorgen.visitLineNumber(2, ctorgen.mark());
        ctorgen.visitLabel(start);
        ctorgen.loadThis();
        ctorgen.invokeConstructor(Type.getObjectType(superName), voidctor);
        ctorgen.visitLabel(end);
        ctorgen.returnValue();
        ctorgen.endMethod();
    }



    static public void writeClassFile(String internalName, byte[] bytecode) throws IOException {
        String generatedPath = "/tmp";
        String[] dirs = internalName.split("/");
        String p = generatedPath;
        for (int i = 0; i < dirs.length - 1; i++) {
            p += File.separator + dirs[i];
            (new File(p)).mkdir();
        }
        String path = generatedPath + File.separator + internalName + ".class";
        File cf = new File(path);
        if (cf.createNewFile()){
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
}
