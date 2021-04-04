package com.mingyi.dataroute.api.security;

import com.vbrug.fw4j.common.third.http.HttpHelp;
import com.vbrug.fw4j.common.third.http.PostRequest;
import com.vbrug.fw4j.common.util.CollectionUtils;
import com.vbrug.fw4j.common.util.JacksonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * @author vbrug
 * @since 1.0.0
 */
@RestController
@RequestMapping("security/subscribe")
public class SubscribeController {

    private static final Logger logger = LoggerFactory.getLogger(SubscribeController.class);

    @RequestMapping(value = "startExecute", method = RequestMethod.POST)
    public Object startExecute(@RequestBody SubscribeDO condDO) {
        logger.info("【乱点订阅】, 请求参数: {}", JacksonUtils.bean2Json(condDO));
        return CollectionUtils.createValueMap().add("status", "0").add("remark", "开始执行任务").build();
    }

    @RequestMapping(value = "callback")
    public Object callback() throws IOException {
        PostRequest postRequest = HttpHelp.createPostRequest("http://192.168.10.28:8080/jqfx/security/rule/updateRuleStatus");
//        PostRequest postRequest = HttpHelp.createPostRequest("http://127.0.0.1:9081/dataroute/security/subscribe/startExecute");
        postRequest.addHeader("Content-Type", "application/json");
        postRequest.putParam("ruleNo", "1608798998032");
        postRequest.putParam("remark", "跑批成功");
        return postRequest.execute();
    }

}
