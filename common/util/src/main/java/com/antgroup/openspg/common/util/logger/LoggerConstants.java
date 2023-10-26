/*
 * Copyright 2023 Ant Group CO., Ltd.
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

package com.antgroup.openspg.common.util.logger;

public class LoggerConstants {

    /**
     * 日志默认值"-"
     */
    public static final String LOG_DEFAULT = "-";

    /**
     * 日志前缀"["
     */
    public static final String LOG_PREFIX = "[";

    /**
     * 日志后缀"]"
     */
    public static final String LOG_SUFFIX = "]";

    /**
     * 日志参数前缀"("
     */
    public static final String LOG_PARAM_PREFIX = "(";

    /**
     * 日志参数后缀")"
     */
    public static final String LOG_PARAM_SUFFIX = ")";

    /**
     * 日志分隔符(英文逗号",")
     */
    public static final String LOG_SEP = ",";

    /**
     * 日志分隔符(英文点号".")
     */
    public static final String LOG_SEP_POINT = ".";

    /**
     * 日志分隔符(英文点号"=")
     */
    public static final String LOG_SEP_EQUAL = "=";

    /**
     * 日志分隔符(英文点号"/")
     */
    public static final String LOG_SEP_SLASH = "/";

    /**
     * 未知(NaN）
     */
    public static final String NAN = "NaN";

    /**
     * 成功("Y")
     */
    public static final String YES = "Y";

    /**
     * 失败("N")
     */
    public static final String NO = "N";

    /**
     * 时间("ms")
     */
    public static final String TIME_UNIT = "ms";

    /**
     * 缓存hit标志
     */
    public static final String HIT = "hit";

    /**
     * 缓存产品merge
     */
    public static final String MERGE = "merge";

    /**
     * 缓存合约筛选
     */
    public static final String SELECTED = "selected";

    /**
     * 仅查询缓存
     */
    public static final String ONLY_QUERY_CACHE = "isOnlyQueryCache";

    /**
     * 差别
     */
    public static final String DIFF = "diff";

    /**
     * 比较
     */
    public static final String COMPARE = "compare";

    /**
     * 日志Key("logKey")
     */
    public static final String LOG_KEY = "logKey";

    /**
     * 日志Key("traceId")
     */
    public static final String TRACE_ID = "traceId";

    /**
     * 日志Key("rpcId")
     */
    public static final String RPC_ID = "rpcId";
}
