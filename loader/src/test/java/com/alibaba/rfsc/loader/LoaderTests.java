/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.rfsc.loader;

import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.junit.Before;
import org.junit.Test;


/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/13/14
 *         Time: 1:36 AM
 */
public class LoaderTests {

    @Before
    public void prepareEnv(){
        System.setProperty("hadoop.home.dir", "/usr/local/opt/hadoop");
        System.setProperty("hadoop.example.jar", "hadoop-mapreduce-examples-2.4.0.jar");
        System.setProperty("hadoop.example.path", "/opt/var/maven/org/apache/hadoop/hadoop-mapreduce-examples/2.4.0/");
    }

    @Test
    public void loadHadoop() throws Exception {
        String hadoopDir = System.getProperty("hadoop.home.dir");
        Loader loader = new Loader(hadoopDir + "/libexec/share/hadoop/");
        loader.add("hadoop-common-lib", "common/lib/*.jar");
        loader.add("hadoop-common", "common/*.jar", "hadoop-common-lib");
        loader.add("hadoop-tools", "tools/lib/*.jar", "hadoop-common");

        loader.add("hadoop-mapreduce", "mapreduce/*.jar", "hadoop-common");

        loader.add("hadoop-hdfs", "hdfs/*.jar", "hadoop-tools");
        //ClassWorld classWorld =
        loader.load();
        ClassRealm classRealm = loader.loadTarget("hadoop-mapreduce", System.getProperty("hadoop.example.jar"),
            System.getProperty("hadoop.example.path"));

        loader.launch(classRealm, "org.apache.hadoop.examples.WordCount", "/u/workdir/Codes/rfsc/LICENSE", "/tmp/out");
    }
}
