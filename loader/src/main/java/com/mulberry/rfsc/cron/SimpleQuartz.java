/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.rfsc.cron;

import com.alibaba.fastjson.JSON;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.text.ParseException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/24/14
 *         Time: 11:17 AM
 */
public class SimpleQuartz {

    public static void main(String[] args) throws SchedulerException, ParseException {
        // Initiate a Schedule Factory
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        // Retrieve a scheduler from schedule factory
        Scheduler scheduler = schedulerFactory.getScheduler();

        // current time
        long currentTime = System.currentTimeMillis();

        // Initiate JobDetail with job name, job group, and executable job class
        JobDetail jobDetail = new JobDetail("jobDetail", "jobDetailGroup", SimpleJob.class);
        // Initiate SimpleTrigger with its name and group name
        SimpleTrigger simpleTrigger = new SimpleTrigger("simpleTrigger", "triggerGroup");

        // set its start up time
        simpleTrigger.setStartTime(new Date(currentTime));
        // set the interval, how often the job should run (10 seconds here)
        simpleTrigger.setRepeatInterval(10000);
        // set the number of execution of this job, set to 10 times.
        // It will run 10 time and exhaust.
        simpleTrigger.setRepeatCount(100);
        // set the ending time of this job.
        // We set it for 60 seconds from its startup time here
        // Even if we set its repeat count to 10,
        // this will stop its process after 6 repeats as it gets it endtime by then.
        //simpleTrigger.setEndTime(new Date(currentTime + 60000L));
        // set priority of trigger. If not set, the default is 5
        //simpleTrigger.setPriority(10);
        // schedule a job with JobDetail and Trigger
        scheduler.scheduleJob(jobDetail, simpleTrigger);


        //CronTrigger cronTrigger = new CronTrigger("cronTrigger", "triggerGroup", "0/5 * * * * ?");
        //scheduler.scheduleJob(jobDetail, cronTrigger);

        // start the scheduler
        scheduler.start();
    }


    public static class SimpleJob implements Job {

        @Override
        public void execute(JobExecutionContext context) throws JobExecutionException {
            System.out.format("context: %s\n", JSON.toJSONString(context));
            System.out.println("Simple Job Running...");
        }

    }

}

