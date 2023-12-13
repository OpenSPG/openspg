/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.sink;

import com.antgroup.openspg.reasoner.common.Utils;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author donghai.ydh
 * @version KgReasonerCsvWriter.java, v 0.1 2023年04月14日 14:41 donghai.ydh
 */
@Slf4j(topic = "userlogger")
public class KgReasonerCsvWriter {

    private static final Map<String, CSVWriter> CSV_WRITER_MAP = new ConcurrentHashMap<>();

    public static void write(String file, Object[] data) {
        //log.info("KgReasonerCsvWriter,file=" + file + ",data=" + Arrays.toString(data));
        CSVWriter writer = CSV_WRITER_MAP.computeIfAbsent(file, k -> {
                    Utils.deletePath(file);
                    Utils.createFile(file);
                    try {
                        return new CSVWriter(new FileWriter(file));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        String[] strData = new String[data.length];
        for (int i = 0; i < data.length; ++i) {
            strData[i] = String.valueOf(data[i]);
        }

        List<String[]> listStrData = new ArrayList<>();
        listStrData.add(strData);
        writer.writeAll(listStrData);
    }

    public static void flush(String file) {
        CSVWriter writer = CSV_WRITER_MAP.remove(file);
        if (null == writer) {
            return;
        }
        try {
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}