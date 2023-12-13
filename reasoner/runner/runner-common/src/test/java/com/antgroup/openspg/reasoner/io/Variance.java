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

public class Variance {
  public static double Sum(double[] data) {
    double sum = 0;
    for (double datum : data) {
      sum = sum + datum;
    }
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
