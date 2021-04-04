package com.mingyi.dataroute.api;

import com.mingyi.dataroute.wfcall.WorkFlowCallService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author vbrug
 * @since 1.0.0
 */
@Component
public class SchedulerController {

    @Scheduled(cron = "0 30 23 * * ?")
    public void syncData() {
        WorkFlowCallService.startProcess(1001);
        WorkFlowCallService.startProcess(1003);
    }

}
