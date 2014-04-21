/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.rfsc.loader;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.NoSuchRealmException;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/13/14
 *         Time: 1:36 AM
 */
public class HadoopLoaderTests {
    private static Loader loader;

    @BeforeClass
    public static void setUpLoader() throws IOException {
        System.setProperty("hadoop.home.dir", "/usr/local/opt/hadoop");
        System.setProperty("hadoop.example.jar", "hadoop-mapreduce-examples-2.4.0.jar");
        System.setProperty("hadoop.example.path", "/opt/var/maven/org/apache/hadoop/hadoop-mapreduce-examples/2.4.0/");
        String hadoopDir = System.getProperty("hadoop.home.dir");
        loader = new Loader(hadoopDir + "/libexec/share/hadoop/");
        loader.add("hadoop-common-lib", "common/lib/*.jar");
        loader.add("hadoop-common", "common/*.jar", "hadoop-common-lib");
        loader.add("hadoop-tools", "tools/lib/*.jar", "hadoop-common");

        loader.add("hadoop-hdfs", "hdfs/*.jar", "hadoop-tools");
        loader.add("hadoop-yarn", "yarn/*.jar", "hadoop-common");
        loader.add("hadoop-mapreduce", "mapreduce/*.jar", "hadoop-yarn");
        //ClassWorld classWorld =
        loader.load();
    }

    @Test
    public void loadResources() throws Exception {
        ClassRealm classRealm = loadExample();
        Enumeration<URL> resources = classRealm.getResources("META-INF/services/");
        while (resources.hasMoreElements()) {
            URL url =  resources.nextElement();
            System.out.println(url);
        }
        System.out.println(classRealm.loadClass("org.apache.hadoop.log.metrics.EventCounter"));
    }

    /**
     * hadoop jar /usr/local/Cellar/hadoop/2.3.0/libexec/share/hadoop/mapreduce/sources/hadoop-mapreduce-examples-2.3.0-sources.jar org.apache.hadoop.examples.WordCount /u/workdir/Codes/rfsc/LICENSE /tmp/out/wc
     * hadoop jar /usr/local/Cellar/hadoop/2.3.0/libexec/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.3.0.jar wordcount /u/workdir/Codes/rfsc/LICENSE /tmp/out/wc
     * @throws Exception
     */
    @Test
    public void wordCount() throws Exception {
        ClassRealm classRealm = loadExample();
        loader.launch(classRealm, "org.apache.hadoop.examples.WordCount", "/u/workdir/Codes/rfsc/LICENSE", "/tmp/out/wordCount");
    }

    @Test
    public void grep() throws Exception {
        ClassRealm classRealm = loadExample();
        loader.launch(classRealm, "org.apache.hadoop.examples.Grep", "/u/workdir/Codes/rfsc/LICENSE", "/tmp/out/grep", "zhiqiangzhan");
    }

    private ClassRealm loadExample() throws IOException, NoSuchRealmException {
        ClassRealm classRealm = loader.loadTarget("hadoop-example", System.getProperty("hadoop.example.jar"),
                System.getProperty("hadoop.example.path"));
        classRealm.setParentRealm(loader.findClassRealm("hadoop-mapreduce"));
        return classRealm;
    }
}
