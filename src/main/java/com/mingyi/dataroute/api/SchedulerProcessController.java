package com.mingyi.dataroute.api;

import com.mingyi.dataroute.scheduler.service.SchedulerService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("scheduler")
public class SchedulerProcessController {

    @Resource
    private SchedulerService service;

    @RequestMapping("initAllProcess")
    public String reStart() {

        return service.initAllProcess();
    }
    @RequestMapping("addJob")
    public String addJob(Integer id) {
        return service.addJob(id);
    }

    @RequestMapping("deleteJob")
    public String  deleteJob(Integer id)  {

        return service.deleteJob(id);
    }

    @RequestMapping("standByScheduler")
    public String standByJob()  {

        return service.standByScheduler();
    }

    /**
     * 挂起之后启动调度
     * @return
     */
    @RequestMapping("restartScheduler")
    public String standByScheduler()  {

        return service.startScheduler();
    }

    @RequestMapping("viewStatByJob")
    public String viewStatByJob()  {
        return service.viewStatByJob();
    }


    /**
     * 回调函数，更新失败的任务
     * @return
     */
    @RequestMapping("updateActFailState")
    public String updateActFailState(Long jobId)  {
        return service.updateActFailState(jobId);
    }

}
