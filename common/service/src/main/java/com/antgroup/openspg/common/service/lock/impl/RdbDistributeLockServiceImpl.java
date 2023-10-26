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

package com.antgroup.openspg.common.service.lock.impl;

import com.antgroup.openspg.common.service.lock.DistributeLockService;
import com.antgroup.openspg.common.service.lock.SysLockRepository;
import com.antgroup.openspg.common.service.lock.model.SysLock;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Service
public class RdbDistributeLockServiceImpl implements DistributeLockService {

    /**
     * The minimum value of lock timeout
     */
    private static final Long MIN_TIMEOUT_MS = 2 * 60 * 1000L;

    private static final Long CLEAN_THREAD_SLEEP_MS = 60 * 1000L;

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    @Autowired
    private SysLockRepository sysLockRepository;

    @EventListener(ApplicationStartedEvent.class)
    public void init() {
        executorService.submit(() -> {
            while (true) {
                try {
                    this.removeTimeOutLock();
                } catch (Exception e) {
                    log.error("clean time out lock fail", e);
                } finally {
                    Thread.sleep(CLEAN_THREAD_SLEEP_MS);
                }
            }
        });
    }

    @Override
    public boolean tryLock(String key, Long timeout) {
        if (timeout != null && timeout < MIN_TIMEOUT_MS) {
            throw new IllegalArgumentException("timeout is illegal");
        }

        return sysLockRepository.addLock(key, timeout == null ? null : timeout.toString());
    }

    @Override
    public boolean unlock(String key) {
        return sysLockRepository.removeLock(key);
    }

    private void removeTimeOutLock() {
        List<SysLock> sysLockList = sysLockRepository.queryAllLock();
        if (CollectionUtils.isEmpty(sysLockList)) {
            return;
        }

        for (SysLock sysLock : sysLockList) {
            if (sysLock.getMethodValue() != null) {
                long timeout = Long.parseLong(sysLock.getMethodValue());
                Long createTime = sysLock.getGmtCreate().getTime();
                Long currentTime = System.currentTimeMillis();
                if (currentTime - createTime > timeout) {
                    sysLockRepository.removeLock(sysLock.getMethodName());
                    log.info("lock: {} is timeout", sysLock.getMethodName());
                }
            }
        }
    }
}
