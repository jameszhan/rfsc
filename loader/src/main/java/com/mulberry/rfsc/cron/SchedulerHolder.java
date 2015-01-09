/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.rfsc.cron;

import org.quartz.Scheduler;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 7/9/14
 *         Time: 5:38 PM
 */
public class SchedulerHolder {

    @Resource
    private Scheduler scheduler;

    public Scheduler getScheduler() {
        return scheduler;
    }
}
