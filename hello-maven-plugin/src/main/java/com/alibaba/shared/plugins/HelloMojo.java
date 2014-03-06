/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.shared.plugins;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;

/**
 *
 * @goal hello
 * @phase validate
 *
 * @author zizhi.zhzzh
 *         Date: 3/6/14
 *         Time: 5:03 PM
 */
public class HelloMojo extends AbstractMojo {

    /**
     * @parameter expression="${project.build.directory}"
     * @required
     */
    private String buildDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("*************************************************");
        getLog().info("Hello: " + buildDir);
        getLog().info("*************************************************");
    }

}
