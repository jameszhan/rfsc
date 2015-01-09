/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.rfsc.cron;

import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/24/14
 *         Time: 10:45 AM
 */
public class Main {

    public static void main(String[] args) throws SchedulerException, Exception {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        //Scheduler scheduler = appContext.getBean("scheduler", Scheduler.class);

        System.out.println(appContext.getBean("jobDetail"));

        /*

        CronTrigger cronTrigger = new CronTrigger("hello", "DEFAULT", "0/5 * * * * ?");

        Trigger trigger = appContext.getBean("cronTrigger", Trigger.class);
        System.out.println(trigger);

        scheduler.shutdown();

        System.out.println("===========================");
        scheduler.start();
        */
    }

}
