package com.mingyi;

import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.fw4j.core.env.ApplicationContext;
import com.vbrug.fw4j.core.env.SystemEnvironment;
import org.mybatis.spring.annotation.MapperScan;
import org.quartz.Job;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.core.QuartzScheduler;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.IOException;

/**
 * @author LK
 * @since 1.0
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.mingyi.dataroute.persistence", "com.mingyi.temp"})
@MapperScan(basePackages = {"com.mingyi.dataroute.persistence.**.mapper", "com.mingyi.workflow.persistence.**.mapper"})
public class APP {

    public static void main(String[] args) throws IOException {
        // 启动
        ConfigurableApplicationContext applicationContext = SpringApplication.run(APP.class, args);

        // 初始化
        init(applicationContext);

        test();

    }

    private static void test(){
        try {
            Scheduler defaultScheduler = StdSchedulerFactory.getDefaultScheduler();
            defaultScheduler.start();
            defaultScheduler.shutdown();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }


    }

    /**
     * 应用环境初始化
     *
     * @param applicationContext spring上下文环境
     * @throws IOException IO异常
     */
    private static void init(ConfigurableApplicationContext applicationContext) throws IOException {
        // 初始化
        SpringHelp.setApplicationContext(applicationContext);


        // 初始化上下文环境
        ApplicationContext.init(applicationContext.getResource("classpath:").getURL().getPath(), new SystemEnvironment());

    }

}
