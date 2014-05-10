/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.rfsc.loader;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/12/14
 *         Time: 12:41 PM
 */
public class Loader {

    private static Logger logger = LoggerFactory.getLogger(Loader.class);

    private final String baseDir;
    private final ClassWorld classWorld;
    private final boolean excludeTestAndSource;
    private final Map<String, ModuleLoader> moduleLoaders = new HashMap<String, ModuleLoader>();

    public Loader(String baseDir) {
        this(baseDir, true);
    }

    public Loader(String baseDir, boolean excludeTestAndSource) {
        this.baseDir = baseDir;
        this.excludeTestAndSource = excludeTestAndSource;
        this.classWorld = new ClassWorld();
    }

    public Loader add(String realmId, String pattern){
        moduleLoaders.put(realmId, newModuleLoader(realmId, null, pattern));
        return this;
    }

    public Loader add(String realmId, String pattern, String parent){
        moduleLoaders.put(realmId, newModuleLoader(realmId, parent, pattern));
        return this;
    }

    public Loader dependency(String realmId, String targetRealmId, String... packages) {
        ModuleLoader moduleLoader = moduleLoaders.get(realmId);
        if (moduleLoader != null) {
            moduleLoader.addDependency(targetRealmId, packages);
        } else {
            logger.warn("Can't find module {}", realmId);
        }
        return this;
    }

    public ClassRealm loadTarget(final String relamId, final String pattern, final String baseDir) throws IOException {
        Path base = Paths.get(baseDir);
        final ClassRealm classRealm = findClassRealm(relamId);
        Files.walkFileTree(base, new SimpleFileVisitor<Path>(){
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + baseDir + "" + pattern);
                if (matcher.matches(file)) {
                    logger.debug("handle target {} to realm {}", file, relamId);
                    classRealm.addURL(file.toUri().toURL());
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return classRealm;
    }

    public ClassWorld load() throws IOException {
        Files.walkFileTree(Paths.get(baseDir), new SimpleFileVisitor<Path>() {
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                for (ModuleLoader moduleLoader : moduleLoaders.values()) {
                    moduleLoader.handle(file, attrs);
                }
                return FileVisitResult.CONTINUE;
            }
        });
        return loadModules();
    }

    public ClassRealm findClassRealm(String realmId) {
        ClassRealm classRealm = classWorld.getClassRealm(realmId);
        if (classRealm == null) {
            try {
                classRealm = classWorld.newRealm(realmId);
            } catch (DuplicateRealmException e) {
                logger.warn("ClassRealm {} has already defined.", realmId, e);
                classRealm = classWorld.getClassRealm(realmId);
            }
        }
        return classRealm;
    }

    private ClassWorld loadModules(){
        for (ModuleLoader moduleLoader : moduleLoaders.values()) {
            moduleLoader.load();
        }
        return classWorld;
    }

    public void launch(ClassRealm mainRealm, String mainClassName, String... args) throws ClassNotFoundException, IllegalAccessException,
            InvocationTargetException, NoSuchMethodException, NoSuchRealmException {
        Class<?> mainClass = mainRealm.loadClass(mainClassName);
        Method mainMethod = getMainMethod(mainClass);
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(mainRealm);
            Object ret = mainMethod.invoke(mainClass, (Object)args);
            System.out.println(ret);
        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }

    private ModuleLoader newModuleLoader(String realmId, String parentRealmId, String pattern){
        return new ModuleLoader(newModule(realmId, parentRealmId),
                baseDir, pattern, excludeTestAndSource);
    }

    private Module newModule(String realmId, String parentRealmId){
        return new Module(classWorld, realmId, parentRealmId);
    }

    protected static Method getMainMethod(Class<?> mainClass) throws NoSuchMethodException {
        Method m = mainClass.getMethod("main", String[].class);
        int modifiers = m.getModifiers();

        if ( Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
            if (m.getReturnType() == Integer.TYPE || m.getReturnType() == Void.TYPE) {
                return m;
            }
        }
        throw new NoSuchMethodException("public static void main(String[] args) in " + mainClass);
    }

    private static class ModuleLoader {
        private final Module module;
        private final PathMatcher pathMatcher;
        private final boolean excludeTestAndSource;

        private ModuleLoader(Module module, String baseDir, String globPattern, boolean excludeTestAndSource) {
            this.module = module;
            this.excludeTestAndSource = excludeTestAndSource;
            this.pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + baseDir + "" + globPattern);
        }

        public void handle(Path file, BasicFileAttributes attrs) throws IOException {
            if (accept(file)) {
                logger.debug("handle target {} to realm {}", file, module.getRealmId());
                module.addURL(file.toUri().toURL());
            }
        }

        public ClassRealm load() {
            return module.load();
        }

        public Module addDependency(String realmId, String... packages) {
            return module.addDependency(realmId, packages);
        }

        private boolean accept(Path file) {
            return pathMatcher.matches(file) && !(excludeTestAndSource
                    && (file.toString().endsWith("-sources.jar") || file.toString().endsWith("-tests.jar")));
            //return pathMatcher.matches(file) && (!excludeTestAndSource
            //        || !(file.toString().endsWith("-sources.jar") || file.toString().endsWith("-tests.jar")));
        }
    }

}
