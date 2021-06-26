package com.mingyi.dataroute.api;

import com.mingyi.dataroute.wfcall.WorkFlowCallService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工作流服务接口
 * @author vbrug
 * @since 1.0.0
 */
@RestController
@RequestMapping("workflow")
public class WorkFlowController {

    /**
     * 执行流程
     */
    @RequestMapping("doProcess")
    public String doProcess(Integer processId) {
        new Thread(() -> WorkFlowCallService.startProcess(processId)).start();
        return "start execute process 【" + processId + "】";
    }

    /**
     * 停止流程
     */
    @RequestMapping("stopProcess")
    public String stopProcess(Integer processId) {
/*
        JobContext jobContext = WorkFlowCallService.processMonitorMap.get(String.valueOf(processId));
        if (jobContext == null)
            return "job 【" + processId + "】 not exists !!!";
        WorkFlowCallService.stopProcess(jobContext);
        return "job 【" + processId + "】 has stopped !!!";
*/
        return null;
    }
}
