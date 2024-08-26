/*
 * Copyright 2023 OpenSPG Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied.
 */

package com.antgroup.openspg.reasoner.runner.local;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.FileAppender;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

public class LogUtil {
    public static void setUpLogFile(String logFileName) {
        LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();
        loggerContext.reset();

        PatternLayoutEncoder patternLayoutEncoder = new PatternLayoutEncoder();
        patternLayoutEncoder.setPattern("%d [%X{traceId}] [%X{rpcId}] [%t] %-5p %c{2} - %m%n");
        patternLayoutEncoder.setContext(loggerContext);
        patternLayoutEncoder.start();

        FileAppender<ILoggingEvent> fileAppender = null;
        if (StringUtils.isNotBlank(logFileName)) {
            fileAppender = new FileAppender<>();
            fileAppender.setFile(logFileName);
            fileAppender.setEncoder(patternLayoutEncoder);
            fileAppender.setContext(loggerContext);
            fileAppender.setAppend(false);
            fileAppender.start();
        }

        ConsoleAppender<ILoggingEvent> consoleAppender = new ConsoleAppender<>();
        consoleAppender.setEncoder(patternLayoutEncoder);
        consoleAppender.setContext(loggerContext);
        consoleAppender.start();

        Logger brpcLogger = loggerContext.getLogger("com.baidu.brpc");
        brpcLogger.setLevel(Level.ERROR);
        brpcLogger.setAdditive(false);
        if (fileAppender != null) {
            brpcLogger.addAppender(fileAppender);
        }
        brpcLogger.addAppender(consoleAppender);

        Logger dtflysLogger = loggerContext.getLogger("com.dtflys.forest");
        dtflysLogger.setLevel(Level.ERROR);
        dtflysLogger.setAdditive(false);
        if (fileAppender != null) {
            dtflysLogger.addAppender(fileAppender);
        }
        dtflysLogger.addAppender(consoleAppender);

        Logger rootLogger = loggerContext.getLogger("root");
        if (fileAppender != null) {
            rootLogger.addAppender(fileAppender);
        }
        rootLogger.addAppender(consoleAppender);
        rootLogger.setLevel(Level.INFO);
    }
}
