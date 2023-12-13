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

package com.antgroup.openspg.reasoner.io;

import com.aliyun.odps.Odps;
import com.aliyun.odps.account.Account;
import com.aliyun.odps.account.AliyunAccount;
import com.aliyun.odps.tunnel.TableTunnel;
import com.aliyun.odps.tunnel.TableTunnel.DownloadSession;
import com.aliyun.odps.tunnel.TableTunnel.UploadSession;
import com.aliyun.odps.tunnel.TunnelException;
import com.antgroup.openspg.reasoner.common.exception.HiveException;
import com.antgroup.openspg.reasoner.common.exception.IllegalArgumentException;
import com.antgroup.openspg.reasoner.common.exception.NotImplementedException;
import com.antgroup.openspg.reasoner.common.exception.OdpsException;
import com.antgroup.openspg.reasoner.io.hive.HiveUtils;
import com.antgroup.openspg.reasoner.io.hive.HiveWriter;
import com.antgroup.openspg.reasoner.io.hive.HiveWriterSession;
import com.antgroup.openspg.reasoner.io.model.AbstractTableInfo;
import com.antgroup.openspg.reasoner.io.model.HiveTableInfo;
import com.antgroup.openspg.reasoner.io.model.OdpsTableInfo;
import com.antgroup.openspg.reasoner.io.odps.OdpsReader;
import com.antgroup.openspg.reasoner.io.odps.OdpsUtils;
import com.antgroup.openspg.reasoner.io.odps.OdpsWriter;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalListener;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import scala.Tuple2;

@Slf4j(topic = "userlogger")
public class IoFactory {

  private static final Map<AbstractTableInfo, Tuple2<String, Object>> SESSION_MAP = new HashMap<>();

  /** create writer session, must call on driver */
  public static String createWriterSession(AbstractTableInfo tableInfo) {
    Tuple2<String, Object> idAndSession = SESSION_MAP.get(tableInfo);
    if (null != idAndSession) {
      return idAndSession._1();
    }
    String sessionId = null;
    // create session
    if (tableInfo instanceof OdpsTableInfo) {
      OdpsTableInfo odpsTableInfo = (OdpsTableInfo) tableInfo;
      UploadSession uploadSession = OdpsUtils.createUploadSession(odpsTableInfo);
      sessionId = uploadSession.getId();
      SESSION_MAP.put(odpsTableInfo, new Tuple2<>(sessionId, uploadSession));
      odpsTableInfo.setUploadSessionId(sessionId);
    } else if (tableInfo instanceof HiveTableInfo) {
      HiveTableInfo hiveTableInfo = (HiveTableInfo) tableInfo;
      HiveWriterSession hiveWriterSession = HiveUtils.createHiveWriterSession(hiveTableInfo);
      sessionId = hiveWriterSession.getSessionId();
      SESSION_MAP.put(hiveTableInfo, new Tuple2<>(sessionId, hiveWriterSession));
    }
    log.info(
        "createWriterSession,table_info="
            + tableInfo.getTableInfoKeyString()
            + ",sessionId="
            + sessionId);
    return sessionId;
  }

  /** get writer */
  public static ITableWriter getTableWriter(
      String sessionId, int index, int parallel, AbstractTableInfo tableInfo) {
    String cacheKey = getCacheKey(sessionId, index);
    ITableWriter resultWriter;
    if (tableInfo instanceof OdpsTableInfo) {
      resultWriter = TABLE_WRITER_CACHE.getIfPresent(cacheKey);
      if (null != resultWriter) {
        return resultWriter;
      }
      synchronized (TABLE_WRITER_CACHE) {
        resultWriter = TABLE_WRITER_CACHE.getIfPresent(cacheKey);
        if (null != resultWriter) {
          return resultWriter;
        }
        OdpsWriter odpsWriter = new OdpsWriter();
        odpsWriter.open(index, parallel, tableInfo);
        TABLE_WRITER_CACHE.put(cacheKey, odpsWriter);
      }
      return TABLE_WRITER_CACHE.getIfPresent(cacheKey);
    } else if (tableInfo instanceof HiveTableInfo) {
      resultWriter = TABLE_WRITER_CACHE.getIfPresent(cacheKey);
      if (null != resultWriter) {
        return resultWriter;
      }
      synchronized (TABLE_WRITER_CACHE) {
        resultWriter = TABLE_WRITER_CACHE.getIfPresent(cacheKey);
        if (null != resultWriter) {
          return resultWriter;
        }
        HiveWriter hiveWriter = new HiveWriter();
        hiveWriter.open(index, parallel, tableInfo);
        TABLE_WRITER_CACHE.put(cacheKey, hiveWriter);
      }
      return TABLE_WRITER_CACHE.getIfPresent(cacheKey);
    }
    throw new NotImplementedException(
        "table type not support," + tableInfo.getClass().getName(), null);
  }

  /**
   * commit session, call on driver
   *
   * @param sessionId
   */
  public static void commitWriterSession(String sessionId) {
    // get session
    Object session = null;
    Iterator<Map.Entry<AbstractTableInfo, Tuple2<String, Object>>> it =
        SESSION_MAP.entrySet().iterator();
    while (it.hasNext()) {
      Tuple2<String, Object> idAndSession = it.next().getValue();
      if (!sessionId.equals(idAndSession._1())) {
        continue;
      }
      session = idAndSession._2();
      it.remove();
      break;
    }
    if (null == session) {
      return;
    }

    log.info("commitWriterSession,sessionId=" + sessionId);

    if (session instanceof UploadSession) {
      UploadSession uploadSession = (UploadSession) session;
      try {
        uploadSession.commit();
      } catch (TunnelException | IOException e) {
        throw new OdpsException("commit session error", e);
      }
    } else if (session instanceof HiveWriterSession) {
      HiveWriterSession hiveWriterSession = (HiveWriterSession) session;
      hiveWriterSession.commit();
    }
  }

