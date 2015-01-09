/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.maven;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 3/1/14
 *         Time: 1:05 PM
 */
public class MavenStart {

    public static void main(String[] args) throws Exception {
        System.setProperty("user.dir", "/u/workdir/Codes/rfsc");
        System.setProperty("maven.home", "/usr/local/Cellar/maven/3.2.1/libexec");
        String mavenCli = "org.apache.maven.cli.MavenCli";

        final ClassWorld world = new ClassWorld();
        world.addListener(new DebugClassWorldListener());
        ClassRealm classRealm = world.newRealm("maven.core");

        for(File file : globFiles(String.format("%s%s", System.getProperty("maven.home"), "/lib/*.jar"))) {
            classRealm.addURL(file.toURI().toURL());
        }

        Launcher launcher = new Launcher(world, mavenCli, "maven.core");
        launcher.launchEnhanced(new String[]{"--debug", "install"});
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


}

