package com.mingyi.dataroute.executor.extract;

import com.google.common.collect.Lists;
import com.mingyi.dataroute.context.TaskContext;
import com.mingyi.dataroute.db.dialect.Dialect;
import com.mingyi.dataroute.executor.ExecutorConstants;
import com.mingyi.dataroute.persistence.task.extract.po.ExtractPO;
import com.mingyi.dataroute.persistence.task.extract.service.ExtractService;
import com.vbrug.fw4j.common.util.*;
import com.vbrug.fw4j.core.design.pc.Consumer;
import com.vbrug.fw4j.core.design.pc.PCHelper;
import com.vbrug.fw4j.core.design.pc.Producer;
import com.vbrug.fw4j.core.spring.SpringHelp;
import com.vbrug.fw4j.core.thread.SignalLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

/**
 * @author vbrug
 * @since 1.0.0
 */
public class ExtractRunner {


    public static final Logger logger = LoggerFactory.getLogger(ExtractRunner.class);

    private final TaskContext taskContext;
    private final ExtractPO po;
    private final ExtractDO ado;
    private String originMaxValue;

    public ExtractRunner(ExtractPO po, TaskContext taskContext) throws SQLException, IOException {
        this.taskContext = taskContext;
        this.po = po;
        this.ado = new ExtractDO(po, taskContext);
    }

    /**
     * 判断此次同步是否有新数据
     */
    public boolean hasNewData() throws SQLException {
        originMaxValue = this.queryOriginMaxValue();
        logger.info("【{}--{}】，原数据表数据最大值为： --> {}", taskContext.getId(), taskContext.getNodeName(), originMaxValue);
        if (StringUtils.isEmpty(originMaxValue))
            return false;
        if (ado.getExtractValueField().getType().equalsIgnoreCase(ExecutorConstants.FIELD_T_DATETIME)) {
            if (DateUtils.compare(originMaxValue, DateUtils.getCurrentDateTime(), DateUtils.YMDHMS) == 1) {
                originMaxValue = DateUtils.getCurrentDateTime();
            }
            return DateUtils.compare(ado.getExtractValueField().getValue(), originMaxValue, DateUtils.YMDHMS) != 1;
        } else {
            return Long.compare(Long.parseLong(ado.getExtractValueField().getValue()), Long.parseLong(originMaxValue)) != 1;
        }
    }

    /**
     * 清空中间表
     */
    public void truncateMdTable() throws SQLException {
        String truncateSQL = ado.getOriginDataSource().getDialect().buildTruncateSQL(po.getTargetTable());
        ado.getTargetSqlRunner().run(truncateSQL);
        logger.info("【{}--{}】，中间表清空SQL --> {}", taskContext.getId(), taskContext.getNodeName(), truncateSQL);
    }

    /**
     * 数据抽取
     * @throws Exception
     */
    public void extractData() throws Exception {
        // 01-初始化锁和队列
        ConcurrentLinkedDeque<List<Map<String, String>>> deque = new ConcurrentLinkedDeque<>();
        SignalLock lock = new SignalLock(true);
        PCHelper pcHelper = new PCHelper();

        // 02-生产者
        List<String> dateList = ado.getExtractValueField().getType().equalsIgnoreCase(ExecutorConstants.FIELD_T_DATETIME)
                ? DateUtils.avgSplitDate(ado.getExtractValueField().getValue(), originMaxValue, po.getProducerNumber())
                : NumberUtils.avgSplitNumber(ado.getExtractValueField().getValue(), originMaxValue, po.getProducerNumber());
        logger.info("【{}--{}】，切割参数-->{}", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(), dateList);
        for (int i = 0; i < po.getProducerNumber(); i++) {
            boolean isStart = false, isEnd = false;
            if (i == 0) isStart = true;
            if ((i + 1) == po.getProducerNumber()) isEnd = true;
            ExtractProducerHandler handler = new ExtractProducerHandler(ado, dateList.get(i), dateList.get(i + 1), isStart, isEnd);
            pcHelper.addProducer(new Producer<>(handler, deque, lock, true, true));
        }

        // 03-消费者
        for (int i = 0; i < po.getConsumerNumber(); i++) {
            pcHelper.addConsumer(new Consumer<>(new ExtractConsumerHandler(ado), deque, lock));
        }
        logger.info("【{}--{}】，消费者入库SQL-->{}", ado.getTaskContext().getId(), ado.getTaskContext().getNodeName(), ado.getBatchInsertSql());

        // 04-等待任务完成
        pcHelper.start();
        pcHelper.finishProducer();
        pcHelper.finishConsumer();

        // 05-判断任务是否正常结束
        if (!pcHelper.isSuccess()) {
            logger.error("任务--> {}--{}，生产消费异常，任务执行失败！", taskContext.getId(), taskContext.getNodeName());
            throw new Exception("生产消费异常，任务执行失败");
        }
    }

