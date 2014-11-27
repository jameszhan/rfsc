package com.mulberry.athena.asm;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 11/18/14
 *         Time: 6:06 PM
 */
public class ReadClassTests {

    static {
        System.out.println("Class <clinit>");
    }

    @BeforeClass
    public static void prepare(){
        System.out.println("Prepare");
    }

    @AfterClass
    public static void cleanup(){
        System.out.println("Clean up");
    }

    @Test
    public void traceClassVisitor() throws Exception {
        StringWriter out = new StringWriter();
        ClassVisitor classVisitor = new TraceClassVisitor(new PrintWriter(out));
        new ClassReader(currentClassStream()).accept(classVisitor, Opcodes.ASM5);
        System.out.println("\n========================================");
        System.out.println(out.toString());
        System.out.println("------------------------------------------\n");
    }

    //@Test
    public void traceDemoClass() throws Exception {
        StringWriter out = new StringWriter();
        ClassVisitor classVisitor = new TraceClassVisitor(new PrintWriter(out));
        new ClassReader(currentClassStream()).accept(classVisitor, Opcodes.ASM5);
        System.out.println("\n========================================");
        System.out.println(out.toString());
        System.out.println("------------------------------------------\n");
    }



    private InputStream currentClassStream() {
        Class<?> clazz = DemoClass.class;
        return clazz.getResourceAsStream(clazz.getSimpleName() + ".class");
    }

}
