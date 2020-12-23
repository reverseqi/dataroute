package com.mingyi.dataroute.api;

import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.wfcall.WorkFlowCallService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 任务http服务接口
 * @author vbrug
 * @since 1.0.0
 */
@RestController
@RequestMapping("task")
public class TaskController {

    private static final Log logger = LogFactory.getLog(TaskController.class);

    /**
     * 任务重跑
     * @param jobId
     * @param nodeId
     * @return
     */
    @RequestMapping("redo")
    public String redo(String id){
        TaskContext taskContext = WorkFlowCallService.failTaskMonitorMap.get(id);
        if (taskContext == null)
            return "task not exists! ";
        new Thread(() -> WorkFlowCallService.execFailTask(taskContext)).start();
        return "redo executing task 【"+ id +"】";
    }


}
