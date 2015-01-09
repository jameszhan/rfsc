
package com.mulberry.asm.generated;

import org.objectweb.asm.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/17/14
 *         Time: 3:57 PM
 */
public class Main {

    public static void main(String[] args) throws Exception {
        File file = new File("./kindle/target/test-classes/com/alibaba/asm/test/Hello.class");
        //new ClassReader(new FileInputStream(file)).accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);
        new ClassReader(new FileInputStream(file)).accept(new DebugClassVistor(), 0);
    }

    public static class DebugClassVistor extends ClassVisitor {
        public DebugClassVistor() {
            super(Opcodes.ASM4);
        }

        @Override
        public void visitEnd() {
            System.out.println("<visitEnd>");
            super.visitEnd();
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            System.out.format("<visit> version: %d, access: %d, name: %s, signature: %s, superName: %s, interfaces: %s.\n",
                    version, access, name, signature, superName, Arrays.toString(interfaces));
            super.visit(version, access, name, signature, superName, interfaces);
        }

        @Override
        public void visitSource(String source, String debug) {
            System.out.format("<visitSource> source: %s, debug: %s\n", source, debug);
            super.visitSource(source, debug);
        }

        @Override
        public void visitOuterClass(String owner, String name, String desc) {
            System.out.format("<visitOuterClass> owner: %s, name: %s, desc: %s\n", owner, name, desc);
            super.visitOuterClass(owner, name, desc);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            System.out.format("<visitAnnotation> desc: %s, visible: %s\n", desc, visible);
            return super.visitAnnotation(desc, visible);
        }

        @Override
        public void visitAttribute(Attribute attr) {
            System.out.format("<visitAttribute> attr: %s\n", attr);
            super.visitAttribute(attr);
        }

        @Override
        public void visitInnerClass(String name, String outerName, String innerName, int access) {
            System.out.format("<visitInnerClass> name: %s, outName: %s, innerName: %s, access: %d\n", name, outerName, innerName, access);
            super.visitInnerClass(name, outerName, innerName, access);
        }

        @Override
        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            System.out.format("<visitField> access: %d name: %s, desc: %s, signature: %s, value: %s\n",
                    access, name, desc, signature, value);
            return super.visitField(access, name, desc, signature, value);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            System.out.format("<visitMethod> access: %d, name: %s, desc: %s, signature: %s, exceptions: %s\n",
                    access, name, desc, signature, Arrays.toString(exceptions));
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

}
