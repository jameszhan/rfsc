package com.mulberry.rfsc.asm;

import static clojure.asm.Opcodes.*;

import clojure.asm.*;
import clojure.asm.commons.GeneratorAdapter;
import clojure.asm.commons.Method;
import clojure.asm.util.TraceClassVisitor;
import org.junit.Test;

import java.io.PrintWriter;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 11/12/14
 *         Time: 6:12 PM
 */
public class Generator {

    public final static String SOURCE_PATH = "NO_SOURCE_PATH";

    private int line = 1;

    /**
     * (+ 1 2)
     */
    @Test
    public void plus_1_2(){
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out), "ObjExpr");
        GeneratorAdapter gen = new GeneratorAdapter(Opcodes.ACC_PUBLIC, Method.getMethod("int getRequiredArity()"),
                null, null, cv);
        gen.visitCode();
        gen.push(3);
        gen.returnValue();
        gen.endMethod();

        System.out.println(new String(cw.toByteArray()));
    }



   // @Test
    public void obj(String internalName, String superName, String[] interfaceNames){
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        ClassVisitor cv = new TraceClassVisitor(cw, new PrintWriter(System.out), "ObjExpr");
        cv.visit(V1_5, ACC_PUBLIC + ACC_SUPER + ACC_FINAL, internalName, null,superName, interfaceNames);
//		         superName != null ? superName :
//		         (isVariadic() ? "clojure/lang/RestFn" : "clojure/lang/AFunction"), null);
            String source = "dynamic_source";
            int lineBefore = 0;
            int lineAfter = 1;
            int columnBefore = 0;
            int columnAfter = 1;

            if(source != null && SOURCE_PATH != null) {
                //cv.visitSource(source, null);
                String smap = "SMAP\n" +
                        ((source.lastIndexOf('.') > 0) ? source.substring(0, source.lastIndexOf('.')) :source)
                        + ".java\n" + "Clojure\n" + "*S Clojure\n" + "*F\n" +
                        "+ 1 " + source + "\n" + SOURCE_PATH + "\n" + "*L\n" +
                        String.format("%d#1,%d:%d\n", lineBefore, lineAfter - lineBefore, lineBefore) + "*E";
                cv.visitSource(source, smap);
            }
            //TODO addAnnotation(cv, classMeta);
        /* FIXME
            //static fields for constants
            for(int i = 0; i < constants.count(); i++) {
                cv.visitField(ACC_PUBLIC + ACC_FINAL + ACC_STATIC, constantName(i), constantType(i).getDescriptor(),
                        null, null);
            }

            //static fields for lookup sites
            for(int i = 0; i < keywordCallsites.count(); i++) {
                cv.visitField(ACC_FINAL + ACC_STATIC, siteNameStatic(i), KEYWORD_LOOKUPSITE_TYPE.getDescriptor(),
                        null, null);
                cv.visitField(ACC_STATIC, thunkNameStatic(i), ILOOKUP_THUNK_TYPE.getDescriptor(),
                        null, null);
            }
        */

            //static init for constants, keywords and vars
            GeneratorAdapter clinitgen = new GeneratorAdapter(ACC_PUBLIC + ACC_STATIC, Method.getMethod("void <clinit> ()"),
                    null, null, cv);

            clinitgen.visitCode();
            clinitgen.visitLineNumber(line, clinitgen.mark());

        /*
            if(constants.count() > 0) {
                emitConstants(clinitgen);
            }

            if(keywordCallsites.count() > 0) {
                emitKeywordCallsites(clinitgen);
            }
        */
/*
            clinitgen.returnValue();

            clinitgen.endMethod();

            //instance fields for closed-overs
            for(ISeq s = RT.keys(closes); s != null; s = s.next()) {
                LocalBinding lb = (LocalBinding) s.first();
                if(isDeftype())
                {
                    int access = isVolatile(lb) ? ACC_VOLATILE :
                            isMutable(lb) ? 0 :
                                    (ACC_PUBLIC + ACC_FINAL);
                    FieldVisitor fv;
                    if(lb.getPrimitiveType() != null)
                        fv = cv.visitField(access
                                , lb.name, Type.getType(lb.getPrimitiveType()).getDescriptor(),
                                null, null);
                    else
                        //todo - when closed-overs are fields, use more specific types here and in ctor and emitLocal?
                        fv = cv.visitField(access
                                , lb.name, OBJECT_TYPE.getDescriptor(), null, null);
                    addAnnotation(fv, RT.meta(lb.sym));
                }
                else
                {
                    //todo - only enable this non-private+writability for letfns where we need it
                    if(lb.getPrimitiveType() != null)
                        cv.visitField(0 + (isVolatile(lb) ? ACC_VOLATILE : 0)
                                , lb.name, Type.getType(lb.getPrimitiveType()).getDescriptor(),
                                null, null);
                    else
                        cv.visitField(0 //+ (oneTimeUse ? 0 : ACC_FINAL)
                                , lb.name, OBJECT_TYPE.getDescriptor(), null, null);
                }
            }

            //static fields for callsites and thunks
            for(int i=0;i<protocolCallsites.count();i++)
            {
                cv.visitField(ACC_PRIVATE + ACC_STATIC, cachedClassName(i), CLASS_TYPE.getDescriptor(), null, null);
            }

            //ctor that takes closed-overs and inits base + fields
            Method m = new Method("<init>", Type.VOID_TYPE, ctorTypes());
            GeneratorAdapter ctorgen = new GeneratorAdapter(ACC_PUBLIC,
                    m,
                    null,
                    null,
                    cv);
            Label start = ctorgen.newLabel();
            Label end = ctorgen.newLabel();
            ctorgen.visitCode();
            ctorgen.visitLineNumber(line, ctorgen.mark());
            ctorgen.visitLabel(start);
            ctorgen.loadThis();

            ctorgen.invokeConstructor(Type.getObjectType(superName), voidctor);

            if(supportsMeta()) {
                ctorgen.loadThis();
                ctorgen.visitVarInsn(IPERSISTENTMAP_TYPE.getOpcode(Opcodes.ILOAD), 1);
                ctorgen.putField(objtype, "__meta", IPERSISTENTMAP_TYPE);
            }

            int a = supportsMeta()?2:1;
            for(ISeq s = RT.keys(closes); s != null; s = s.next(), ++a) {
                Compiler.LocalBinding lb = (Compiler.LocalBinding) s.first();
                ctorgen.loadThis();
                Class primc = lb.getPrimitiveType();
                if(primc != null) {
                    ctorgen.visitVarInsn(Type.getType(primc).getOpcode(Opcodes.ILOAD), a);
                    ctorgen.putField(objtype, lb.name, Type.getType(primc));
                    if(primc == Long.TYPE || primc == Double.TYPE)
                        ++a;
                } else {
                    ctorgen.visitVarInsn(OBJECT_TYPE.getOpcode(Opcodes.ILOAD), a);
                    ctorgen.putField(objtype, lb.name, OBJECT_TYPE);
                }
                closesExprs = closesExprs.cons(new Compiler.LocalBindingExpr(lb, null));
            }


            ctorgen.visitLabel(end);

            ctorgen.returnValue();

            ctorgen.endMethod();

            if(altCtorDrops > 0) {
                //ctor that takes closed-overs and inits base + fields
                Type[] ctorTypes = ctorTypes();
                Type[] altCtorTypes = new Type[ctorTypes.length-altCtorDrops];
                for(int i=0;i<altCtorTypes.length;i++)
                    altCtorTypes[i] = ctorTypes[i];
                Method alt = new Method("<init>", Type.VOID_TYPE, altCtorTypes);
                ctorgen = new GeneratorAdapter(ACC_PUBLIC,
                        alt,
                        null,
                        null,
                        cv);
                ctorgen.visitCode();
                ctorgen.loadThis();
                ctorgen.loadArgs();
                for(int i=0;i<altCtorDrops;i++)
                    ctorgen.visitInsn(Opcodes.ACONST_NULL);

                ctorgen.invokeConstructor(objtype, new Method("<init>", Type.VOID_TYPE, ctorTypes));

                ctorgen.returnValue();
                ctorgen.endMethod();
            }

            if(supportsMeta()) {
                //ctor that takes closed-overs but not meta
                Type[] ctorTypes = ctorTypes();
                Type[] noMetaCtorTypes = new Type[ctorTypes.length-1];
                for(int i=1;i<ctorTypes.length;i++) {
                    noMetaCtorTypes[i - 1] = ctorTypes[i];
                }
                Method alt = new Method("<init>", Type.VOID_TYPE, noMetaCtorTypes);
                ctorgen = new GeneratorAdapter(ACC_PUBLIC, alt, null, null, cv);
                ctorgen.visitCode();
                ctorgen.loadThis();
                ctorgen.visitInsn(Opcodes.ACONST_NULL);	//null meta
                ctorgen.loadArgs();
                ctorgen.invokeConstructor(objtype, new Method("<init>", Type.VOID_TYPE, ctorTypes));

                ctorgen.returnValue();
                ctorgen.endMethod();

                //meta()
                Method meth = Method.getMethod("clojure.lang.IPersistentMap meta()");

                GeneratorAdapter gen = new GeneratorAdapter(ACC_PUBLIC, meth, null, null, cv);
                gen.visitCode();
                gen.loadThis();
                gen.getField(objtype,"__meta",IPERSISTENTMAP_TYPE);

                gen.returnValue();
                gen.endMethod();

                //withMeta()
                meth = Method.getMethod("clojure.lang.IObj withMeta(clojure.lang.IPersistentMap)");

                gen = new GeneratorAdapter(ACC_PUBLIC, meth, null, null, cv);
                gen.visitCode();
                gen.newInstance(objtype);
                gen.dup();
                gen.loadArg(0);

                for(ISeq s = RT.keys(closes); s != null; s = s.next(), ++a) {
                    LocalBinding lb = (LocalBinding) s.first();
                    gen.loadThis();
                    Class primc = lb.getPrimitiveType();
                    if(primc != null)
                    {
                        gen.getField(objtype, lb.name, Type.getType(primc));
                    }
                    else
                    {
                        gen.getField(objtype, lb.name, OBJECT_TYPE);
                    }
                }

                gen.invokeConstructor(objtype, new Method("<init>", Type.VOID_TYPE, ctorTypes));
                gen.returnValue();
                gen.endMethod();
            }

            emitStatics(cv);
            emitMethods(cv);

            if(keywordCallsites.count() > 0)
            {
                Method meth = Method.getMethod("void swapThunk(int,clojure.lang.ILookupThunk)");

                GeneratorAdapter gen = new GeneratorAdapter(ACC_PUBLIC,
                        meth,
                        null,
                        null,
                        cv);
                gen.visitCode();
                Label endLabel = gen.newLabel();

                Label[] labels = new Label[keywordCallsites.count()];
                for(int i = 0; i < keywordCallsites.count();i++)
                {
                    labels[i] = gen.newLabel();
                }
                gen.loadArg(0);
                gen.visitTableSwitchInsn(0,keywordCallsites.count()-1,endLabel,labels);

                for(int i = 0; i < keywordCallsites.count();i++)
                {
                    gen.mark(labels[i]);
//				gen.loadThis();
                    gen.loadArg(1);
                    gen.putStatic(objtype, thunkNameStatic(i),ILOOKUP_THUNK_TYPE);
                    gen.goTo(endLabel);
                }

                gen.mark(endLabel);

                gen.returnValue();
                gen.endMethod();
            }

            //end of class
            cv.visitEnd();

            bytecode = cw.toByteArray();
            if(RT.booleanCast(COMPILE_FILES.deref())) {
                writeClassFile(internalName, bytecode);
            }
            */
    }

}
