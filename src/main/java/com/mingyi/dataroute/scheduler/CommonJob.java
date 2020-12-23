package com.mingyi.dataroute.scheduler;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class CommonJob implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        System.out.println("JobId: " + context.getMergedJobDataMap().getString("jobId") + ", Quartz Job Time is " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }
}
