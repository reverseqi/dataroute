package com.mingyi.dataroute.api;

import com.mingyi.dataroute.wfcall.WorkFlowCallService;
import com.vbrug.fw4j.common.util.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class SchedulerController1 {

    public void syncData() {
        if (!CollectionUtils.isEmpty(WorkFlowCallService.failJob)) {
            WorkFlowCallService.FAIL_TEST = 1;
            Set<Long> longs = WorkFlowCallService.failJob.keySet();
            for (Long aLong : longs) {
                WorkFlowCallService.execFailTask(aLong);
            }
        } else
            WorkFlowCallService.startProcess(901);
    }

}
