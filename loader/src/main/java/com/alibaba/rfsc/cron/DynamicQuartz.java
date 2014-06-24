/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.rfsc.cron;

import org.quartz.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scheduling.quartz.JobDetailBean;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/24/14
 *         Time: 11:37 AM
 */
public class DynamicQuartz {

    private final static String TRIGGER_GROUP = "PreData";

    public static void main(String[] args) throws SchedulerException, ParseException {
        ApplicationContext appContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        Scheduler scheduler = appContext.getBean("scheduler", Scheduler.class);

        CronTrigger cronTrigger = new CronTrigger("config", TRIGGER_GROUP, "0/3 * * * * ?");
        JobDetail jobDetail = new JobDetail("job-1", TRIGGER_GROUP, PoolingTask.class);
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("name", "james");
        jobDetail.setJobDataMap(jobDataMap);

        scheduler.scheduleJob(jobDetail, cronTrigger);


        String[] triggerGroups = scheduler.getTriggerGroupNames();
        for (String triggerGroup : triggerGroups) {
            String[] triggers = scheduler.getTriggerNames(triggerGroup);
            for (String triggerName : triggers) {
                System.out.format("%s: %s\n", triggerGroup, triggerName);
                Trigger trigger = scheduler.getTrigger(triggerName, triggerGroup);
                if (TRIGGER_GROUP.equalsIgnoreCase(triggerGroup) || trigger instanceof CronTrigger) {
                    ((CronTrigger) trigger).setCronExpression("0/10 * * * * ?");
                    scheduler.rescheduleJob(triggerName, triggerGroup, trigger);
                }
                System.out.println(trigger);
                /*
                if (tg instanceof SimpleTrigger && tg.getName().equals("simpleTrigger")) {
                    ((SimpleTrigger)tg).setRepeatCount(100);
                    // reschedule the job
                    scheduler.rescheduleJob(triggers[j], triggerGroups[i], tg);
                    // unschedule the job
                    //scheduler.unscheduleJob(triggersInGroup[j], triggerGroups[i]);
                }
                */
            }
        }
    }

}