  /**
   * flush writer, call on worker
   *
   * @param sessionId
   * @param index
   */
  public static void closeWriter(String sessionId, int index) {
    TABLE_WRITER_CACHE.invalidate(getCacheKey(sessionId, index));
  }

  /** get table reader */
  public static ITableReader getTableReader(
      int index, int parallel, AbstractTableInfo tableInfo, Object engineContext) {
    return getTableReader(index, parallel, 0, 1, Lists.newArrayList(tableInfo), engineContext);
  }

  private static final String GEAFLOW_CONTEXT_CLASS_NAME =
      "com.antfin.arch.runtime.context.GRuntimeContext";
  private static final String GEAFLOW_TABLER_READER_CLASS_NAME =
      "com.antgroup.openspg.reasoner.geaflow.io.hive.GeaflowHiveReader";

  /** get table reader for multiple round read */
  public static ITableReader getTableReader(
      int index,
      int parallel,
      int nowRound,
      int allRound,
      List<AbstractTableInfo> tableInfoList,
      Object engineContext) {
    if (null == tableInfoList || tableInfoList.isEmpty()) {
      throw new IllegalArgumentException(
          "tableInfoList", "emptyList", "please input table info list", null);
    }
    if (tableInfoList.get(0) instanceof OdpsTableInfo) {
      Map<OdpsTableInfo, DownloadSession> downloadSessionMap = new HashMap<>();
      for (AbstractTableInfo tableInfo : tableInfoList) {
        OdpsTableInfo odpsTableInfo = (OdpsTableInfo) tableInfo;
        try {
          downloadSessionMap.put(odpsTableInfo, DOWNLOAD_SESSION_CACHE.get(odpsTableInfo));
        } catch (ExecutionException e) {
          throw new OdpsException("create odps download session error", e);
        }
      }
      OdpsReader odpsReader = new OdpsReader(downloadSessionMap);
      odpsReader.init(index, parallel, nowRound, allRound, tableInfoList);
      return odpsReader;
    } else if (tableInfoList.get(0) instanceof HiveTableInfo) {
      if (allRound > 1) {
        throw new HiveException("hive reader not support multiple round read", null);
      }
      if (1 != tableInfoList.size()) {
        throw new HiveException("hive reader not support multiple table read", null);
      }
      if (GEAFLOW_CONTEXT_CLASS_NAME.equals(engineContext.getClass().getName())) {
        ITableReader tableReader;
        String implClassName = GEAFLOW_TABLER_READER_CLASS_NAME;
        try {
          tableReader =
              (ITableReader)
                  Class.forName(implClassName)
                      .getConstructor(Object.class)
                      .newInstance(engineContext);
          tableReader.init(index, parallel, nowRound, allRound, tableInfoList);
        } catch (Exception e) {
          throw new HiveException("can not create ITableReader from name " + implClassName, e);
        }
        return tableReader;
      }
      throw new HiveException("unknown engine type, " + engineContext.getClass().getName(), null);
    }
    throw new NotImplementedException(
        "table type not support," + tableInfoList.get(0).getClass().getName(), null);
  }

  private static String getCacheKey(String sessionId, int index) {
    return sessionId + "_" + index;
  }

  private static final Cache<String, ITableWriter> TABLE_WRITER_CACHE =
      CacheBuilder.newBuilder()
          .maximumSize(100)
          .expireAfterAccess(3, TimeUnit.HOURS)
          .expireAfterWrite(6, TimeUnit.HOURS)
          .removalListener(
              (RemovalListener<String, ITableWriter>)
                  notification -> {
                    log.info(
                        "start_remove_writer, key={}, writeCount={}",
                        notification.getKey(),
                        notification.getValue().writeCount());
                    notification.getValue().close();
                  })
          .build();

  private static final LoadingCache<OdpsTableInfo, DownloadSession> DOWNLOAD_SESSION_CACHE =
      CacheBuilder.newBuilder()
          .maximumSize(2000)
          .expireAfterAccess(3, TimeUnit.HOURS)
          .expireAfterWrite(6, TimeUnit.HOURS)
          .build(
              new CacheLoader<OdpsTableInfo, DownloadSession>() {
                @Override
                public DownloadSession load(OdpsTableInfo odpsTableInfo) throws Exception {
                  log.info("create_download_session,=" + odpsTableInfo);
                  Account account =
                      new AliyunAccount(odpsTableInfo.getAccessID(), odpsTableInfo.getAccessKey());
                  Odps odps = new Odps(account);
                  odps.setEndpoint(odpsTableInfo.getEndPoint());
                  odps.setDefaultProject(odpsTableInfo.getProject());

                  TableTunnel tunnel = new TableTunnel(odps);
                  if (StringUtils.isNotEmpty(odpsTableInfo.getTunnelEndPoint())) {
                    tunnel.setEndpoint(odpsTableInfo.getTunnelEndPoint());
                  }

                  DownloadSession downloadSession =
                      OdpsUtils.tryCreateDownloadSession(tunnel, odpsTableInfo);
                  if (null == downloadSession) {
                    throw new OdpsException("get_download_session_error", null);
                  }
                  return downloadSession;
                }
              });
}
