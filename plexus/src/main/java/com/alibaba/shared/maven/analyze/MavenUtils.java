/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.shared.maven.analyze;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.inject.AbstractModule;
import org.codehaus.plexus.*;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 3/8/14
 *         Time: 3:44 PM
 */
public class MavenUtils {

    private static final String DEFAULT_REALM_NAME = "plexus.core";
    private static PlexusContainer plexusContainer;


    public static Object lookup(String role) throws Exception {
        if (plexusContainer == null) {
            plexusContainer = setupContainer();
        }
        ClassLoader oldClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(plexusContainer.getContainerRealm());
            return plexusContainer.lookup(role);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassLoader);
        }
    }

    private static PlexusContainer setupContainer() throws Exception {
        System.setProperty("maven.home", "/usr/local/Cellar/maven/3.2.1/libexec");
        ClassWorld world = new ClassWorld();
        ClassRealm classRealm = world.newRealm(DEFAULT_REALM_NAME, ClassLoader.getSystemClassLoader());
        for(File file : globFiles(String.format("%s%s", System.getProperty("maven.home"), "/lib/*.jar"))) {
            classRealm.addURL(file.toURI().toURL());
        }

        ContainerConfiguration cc = new DefaultContainerConfiguration()
                .setClassWorld(world)
                .setRealm(classRealm)
                .setClassPathScanning(PlexusConstants.SCANNING_INDEX)
                .setAutoWiring(true)
                .setName("maven");

        DefaultPlexusContainer container = new DefaultPlexusContainer(cc, new AbstractModule() {
            protected void configure() {
            }
        });
        container.setLookupRealm(null);
        return container;
    }

    public static Object getFieldValue(String fieldName, Object target) {
        Object retValue = null;
        try {
            Field f = findFieldIncludeSuperclass(fieldName, target.getClass());
            f.setAccessible(true);
            retValue = f.get(target);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return retValue;
    }

    protected static Field findFieldIncludeSuperclass(String fieldName, Class<?> clazz) {
        Field retValue = null;
        try {
            retValue = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            clazz = clazz.getSuperclass();
            if (clazz != null) {
                retValue = findFieldIncludeSuperclass(fieldName, clazz);
            }
        }
        return retValue;
    }



    private static List<File> globFiles(String globPattern) {
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
}
