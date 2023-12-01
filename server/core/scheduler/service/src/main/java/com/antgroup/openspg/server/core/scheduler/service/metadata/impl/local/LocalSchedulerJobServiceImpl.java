/**
 * Alipay.com Inc. Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.server.core.scheduler.service.metadata.impl.local;

import java.util.List;

import com.antgroup.openspg.server.common.model.base.Page;
import com.antgroup.openspg.server.core.scheduler.model.service.SchedulerJob;
import com.antgroup.openspg.server.core.scheduler.service.metadata.SchedulerJobService;
import org.springframework.stereotype.Service;

/**
 *
 * @author yangjin
 * @version : SchedulerJobServiceImpl.java, v 0.1 2023年11月30日 14:09 yangjin Exp $
 */
@Service
public class LocalSchedulerJobServiceImpl implements SchedulerJobService {

    @Override
    public int insert(SchedulerJob record) {
        return 0;
    }

    @Override
    public int deleteById(Long id) {
        return 0;
    }

    @Override
    public int deleteByIds(List<Long> ids) {
        return 0;
    }

    @Override
    public int update(SchedulerJob record) {
        return 0;
    }

    @Override
    public SchedulerJob getById(Long id) {
        return null;
    }

    @Override
    public Page<List<SchedulerJob>> query(SchedulerJob record) {
        return null;
    }

    @Override
    public int getCount(SchedulerJob record) {
        return 0;
    }

    @Override
    public List<SchedulerJob> getByIds(List<Long> ids) {
        return null;
    }
}
