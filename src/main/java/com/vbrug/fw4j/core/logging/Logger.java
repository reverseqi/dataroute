package com.vbrug.fw4j.core.logging;

import com.vbrug.fw4j.common.util.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Logger implements Log{

    private Log log;

    private String prefix = "";

    private Logger(Class<?> clazz){
        this.log = LogFactory.getLog(clazz);
    }

    public static Logger getLogger(Class<?> clazz){
        return new Logger(clazz);
    }

    public static Logger getLogger(Class<?> clazz, String prefix){
        Logger logger = new Logger(clazz);
        logger.prefix = "[ "+prefix+" ] ";
        return logger;
    }

    @Override
    public boolean isFatalEnabled() {
        return false;
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void fatal(Object message) {
        log.fatal(this.prefix+String.valueOf(message));
    }

    @Override
    public void fatal(Object message, Throwable t) {
        log.fatal(this.prefix+String.valueOf(message), t);
    }

    @Override
    public void error(Object message) {
        log.error(this.prefix+String.valueOf(message));
    }

    @Override
    public void error(Object message, Throwable t) {
        log.error(this.prefix+String.valueOf(message), t);
    }

    @Override
    public void warn(Object message) {
        log.warn(this.prefix+String.valueOf(message));
    }

    @Override
    public void warn(Object message, Throwable t) {
        log.warn(this.prefix+String.valueOf(message), t);
    }

    @Override
    public void info(Object message) {
        log.info(this.prefix+String.valueOf(message));
    }

    @Override
    public void info(Object message, Throwable t) {
        log.info(this.prefix+String.valueOf(message), t);
    }

    @Override
    public void debug(Object message) {
        log.debug(this.prefix+String.valueOf(message));
    }

    @Override
    public void debug(Object message, Throwable t) {
        log.debug(this.prefix+String.valueOf(message), t);
    }

    @Override
    public void trace(Object message) {
        log.trace(this.prefix+String.valueOf(message));
    }

    @Override
    public void trace(Object message, Throwable t) {
        log.trace(this.prefix+String.valueOf(message), t );
    }

    /**
     * Format the log output format
     * @param logFormatType LogFormatType
     * @param message the message
     */
    public void info(LogFormatType logFormatType, Object message){
        log.info(this.getFormatMessage(logFormatType, message));
    }

    private String getFormatMessage(LogFormatType logFormatType, Object message){
        StringBuilder sb = new StringBuilder();
        sb.append(this.prefix);
        sb.append(System.lineSeparator());
        String msg = String.valueOf(message);
        String splitMark;
        if (LogFormatType.STAGE == logFormatType){
            splitMark = StringUtils.rpad("=", '=',  80);
        } else {
            splitMark = StringUtils.rpad("*", '*',  80);
        }
        sb.append(splitMark);
        sb.append(System.lineSeparator());
        String[] strings = msg.split(System.lineSeparator());
        for (String string : strings) {
            sb.append(StringUtils.alignFill(string, ' ', 80));
            sb.append(System.lineSeparator());
        }
        sb.append(splitMark);
        return sb.toString();
    }
}
