/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.io;

import com.antgroup.openspg.reasoner.io.model.AbstractTableInfo;

/**
 * @author donghai.ydh
 * @version ITableWriter.java, v 0.1 2023年03月03日 10:30 donghai.ydh
 */
public interface ITableWriter {
    /**
     * open a table writer
     */
    void open(int taskIndex, int parallel, AbstractTableInfo tableInfo);

    /**
     * write a row data into table
     */
    void write(Object[] data);

    /**
     * close writer, commit data
     */
    void close();

    /**
     * get write count
     */
    long writeCount();
}