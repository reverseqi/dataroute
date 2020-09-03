package com.mingyi.temp;

import com.mingyi.dataroute.executor.ExecutorFactory;
import com.mingyi.dataroute.persistence.task.bsql.service.BSqlService;
import com.mingyi.dataroute.persistence.task.extract.service.ExtractService;
import com.vbrug.fw4j.core.spring.SpringHelp;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author vbrug
 * @since 1.0.0
 */
@EnableScheduling
@Controller
@RequestMapping("/")
public class TaskController2 {

    private static final Log log = LogFactory.getLog(TaskController2.class);

    private static AtomicInteger lock = new AtomicInteger(0);

    @RequestMapping("test")
    public String test() throws InterruptedException {
        List<Integer> extractList = SpringHelp.getBean(ExtractService.class).selectAllTask();
        CountDownLatch countDownLatch = new CountDownLatch(extractList.size());
        extractList.forEach(x -> {
            new Thread(){
                @Override
                public void run() {
                    ExecutorFactory.createExecutor(ExecutorFactory.EXTRACT).execute(x);
                    countDownLatch.countDown();
                }
            }.start();
        });
        countDownLatch.await();

        List<Integer> integerList = SpringHelp.getBean(BSqlService.class).selectAll();
        for (Integer id : integerList) {
            ExecutorFactory.createExecutor(ExecutorFactory.BSQL).execute(id);
        }
        return "success";
    }

    @Scheduled(cron="0 0/1 * * * ?")
    public void execute(){
        synchronized (this) {
            if (lock.get() != 0){
                log.info("尚有任务执行中.........");
                return;
            }
        }
        lock.incrementAndGet();
        try {
            this.test();
            lock.decrementAndGet();
        } catch (InterruptedException e) {
            e.printStackTrace();
            log.error("-------任务执行异常-----------");
            log.error(e.getMessage(), e);
        } finally {
            if (lock.get() != 0)
                lock.set(0);
        }

    }

}
