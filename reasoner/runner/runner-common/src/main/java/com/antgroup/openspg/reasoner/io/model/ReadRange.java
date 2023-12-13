/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.io.model;


public class ReadRange implements Comparable<ReadRange> {
    private long start;
    private long end;

    /**
     * create read range
     */
    public ReadRange(long start, long end) {
        this.start = start;
        this.end = end;
    }

    /**
     * Getter method for property <tt>start</tt>.
     *
     * @return property value of start
     */
    public long getStart() {
        return start;
    }

    /**
     * Setter method for property <tt>start</tt>.
     *
     * @param start value to be assigned to property start
     */
    public void setStart(long start) {
        this.start = start;
    }

    /**
     * Getter method for property <tt>end</tt>.
     *
     * @return property value of end
     */
    public long getEnd() {
        return end;
    }

    /**
     * Setter method for property <tt>end</tt>.
     *
     * @param end value to be assigned to property end
     */
    public void setEnd(long end) {
        this.end = end;
    }

    /**
     * get read count
     */
    public long getCount() {
        return end - start;
    }

    /**
     * compare
     */
    @Override
    public int compareTo(ReadRange o) {
        return (int) (this.start - o.start);
    }
}