    /**
     * 更新触发条件
     */
    public List<ExtractDO.Field> updateTrigger() throws SQLException {
        // 获取同步最大时间
        ado.getExtractValueField().setValue(this.queryTargetMaxTime());
        // 获取当前最新主键
        if (ado.getExtractKeyField() != null) {
            String keys = this.queryDupTimeKey(ado.getExtractValueField().getValue());
            ado.getExtractKeyField().setValue(keys);
        }

        // 拼装List
        List<ExtractDO.Field> triggerFieldList;
        if (ado.getExtractKeyField() != null) {
            triggerFieldList = Lists.newArrayList(ado.getExtractValueField(), ado.getExtractKeyField());
        } else {
            triggerFieldList = Lists.newArrayList(ado.getExtractValueField());
        }

        SpringHelp.getBean(ExtractService.class).updateTriggerValue(
                taskContext.getJobContext().getProcessId(),
                taskContext.getNodeId(),
                JacksonUtils.bean2Json(triggerFieldList));
        return triggerFieldList;
    }

    /**
     * 查询同步的最大时间
     */
    private String queryOriginMaxValue() throws SQLException {
        Dialect dialect = ado.getOriginDataSource().getDialect();

        // 查询条件判断
        List<String> condList = new ArrayList<>();
        condList.add(ado.getExtractValueCond());
        if (StringUtils.hasText(ado.getPo().getDefaultCond()))
            condList.add(ado.getPo().getDefaultCond());

        // 排除同触发值的key
        if (StringUtils.hasText(ado.getExtractKeyCond())) {
            condList.add(ado.getExtractKeyCond());
        }

        String maxValueExpression;
        if (ado.getExtractValueField().getType().equalsIgnoreCase(ExecutorConstants.FIELD_T_DATETIME)) {
            maxValueExpression = "max(" + dialect.vfDate2String(ado.getExtractValueField().getName()) + ")";
        } else {
            maxValueExpression = "max(" + ado.getExtractValueField().getName() + ")";
        }

        // 拼接查询SQL
        String querySQL = dialect.buildQuerySQL(po.getOriginTable(),
                new String[]{maxValueExpression},
                condList.toArray(new String[condList.size()]));
        logger.info("【{}--{}】，原始库触发条件最大值查询SQL --> {}", taskContext.getId(), taskContext.getNodeName(), StringUtils.removeTNR(querySQL));
        Map<String, Object> result = ado.getOriginSqlRunner().selectOne(querySQL);
        if (CollectionUtils.isEmpty(result) || Objects.isNull(result.entrySet().iterator().next()))
            return null;
        return dialect.jdbcType2String(result).entrySet().iterator().next().getValue();
    }

    /**
     * 查询同步的最大时间
     */
    private String queryTargetMaxTime() throws SQLException {
        Dialect dialect = ado.getTargetDataSource().getDialect();

        // 查询条件判断
        List<String> condList = new ArrayList<>();

        // 拼接查询SQL
        String querySQL = "";
        String maxValueExpression;
        if (ado.getExtractValueField().getType().equalsIgnoreCase(ExecutorConstants.FIELD_T_DATETIME)) {
            maxValueExpression = "max(" + dialect.vfDate2String(ado.getExtractValueField().getName()) + ")";
        } else {
            maxValueExpression = "max(" + ado.getExtractValueField().getName() + ")";
        }
        if (ado.getJobField() != null) {
            querySQL = dialect.buildQuerySQL(po.getTargetTable(),
                    new String[]{maxValueExpression},
                    ado.getJobField().getName() + " = " + ado.getJobField().getValue());
        } else {
            querySQL = dialect.buildQuerySQL(po.getTargetTable(),
                    new String[]{maxValueExpression});
        }
        logger.info("【{}--{}】，目标库同步数据最时间查询SQL --> {}", taskContext.getId(), taskContext.getNodeName(), StringUtils.removeTNR(querySQL));
        Map<String, Object> result = ado.getTargetSqlRunner().selectOne(querySQL);
        if (CollectionUtils.isEmpty(result) || Objects.isNull(result.entrySet().iterator().next()))
            return null;
        return dialect.jdbcType2String(result).entrySet().iterator().next().getValue();
    }

    /**
     * 查询同时间key
     */
    private String queryDupTimeKey(String syncMaxTime) throws SQLException {
        Dialect dialect = ado.getTargetDataSource().getDialect();
        String querySQL = null;

        String syncMaxCond = syncMaxTime;
        if (ado.getExtractValueField().getType().equalsIgnoreCase(ExecutorConstants.FIELD_T_DATETIME)) {
            syncMaxCond = dialect.vfString2Date(syncMaxTime);
        }
        // 拼接查询SQL
        if (ado.getJobField() != null) {
            querySQL = dialect.buildQuerySQL(po.getTargetTable(),
                    new String[]{ado.getExtractKeyField().getName()},
                    ado.getJobField().getName() + " = " + ado.getJobField().getValue(),
                    ado.getExtractValueField().getName() + " = " + syncMaxCond);
        } else {
            querySQL = dialect.buildQuerySQL(po.getTargetTable(),
                    new String[]{ado.getExtractKeyField().getName()},
                    ado.getExtractValueField().getName() + " = " + syncMaxCond);
        }
        logger.info("【{}--{}】，同步最大时间Key查询SQL --> {}", taskContext.getId(), taskContext.getNodeName(), StringUtils.removeTNR(querySQL));

        List<Map<String, Object>> maps = ado.getTargetSqlRunner().selectAll(querySQL);
        return dialect.jdbcType2String(maps).stream().map(x -> x.entrySet().iterator().next().getValue()).collect(Collectors.joining(","));
    }

}
