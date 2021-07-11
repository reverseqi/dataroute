package com.mingyi.dataroute.scheduler;

import com.mingyi.dataroute.persistence.scheduler.entity.SchedulerPO;
import com.mingyi.dataroute.persistence.scheduler.mapper.SchedulerMapper;
import com.mingyi.dataroute.scheduler.service.SchedulerService;
import com.mingyi.dataroute.scheduler.service.SchedulerServiceImpl;
import com.mingyi.dataroute.wfcall.WorkFlowCallService;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.workflow.core.WorkFlowService;
import com.vbrug.workflow.core.context.JobContext;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author arthur
 * @since 1.0.0
 */

public class CommonJob implements Job {

    private SchedulerService service = SpringHelp.getBean(SchedulerServiceImpl.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {

        String s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        System.out.println("JobId: " + context.getMergedJobDataMap().getString("jobId") + ", Quartz Job Time is " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "start");

        Integer processId = context.getMergedJobDataMap().getInt("processId");
        if(processId==911){
            // 轮循任务
            List<SchedulerPO> processAllAct = service.findProcessAllAct();
            Map<Long, Integer> map = processAllAct.stream().collect(Collectors.toMap(SchedulerPO::getJobId, SchedulerPO::getProcessId,(key1 , key2)->key1));
            Iterator<Map.Entry<Long, Integer>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()){
                Map.Entry<Long, Integer> next = iterator.next();
                Long jobId = next.getKey();
                SchedulerPO actJobId = service.findProcessActByJobId(jobId);
                // 正在执行中 跳过
                if(actJobId.getState()==1){
                    continue;
                }else if(actJobId.getState()==0){
                    WorkFlowCallService.doJob(actJobId.getJobId());
                    // 更新 任务状态
                    updateActState( actJobId.getJobId(),1);
                }else if(actJobId.getState()==9){
                    //调用重跑失败流程
                    WorkFlowCallService.redoFailJob(actJobId.getJobId());
                    // 更新 任务状态
                    updateActState( actJobId.getJobId(),1);
                }
            }
        }
        // 查询任务是否有依赖，
        //SchedulerPO processById = mapper.findProcessById(context.getMergedJobDataMap().getString("jobId"));
        SchedulerPO process = service.findProcessById(processId);
        Integer relyLastJob = process.getRelyLastJob();
        // 如果为1则查找上一次的任务依赖是否完成
        if(relyLastJob==1){
            // 查询出所有的正在执行的流程ID
            List<SchedulerPO> act = service.findProcessActById(processId);
            if(act !=null && act.size()!=0) {
                SchedulerPO  currentAct= act.get(0);
                if(currentAct.getState()==9){
                    //调用重跑失败流程
                    WorkFlowCallService.redoFailJob(currentAct.getJobId());
                    // 更新 任务状态
                    updateActState( currentAct.getJobId(),1);

                }else if(currentAct.getState()==0){
                    //执行任务
                    WorkFlowCallService.doJob(currentAct.getJobId());
                    // 更新任务状态
                    updateActState( currentAct.getJobId(),1);

                }
            }else{
                // 调用流程执行;
                Long jobId = WorkFlowCallService.newJob(processId);
                // 插入act 表中
                updateActState(jobId,1);

            }
        }else{
            // 调用流程执行;
            Long jobId = WorkFlowCallService.newJob(processId);
            // 插入act 表中
            updateActState(jobId,1);

        }

        System.out.println("JobId: " + context.getMergedJobDataMap().getString("jobId") + ", Quartz Job Time is " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "end");


    }

    /**
     * 执行任务方法
     * @param context
     */
    public void  execCrontab(JobExecutionContext context){

    }

    @Transactional
    public void updateActState(Long jobId,Integer state){

    }

    public static void main(String[] args) {
        List<Person> list = new ArrayList();
        list.add(new Person("1001", "小A"));
        list.add(new Person("1002", "小B"));
        list.add(new Person("1001", "小C"));
        System.out.println(list);

        Map<String, String> map = list.stream().collect(Collectors.toMap(Person::getId, Person::getName,(key1 , key2)->key2));
        Iterator<Map.Entry<String, String>> iterator = map.entrySet().iterator();
        while(iterator.hasNext()){
            Map.Entry<String, String> next = iterator.next();
            String key = next.getKey();
            String value = next.getValue();
            System.out.println(key+"----"+value);
        }
    }
   static class Person{
        public Person(String id,String name){
            this.id=id; this.name=name;
        }
       private String name;
       private String id;

       public String getId() {
           return id;
       }

       public void setId(String id) {
           this.id = id;
       }

       public String getName() {
           return name;
       }

       public void setName(String name) {
           this.name = name;
       }
   }
}
