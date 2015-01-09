/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 * @goal memory
 *
 * @author zizhi.zhzzh
 *         Date: 3/6/14
 *         Time: 5:46 PM
 */
public class MemoryMojo extends AbstractMojo {

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        Runtime rt = Runtime.getRuntime();
        long max = rt.maxMemory();
        long total = rt.totalMemory();
        long free = rt.freeMemory();
        long usable = max - total + free;

        getLog().info("*************************************************");
        getLog().info("Max Memory = " + max);
        getLog().info("Allocate Memory = " + total);
        getLog().info("Free Allocate Memory = " + free);
        getLog().info("Max Usable Memory = " + usable);
        getLog().info("*************************************************");
    }
}
