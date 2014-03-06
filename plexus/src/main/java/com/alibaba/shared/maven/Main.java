/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.shared.maven;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 2/24/14
 *         Time: 11:58 PM
 */
public class Main {

    private static final String DEFAULT_REALM_NAME = "plexus.core";

    public static void main(final String[] args) throws Exception {
        System.setProperty("user.dir", "/u/workdir/Codes/rfsc");
        System.setProperty("maven.home", "/usr/local/Cellar/maven/3.2.1/libexec");
        String mavenCli = "org.apache.maven.cli.MavenCli";

        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        final ClassWorld world = new ClassWorld();
        world.addListener(new DebugClassWorldListener());
        ClassRealm classRealm = world.newRealm(DEFAULT_REALM_NAME, systemClassLoader);

        for(File file : globFiles(String.format("%s%s", System.getProperty("maven.home"), "/lib/*.jar"))) {
            classRealm.addURL(file.toURI().toURL());
        }

        //final String[] arguments = new String[]{"--debug", "install"};
        final String[] arguments = new String[]{"dependency:list"};
        final Class<?> mainClass = classRealm.loadClass(mavenCli);
        final Method method = getMainMethod(mainClass);
        executeIn(classRealm, new Runnable() {
            public void run() {
                try {
                    method.invoke(mainClass, arguments, world);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    static void executeIn(ClassLoader cl, Runnable runnable) throws Exception {
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(cl);
            runnable.run();
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassLoader);
        }
    }


    public static List<File> globFiles(String globPattern) {
        File globFile = new File(globPattern);
        File dir = globFile.getParentFile();
        if (!dir.exists()) {
            return Collections.emptyList();
        }
        String localName = globFile.getName();
        int starLoc = localName.indexOf("*");
        final String prefix = localName.substring(0, starLoc);
        final String suffix = localName.substring(starLoc + 1);
        File[] files = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.startsWith(prefix) && name.endsWith(suffix);
            }
        });
        return Arrays.asList(files);
    }


    protected static Method getMainMethod(Class<?> mainClass) throws Exception {
        Method m = mainClass.getMethod("main", new Class[]{String[].class, ClassWorld.class});
        int modifiers = m.getModifiers();

        if ( Modifier.isStatic(modifiers) && Modifier.isPublic(modifiers)) {
            if (m.getReturnType() == Integer.TYPE || m.getReturnType() == Void.TYPE) {
                return m;
            }
        }
        throw new NoSuchMethodException("public static void main(String[] args) in " + mainClass);
    }

}
