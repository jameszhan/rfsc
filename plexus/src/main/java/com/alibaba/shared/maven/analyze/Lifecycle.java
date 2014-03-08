/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.shared.maven.analyze;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 3/8/14
 *         Time: 3:52 PM
 */
public class Lifecycle {

    public static void main(String[] args) throws Exception{
        Object lcpa = MavenUtils.lookup("org.apache.maven.lifecycle.LifeCyclePluginAnalyzer");

        System.out.println("lifecycleMappings: ");
        System.out.println(JSON.toJSONString(MavenUtils.getFieldValue("lifecycleMappings", lcpa), SerializerFeature.PrettyFormat));
        System.out.println("\n\n\n");
        System.out.println("defaultLifeCycles: ");
        System.out.println(JSON.toJSONString(MavenUtils.getFieldValue("defaultLifeCycles", lcpa), SerializerFeature.PrettyFormat));
    }

}
