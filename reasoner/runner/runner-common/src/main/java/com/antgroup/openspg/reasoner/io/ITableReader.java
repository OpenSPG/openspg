/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.io;

import com.antgroup.openspg.reasoner.io.model.AbstractTableInfo;

import java.util.Iterator;
import java.util.List;


public interface ITableReader extends AutoCloseable, Iterator<Object[]> {
    /**
     * init reader for read a list of tables
     */
    void init(int index, int parallel, int nowRound, int allRound, List<AbstractTableInfo> tableInfoList);

    /**
     * close reader
     */
    void close() throws Exception;
}