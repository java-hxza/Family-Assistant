package com.family.assistant.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    public static Logger getLogger() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        return LoggerFactory.getLogger(stackTrace[2].getClassName());
    }
} 