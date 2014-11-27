package com.mulberry.athena.compile;


import javax.tools.*;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author James Zhan
 *         Date: 6/17/14
 *         Time: 5:37 PM
 */
public class DynamicCompiler {

    public final DynamicClassLoader classLoader = new DynamicClassLoader();

    public CompiledResult compile(String qualifiedName, String sourceCode) {
        SimpleJavaFileObject file = new StringSourceFile(qualifiedName, sourceCode);
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        Map<String, StringClassFile> cache = new HashMap<>();
        JavaFileManager fileManager = new CachedFileManager(compiler.getStandardFileManager(null, null, null), cache);
        try {
            DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
            List<SimpleJavaFileObject> files = Arrays.asList(file);
            StringWriter out = new StringWriter();
            JavaCompiler.CompilationTask task = compiler.getTask(out, fileManager, diagnostics, null, null, files);
            if (task.call()) {
                StringClassFile stringClassFile = cache.get(qualifiedName);
                Class<?> clazz = classLoader.defineClass(qualifiedName, stringClassFile.getClassAsBytes());
                return new CompiledResult(clazz);
            } else {
                for (Diagnostic<? extends JavaFileObject> diagnostic : diagnostics.getDiagnostics()) {
                    out.append("Error on line ")
                        .append(String.valueOf(diagnostic.getLineNumber()))
                        .append(" in ")
                        .append(diagnostic.toString())
                        .append('\n');
                }
                return new CompiledResult(out.toString());
            }
        } finally {
            try {
                fileManager.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        DynamicCompiler dynamicCompiler = new DynamicCompiler();
        CompiledResult compiledResult = dynamicCompiler.compile("com.alibaba.World",
                "package com.alibaba;" +
                "public class World {" +
                "   public int add(int a, int b){" +
                "       return a + b;" +
                "   }" +
                "}");
        if (compiledResult.isSuccess()) {
            Class<?> clazz = compiledResult.getClazz();
            Method method = clazz.getMethod("add", int.class, int.class);
            Object target = clazz.newInstance();
            System.out.println(method.invoke(target, 1, 2));
        } else {
            System.out.println(compiledResult.getErrorMessage());
        }

        System.out.println(dynamicCompiler.classLoader.findClass("com.alibaba.World"));
    }


}
