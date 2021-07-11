package com.mingyi.dataroute.scheduler.service;

import com.mingyi.dataroute.persistence.scheduler.entity.SchedulerPO;
import com.mingyi.dataroute.persistence.scheduler.mapper.SchedulerMapper;
import com.mingyi.dataroute.scheduler.CommonJob;
import com.mingyi.dataroute.scheduler.SchedulerHelp;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class SchedulerServiceImpl implements SchedulerService {

     Logger logger = LoggerFactory.getLogger(SchedulerHelp.class);
     StdSchedulerFactory factory = new StdSchedulerFactory();
     Scheduler scheduler;
     Map<String, Object> map = new HashMap<String,Object>();
     Map<String, String> excutorMap = new HashMap<String,String>();
     SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

     {
        try {
            // 自定义配置方法
            /*Properties  pop = new Properties();
            pop.put("org.quartz.threadPool.threadCount", "10");
            factory.initialize(pop);*/
            scheduler = factory.getScheduler();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    @Resource
    private SchedulerMapper mapper;

    @Override
    public String initAllProcess() {
        try {
            List<SchedulerPO>  allProcess = mapper.findAllProcess();
            logger.info("定时调度重新启动-- " + simpleDateFormat.format(new Date()));
            scheduler.clear();
            for (int i=0 ; i< allProcess.size() ; i++){
                SchedulerPO bean = allProcess.get(i);
                if (!CronExpression.isValidExpression(bean.getCrontab())){
                   throw new RuntimeException("JobId :【 "+ bean.getProcessId()+ " 】定时任务表达式 【"+bean.getCrontab()+" 】异常！！！请检查");
                }
                JobDetail jobDetail = JobBuilder.newJob(CommonJob.class).usingJobData("processId",bean.getProcessId()).withIdentity("Job"+i).build();
                jobDetail.getJobDataMap().put("jobId",  bean.getProcessId()+"");
                map.put("Job"+bean.getProcessId(),jobDetail.getKey());
                CronTrigger jobTrigger = TriggerBuilder.newTrigger().withIdentity("jobTrigger"+ bean.getProcessId())
                        .withSchedule(CronScheduleBuilder.cronSchedule(bean.getCrontab().trim())).build();
                map.put("JobTrigger"+bean.getProcessId(),jobTrigger.getKey());
                if(!scheduler.checkExists(jobDetail.getKey())){
                    scheduler.scheduleJob(jobDetail,jobTrigger);
                    excutorMap.put(bean.getProcessId().toString(),"Job"+bean.getProcessId()+" -- processId "+bean.getProcessId() );
                }
            }
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("定时调度初始化执行错误 ：　-- " + e.getMessage()+ " 失败时间 ： "+ simpleDateFormat.format(new Date()));
            return "Scheduler restart error ! ";
        }catch (RuntimeException e){
            logger.error(e.getMessage());
            return e.getMessage();
        }
        return "Scheduler initAllProcess success ! " + " 当前时间 ： "+ simpleDateFormat.format(new Date());
    }

    @Override
    public String initLoopProcess() {
        try {
            List<SchedulerPO>  allProcess = mapper.findAllProcess();
            logger.info("定时调度重新启动-- " + simpleDateFormat.format(new Date()));
            scheduler.clear();
            for (int i=0 ; i< allProcess.size() ; i++){
                SchedulerPO bean = allProcess.get(i);
                if (!CronExpression.isValidExpression(bean.getCrontab())){
                    throw new RuntimeException("JobId :【 "+ bean.getProcessId()+ " 】定时任务表达式 【"+bean.getCrontab()+" 】异常！！！请检查");
                }
                JobDetail jobDetail = JobBuilder.newJob(CommonJob.class).usingJobData("processId",bean.getProcessId()).withIdentity("Job"+i).build();
                jobDetail.getJobDataMap().put("jobId", i+"");
                map.put("Job"+bean.getProcessId(),jobDetail.getKey());
                CronTrigger jobTrigger = TriggerBuilder.newTrigger().withIdentity("jobTrigger"+i)
                        .withSchedule(CronScheduleBuilder.cronSchedule(bean.getCrontab().trim())).build();
                map.put("JobTrigger"+bean.getProcessId(),jobTrigger.getKey());
                if(!scheduler.checkExists(jobDetail.getKey())){
                    scheduler.scheduleJob(jobDetail,jobTrigger);
                    excutorMap.put(bean.getProcessId().toString(),"Job"+bean.getProcessId()+" -- processId "+bean.getProcessId() );
                }
            }
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("定时调度初始化执行错误 ：　-- " + e.getMessage()+ " 失败时间 ： "+ simpleDateFormat.format(new Date()));
            return "Scheduler restart error ! ";
        }catch (RuntimeException e){
            logger.error(e.getMessage());
            return e.getMessage();
        }
        return "Scheduler initAllProcess success ! " + " 当前时间 ： "+ simpleDateFormat.format(new Date());
    }


    /**
     * 初始化所有的任务
     * @return
     */
    /*@Override
    public String initAllProcess(){
        try {
            List<SchedulerPO>  allProcess = mapper.findAllProcess();
            Date date = new Date();
            System.out.println("Current Time is "+simpleDateFormat.format(date));
            logger.info( simpleDateFormat.format(date) + "-- 定时任务启动成功");
            for (SchedulerPO bean: allProcess) {
                JobDetail jobDetail = JobBuilder.newJob(CommonJob.class).withIdentity("Job"+"--"+bean.getProcessId(),"AI_GROUP").build();
                map.put("Job"+bean.getProcessId(),jobDetail.getKey());
                jobDetail.getJobDataMap().put("jobId", bean.getProcessId()+"");
                CronTrigger jobTrigger = TriggerBuilder.newTrigger().withIdentity("JobTrigger"+bean.getProcessId())
                        .withSchedule(CronScheduleBuilder.cronSchedule(bean.getCrontab())).build();
                map.put("JobTrigger"+bean.getProcessId(),jobTrigger.getKey());
                if ("0".equals(bean.getStopFlag().toString())){
                    scheduler.scheduleJob(jobDetail,jobTrigger);
                    excutorMap.put(bean.getProcessId().toString(),"Job"+bean.getProcessId()+" -- processId "+bean.getProcessId() );
                }
            }
            scheduler.start();

        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("定时调度执行错误 ：　-- " + e.getMessage());
            return "Scheduler start error ! " + e.getMessage();
        }
        return "Scheduler start success ! ";
    }*/


    @Override
    public String  deleteJob(Integer id) {
        SchedulerPO  bean =  mapper.findProcessById(id);
        try {
            Scheduler scheduler = factory.getScheduler();
            JobKey jobKey = (JobKey)map.get("Job"+bean.getProcessId());
            if(scheduler.checkExists(jobKey)){
                scheduler.deleteJob(jobKey);
            }else {
                return "Scheduler not exists ! " + "【 "+ bean.getProcessId() + " 】";
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("定时调度删除任务执行错误 ：　-- " + e.getMessage() + " 失败时间 ： "+ simpleDateFormat.format(new Date()));
            return "Scheduler add error ! " + "【 "+ bean.getProcessId() + " 】";
        }

        return "Scheduler delete success ! " + "【 "+ bean.getProcessId() + " 】";
    }

    @Override
    public String viewStatByJob() {
        StringBuilder  sb = new StringBuilder();
        try {
            SchedulerMetaData metaData = scheduler.getMetaData();
            Date runningSince = scheduler.getMetaData().getRunningSince();
            System.out.println(" 启动时间:【 "+ simpleDateFormat.format( runningSince )+" 】");
            sb.append(" 启动时间:【 "+ simpleDateFormat.format( runningSince )+" 】").append("<br/>");
            sb.append("正在执行中的任务 ： ").append("<br/>");
            if(excutorMap.size() == 0 || excutorMap==null){
                return "当前无正在执行中的任务 ！！！！";
            }
            for (String key: excutorMap.keySet()  ) {
                String  value = excutorMap.get(key);
                sb.append(value).append("<br/>");
            }
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("定时调度查看异常 ：　-- " + e.getMessage()+" 时间 ： "+ simpleDateFormat.format(new Date()));
        }
        return sb.toString() +"<br/> 当前时间 ： "+ simpleDateFormat.format(new Date());
    }


    @Override
    public String  addJob(Integer id) {
        SchedulerPO  bean =  mapper.findProcessById(id);
        try {
            JobDetail jobDetail = JobBuilder.newJob(CommonJob.class).withIdentity("Job"+"--"+bean.getProcessId()).build();
            jobDetail.getJobDataMap().put("jobId", bean.getProcessId()+"");
            CronTrigger jobTrigger = TriggerBuilder.newTrigger().withIdentity("JobTrigger"+bean.getProcessId())
                    .withSchedule(CronScheduleBuilder.cronSchedule(bean.getCrontab())).build();
            if(!scheduler.checkExists(jobDetail.getKey())){
                scheduler.scheduleJob(jobDetail,jobTrigger);
            }else {
                return "Scheduler exists  ! " + "【 "+ bean.getProcessId() + " 】";
            }
            map.put("Job"+bean.getProcessId(),jobDetail.getKey());
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("定时调度添加任务执行错误 ：　-- " + e.getMessage()+" 失败时间 ： "+ simpleDateFormat.format(new Date()));
            return "Scheduler add error ! " + "【 "+ bean.getProcessId() + " 】";
        }
        return "Scheduler add success ! " + "【 "+ bean.getProcessId() + " 】"  + " 添加成功时间 ： "+ simpleDateFormat.format(new Date());
    }



    @Override
    public String  standByScheduler() {
        try {
            scheduler.standby();
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("定时调度执行挂起失败 ：　-- " + e.getMessage()+" 失败时间 ： "+ simpleDateFormat.format(new Date()));
            return "Scheduler StandBy error ! ";
        }
        return "Scheduler StandBy success ! " + " 挂起时间 ： "+ simpleDateFormat.format(new Date());
    }


    @Override
    public String  startScheduler() {
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            e.printStackTrace();
            logger.error("定时调度执行重新启动失败 ：　-- " + e.getMessage()+" 失败时间 ： "+ simpleDateFormat.format(new Date()));
            return "Scheduler Start error ! ";
        }
        return "Scheduler Start success ! " + " 启动时间 ： "+ simpleDateFormat.format(new Date());
    }

    @Override
    public String updateActFailState(Long jobId) {

        return mapper.updateActFailState(jobId);
    }

    @Override
    public String updateActSuccessState(Long jobId) {

        return mapper.updateActSuccessState(jobId);
    }

    @Override
    public List<SchedulerPO> findProcessAllAct() {
        return mapper.findProcessAllAct();
    }

    @Override
    public SchedulerPO findProcessActByJobId(Long jobId) {
        return mapper.findProcessActByJobId(jobId);
    }

    @Override
    public SchedulerPO findProcessById(Integer processId) {
        return mapper.findProcessById(processId);
    }

    @Override
    public List<SchedulerPO> findProcessActById(Integer processId) {
        return mapper.findProcessActById(processId);
    }


}
