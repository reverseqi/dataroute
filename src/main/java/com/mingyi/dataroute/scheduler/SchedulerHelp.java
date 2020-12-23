package com.mingyi.dataroute.scheduler;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class SchedulerHelp {


    public static void main(String[] args) throws Exception{
        Logger logger = LoggerFactory.getLogger(SchedulerHelp.class);
        LoggerContext iLoggerFactory = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger root = iLoggerFactory.getLogger("root");
        root.setLevel(Level.INFO);
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println("Current Time is "+simpleDateFormat.format(date));
        JobDetail jobDetail1 = JobBuilder.newJob(CommonJob.class).withIdentity("Job1").build();
        jobDetail1.getJobDataMap().put("jobId", "001");
        CronTrigger testJob1Trigger = TriggerBuilder.newTrigger().withIdentity("testJob1Trigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).build();
        JobDetail jobDetail2 = JobBuilder.newJob(CommonJob.class).withIdentity("testJob2").build();
        jobDetail2.getJobDataMap().put("jobId", "002");
        CronTrigger testJob2Trigger = TriggerBuilder.newTrigger().withIdentity("testJob2Trigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/5 * * * * ?")).build();

        SchedulerFactory factory = new StdSchedulerFactory();
        Scheduler scheduler = factory.getScheduler();
        scheduler.start();
        scheduler.scheduleJob(jobDetail1, testJob1Trigger);
        scheduler.scheduleJob(jobDetail2, testJob2Trigger);
        Thread.sleep(20000L);
        scheduler.standby();
        Thread.sleep(3000L);

        scheduler.start();

        Thread.sleep(3000L);
        scheduler.shutdown();
    }
}

