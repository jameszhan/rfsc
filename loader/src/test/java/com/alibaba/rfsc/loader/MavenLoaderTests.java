/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.rfsc.loader;

import org.junit.Test;
import org.junit.BeforeClass;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/21/14
 *         Time: 10:24 AM
 */
public class MavenLoaderTests {

    private static Loader loader;

    @BeforeClass
    public static void setUpLoader() throws IOException {
        System.setProperty("user.dir", "/u/workdir/Codes/rfsc");
        System.setProperty("maven.home.dir", "/usr/local/opt/maven");

        String mavenDir = System.getProperty("maven.home.dir") + "/libexec/";
        loader = new Loader(mavenDir);
        loader.add("plexus.core", "lib/*.jar");

        loader.load();
    }


    @Test
    public void dependencyList() throws Exception {
        loader.launch(loader.findClassRealm("plexus.core"), "org.apache.maven.cli.MavenCli", "dependency:list");
    }


}
