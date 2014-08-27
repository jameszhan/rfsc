/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.rfsc.cron;

import com.google.common.base.Optional;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/24/14
 *         Time: 10:38 AM
 */
public class PoolingTask implements Job {

    static int count = 0;
    {
        count++;
        System.out.println("count = " + count);
    }

    private String name = "Default Name";

    @Resource(name = "schedulerHolder")
    private SchedulerHolder scheduler;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        System.out.println("====================");
        System.out.println(context.getScheduler());
        System.out.println(scheduler);
        System.out.println(this);
        System.out.println(this.hashCode());
        System.out.println(context.getTrigger().hashCode());
        System.out.println("====================");
        System.out.println(context);
        System.out.println("Hello " + context.getMergedJobDataMap().getString("name"));
    }

    public void doBiz(){
        System.out.println("Hello " + name);
    }

    public void setName(String name) {
        this.name = name;
    }

}
