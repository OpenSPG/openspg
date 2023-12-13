/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.batching;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengqiang.cq
 * @version $Id: DynamicBatchSize.java, v 0.1 2020-09-11 2:23 下午 chengqiang.cq Exp $$
 */
@Slf4j(topic = "userlogger")
public class DynamicBatchSize {
    private final List<Integer> costs        = new ArrayList<>();
    private final List<Long>    batchSizes   = new ArrayList<>();
    private       long          preBatchTime = 0;
    private final double        step         = 1.0;
    private       long          processSize  = 0;
    private       int           disturb      = 10;
    private       long          minBatchSize = 10;
    private       long          maxBatchSize = 5000000;

    private int  nowRound = 0;
    private long allSize  = 0;

    private final int expectRounds;

    public DynamicBatchSize(long allSize, long minBatchSize, long maxBatchSize, int expectRounds) {
        this.allSize = allSize;
        this.minBatchSize = minBatchSize;
        if (this.minBatchSize > this.allSize) {
            this.minBatchSize = this.allSize;
        }
        this.maxBatchSize = maxBatchSize;
        this.expectRounds = expectRounds;
        initNewDisturb();
    }

    private int computeProcessTime() {
        long curTime = System.currentTimeMillis();
        int cost = (int) (curTime - preBatchTime);
        preBatchTime = curTime;
        return cost;
    }

    public long remainSize() {
        long remain = allSize - processSize;
        if (remain < 0) {
            return 0;
        }
        return remain;
    }

    public long getAllSize() {
        return allSize;
    }

    public long getProcessSize() {
        return processSize;
    }

    private void initNewDisturb() {
        disturb = (int) (allSize * 0.0005);
        if (disturb <= 0) {
            disturb = 10;
        }
    }

    /**
     * 获取下一个batch大小
     * 动态batchSize计算公式为
     * N^i = N^(i-1) - p*s
     * 其中p为步长, s为梯度
     *
     * @return
     */
    public long getNextBatchSize() {
        if (allSize - processSize <= 0) {
            return 0;
        }
        if (nowRound == 0) {
            if (expectRounds <= 1) {
                processSize = allSize;
                return allSize;
            }
            //第一轮设置为500分位
            preBatchTime = System.currentTimeMillis();
            long curSize = (long) (allSize / (expectRounds * 2.5));
            curSize = uniformBatchSize(curSize);
            processSize = processSize + curSize;
            batchSizes.add(curSize);

            nowRound++;
            return curSize;
        } else if (nowRound == 1) {
            //第二轮设置为200分位
            long curSize = allSize / expectRounds;
            curSize = uniformBatchSize(curSize);
            costs.add(computeProcessTime());
            processSize = processSize + curSize;
            batchSizes.add(curSize);

            nowRound++;
            return curSize;
        } else {
            int nowCost = computeProcessTime();
            costs.add(nowCost);
            int size = costs.size();
            long curSize = 0;
            if (batchSizes.get(size - 1) - batchSizes.get(size - 2) == 0) {
                // 分母为0，batchSize做一个扰动，避免被限制在某一个数值
                curSize = batchSizes.get(size - 1) + disturb;
            } else {
                double tN1 = costs.get(size - 1) * allSize * 1.0 / batchSizes.get(size - 1);
                double tN2 = costs.get(size - 2) * allSize * 1.0 / batchSizes.get(size - 2);
                double gradient = (tN1 - tN2)
                        / (batchSizes.get(size - 1) - batchSizes.get(size - 2));
                curSize = Double.valueOf(batchSizes.get(size - 1) - step * gradient).intValue();
                if (curSize <= 0) {
                    //兜底，如果计算值异常则取上一个batch大小
                    curSize = batchSizes.get(size - 1);
                }
            }
            curSize = (int) Math.min(allSize - processSize, curSize);
            curSize = uniformBatchSize(curSize);

            // 梯度控制，一次不允许增加超过30%
            final double gradient = 1.3;
            if (batchSizes.size() > 0) {
                long lastBatchSize = batchSizes.get(batchSizes.size() - 1);
                if (1.0 * curSize / lastBatchSize > gradient) {
                    curSize = (long) (lastBatchSize * gradient);
                }
            }

            if (nowCost < 30 * 1000) {
                // 每轮计算小于30秒，不允许降低batch大小，因为负载非常低时，梯度不明显
                long lastSize = batchSizes.get(batchSizes.size() - 1);
                if (curSize < lastSize) {
                    curSize = lastSize + disturb;
                }
                if (curSize > remainSize()) {
                    curSize = remainSize();
                }
            }

            batchSizes.add(curSize);
            processSize = processSize + curSize;

            log.info("costList=" + JSONObject.toJSONString(costs)
                    + ", batchSizes=" + JSONObject.toJSONString(batchSizes)
                    + ", nowRound=" + nowRound);
            nowRound++;
            return curSize;
        }
    }

    private long uniformBatchSize(long batchSize) {
        long size = batchSize;
        if (batchSize < minBatchSize) {
            size = minBatchSize;
        } else if (batchSize > maxBatchSize) {
            size = maxBatchSize;
        }
        long remain = remainSize();
        if (size > remain) {
            return remain;
        }
        return size;
    }
}