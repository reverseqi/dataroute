package com.mingyi.dataroute;

import com.vbrug.fw4j.common.util.IOUtils;
import com.vbrug.fw4j.core.env.ApplicationContext;
import com.vbrug.fw4j.core.env.SystemEnvironment;
import com.vbrug.fw4j.core.spring.SpringHelp;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.FileReader;
import java.io.IOException;

/**
 * @author LK
 * @since 1.0
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.mingyi.dataroute", "com.mingyi.dataroute.api", "com.vbrug.workflow"})
@MapperScan(basePackages = {"com.mingyi.dataroute.persistence.**.mapper", "com.vbrug.workflow.core.persistence.**.mapper"})
public class APP {

    public static void main(String[] args) throws IOException {
        // 启动
        ConfigurableApplicationContext applicationContext = SpringApplication.run(APP.class, args);

        // 初始化
        init(applicationContext, args);

        test();

    }

    private static void test() {
//        WorkFlowCallService.startProcess(1001);
    }

    /**
     * 应用环境初始化
     * @param applicationContext spring上下文环境
     * @throws IOException IO异常
     */
    private static void init(ConfigurableApplicationContext applicationContext, String[] args) throws IOException {
        // 初始化
        SpringHelp.setApplicationContext(applicationContext);
        // 初始化上下文环境
        ApplicationContext.init(applicationContext.getResource("classpath:").getURL().getPath(), new SystemEnvironment());

        String content = IOUtils.getContent(new FileReader("/root/a.a")).trim();
        System.setProperty("my_losdf", content);

    }

}
