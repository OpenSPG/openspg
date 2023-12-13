/*
 * Ant Group
 * Copyright (c) 2004-2023 All Rights Reserved.
 */
package com.antgroup.openspg.reasoner.io;


public class Variance {
    public static double Sum(double[] data) {
        double sum = 0;
        for (double datum : data) {sum = sum + datum;}
        return sum;
    }

    public static double Mean(double[] data) {
        double mean = 0;
        mean = Sum(data) / data.length;
        return mean;
    }

    public static double sampleVariance(double[] data) {
        double variance = 0;
        double mean = Mean(data);
        for (double datum : data) {
            variance = variance + (Math.pow((datum - mean), 2));
        }
        variance = variance / (data.length - 1);
        return variance;
    }

    public static double sampleStdDev(double[] data) {
        double std_dev;
        std_dev = Math.sqrt(sampleVariance(data));
        return std_dev;
    }
}