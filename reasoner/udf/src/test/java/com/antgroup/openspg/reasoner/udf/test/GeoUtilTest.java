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

package com.antgroup.openspg.reasoner.udf.test;

import com.antgroup.openspg.reasoner.udf.utils.DateUtils;
import com.antgroup.openspg.reasoner.udf.utils.GeoUtils;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.TimeZone;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTWriter;

public class GeoUtilTest {
  private WKTWriter writer = new WKTWriter();

  @Before
  public void init() {
    DateUtils.timeZone = TimeZone.getTimeZone("Asia/Shanghai");
  }

  @Test
  public void testBuffer() {
    try {
      // WFC Beijing
      String wkt = "POINT (116.458844 39.918806)";
      Geometry g1 = GeoUtils.fromWKT(wkt);
      List<String> s2CellIdList = GeoUtils.getCoveredS2CellIdList(g1);

      // buffer 100m
      Geometry bufferedG1 = GeoUtils.buffer(g1, 100);
      String bufferedWkt = writer.write(bufferedG1);
      Assert.assertTrue(
          bufferedWkt.equals(
              "POLYGON ((116.460015 39.918806, 116.459993 39.918577, 116.459926 39"
                  + ".918358, 116.459818 "
                  + "39.918155, 116.459672 39.917978, 116.459495 39.917832, 116.459292 39.917724, 116.459073 39.917657,"
                  + " 116.458844 39"
                  + ".917635, 116.458615 39.917657, 116.458396 39.917724, 116.458193 39.917832, 116.458016 39.917978, "
                  + "116.45787 39"
                  + ".918155, 116.457762 39.918358, 116.457695 39.918577, 116.457673 39.918806, 116.457695 39.919035, "
                  + "116.457762 39"
                  + ".919254, 116.45787 39.919457, 116.458016 39.919634, 116.458193 39.91978, 116.458396 39.919888, 116"
                  + ".458615 39"
                  + ".919955, 116.458844 39.919977, 116.459073 39.919955, 116.459292 39.919888, 116.459495 39.91978, "
                  + "116.459672 39"
                  + ".919634, 116.459818 39.919457, 116.459926 39.919254, 116.459993 39.919035, 116.460015 39.918806))"));
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }

  @Test
  public void testGetCoveredS2CellIdList() {
    try {
      String wktPolygon =
          "POLYGON((115.167803 38.70004,115.16782 38.700063,115.167838 38.700073,115.167855 38"
              + ".700081,115.167886 38.700083,115.167912 38.70008,115.167937 38.700074,115.168447 38.699958,115"
              + ".168477 38.699949,115.1685 38.699936,115.168511 38.699916,115.168518 38.699897,115.1685 38.699727,"
              + "115.168494 38.699705,115.168476 38.699691,115.168452 38.699681,115.168425 38.699681,115.167828 38"
              + ".699746,115.167798 38.699752,115.167775 38.699763,115.167752 38.69978,115.167739 38.699809,115"
              + ".167739 38.699838,115.167803 38.70004))";
      Geometry polygon = GeoUtils.fromWKT(wktPolygon);
      List<String> s2CellIdList = GeoUtils.getCoveredS2CellIdList(polygon);
      List<String> actualCellIdList = Lists.newArrayList("35e5c419");
      Assert.assertTrue(s2CellIdList.size() == actualCellIdList.size());
      Assert.assertTrue(actualCellIdList.containsAll(s2CellIdList));

      String wktPoint = "POINT(119.431212 32.214768)";
      Geometry point = GeoUtils.fromWKT(wktPoint);
      s2CellIdList = GeoUtils.getCoveredS2CellIdList(point);
      actualCellIdList = Lists.newArrayList("35b42ac5");
      Assert.assertTrue(s2CellIdList.size() == actualCellIdList.size());
      Assert.assertTrue(actualCellIdList.containsAll(s2CellIdList));

      String wktLineString =
          "LINESTRING(116.506619 39.945368,116.509562 39.945402,116.509474 39.943348,116"
              + ".506648 39.943247)";
      Geometry lineString = GeoUtils.fromWKT(wktLineString);
      s2CellIdList = GeoUtils.getCoveredS2CellIdList(lineString);
      actualCellIdList = Lists.newArrayList("35f1abf9", "35f1abfb", "35f1abfd", "35f1abff");
      Assert.assertTrue(actualCellIdList.size() == s2CellIdList.size());
      Assert.assertTrue(actualCellIdList.containsAll(s2CellIdList));

      String wktMultiPoint =
          "MULTIPOINT(116.506619 39.945368,116.509562 39.945402,116.509474 39.943348,116"
              + ".506648 39.943247)";
      Geometry multiPoint = GeoUtils.fromWKT(wktMultiPoint);
      s2CellIdList = GeoUtils.getCoveredS2CellIdList(multiPoint);
      actualCellIdList = Lists.newArrayList("35f1abf9", "35f1abfb", "35f1abfd", "35f1abff");
      Assert.assertTrue(actualCellIdList.size() == s2CellIdList.size());
      Assert.assertTrue(actualCellIdList.containsAll(s2CellIdList));

      String wktMultiPolygon =
          "MULTIPOLYGON(((116.391957 39.903899, 116.391976 39.903602, 116.39202 39.903511,"
              + " 116.392074 39.90344, 116.392141 39.903372, 116.392205 39.903338, 116.392479 39.903196, 116.392648 "
              + "39.9031, 116.392797 39.903017, 116.392884 39.902985, 116.393052 39.902935, 116.393135 39.902916, 116"
              + ".393235 39.902905, 116.393636 39.902907, 116.395554 39.902979, 116.395596 39.90299, 116.395641 39"
              + ".903007, 116.395679 39.903028, 116.39572 39.903053, 116.395754 39.903084, 116.39579 39.903136, 116"
              + ".395765 39.903648, 116.395676 39.904954, 116.395619 39.905952, 116.395562 39.906959, 116.395527 39"
              + ".907001, 116.395488 39.907035, 116.395409 39.90708, 116.395354 39.907101, 116.395291 39.907108, 116"
              + ".394617 39.907091, 116.393651 39.907065, 116.392053 39.907002, 116.392016 39.906996, 116.391986 39"
              + ".90698, 116.391953 39.906961, 116.391926 39.906944, 116.391892 39.906915, 116.391878 39.906892, 116"
              + ".391855 39.906859, 116.391842 39.906812, 116.391832 39.906707, 116.391854 39.906041, 116.391893 39"
              + ".905375, 116.391957 39.903899)), ((116.388391 39.9061585, 116.3880048 39.9037059, 116.3915024 39"
              + ".9037553, 116.3912556 39.9062079, 116.388391 39.9061585)))";
      Geometry multiPolygon = GeoUtils.fromWKT(wktMultiPolygon);
      s2CellIdList = GeoUtils.getCoveredS2CellIdList(multiPolygon);
      actualCellIdList =
          Lists.newArrayList("35f05295", "35f05297", "35f052bd", "35f05291", "35f05297");
      Assert.assertTrue(actualCellIdList.size() == s2CellIdList.size());
      Assert.assertTrue(actualCellIdList.containsAll(s2CellIdList));

      String innerHolePolygonWkt =
          "POLYGON ((116.3887123 39.9060117, 116.3880793 39.9046866, 116.3878969 39.9033533, 116.3904611 39.9026949, 116"
              + ".3921992 39.9039377, 116.3921133 39.9061269, 116.3887123 39.9060117), (116.3891736 39.9053121, 116.3893238 39"
              + ".9036743, 116.3911263 39.9037648, 116.3910726 39.9054027, 116.3891736 39.9053121))";
      Geometry innerHolePolygon = GeoUtils.fromWKT(innerHolePolygonWkt);
      s2CellIdList = GeoUtils.getCoveredS2CellIdList(innerHolePolygon);
      actualCellIdList = Lists.newArrayList("24b2d34b", "35f05291", "35f05297", "3b4d2cb5");
      Assert.assertTrue(actualCellIdList.size() == s2CellIdList.size());
      Assert.assertTrue(actualCellIdList.containsAll(s2CellIdList));
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }

  @Test
  public void testDistance() {
    String wkt1 =
        "POLYGON((116.391957 39.903899, 116.391976 39.903602, 116.39202 39.903511,"
            + " 116.392074 39.90344, 116.392141 39.903372, 116.392205 39.903338, 116.392479 39.903196, 116.392648 "
            + "39.9031, 116.392797 39.903017, 116.392884 39.902985, 116.393052 39.902935, 116.393135 39.902916, 116"
            + ".393235 39.902905, 116.393636 39.902907, 116.395554 39.902979, 116.395596 39.90299, 116.395641 39"
            + ".903007, 116.395679 39.903028, 116.39572 39.903053, 116.395754 39.903084, 116.39579 39.903136, 116"
            + ".395765 39.903648, 116.395676 39.904954, 116.395619 39.905952, 116.395562 39.906959, 116.395527 39"
            + ".907001, 116.395488 39.907035, 116.395409 39.90708, 116.395354 39.907101, 116.395291 39.907108, 116"
            + ".394617 39.907091, 116.393651 39.907065, 116.392053 39.907002, 116.392016 39.906996, 116.391986 39"
            + ".90698, 116.391953 39.906961, 116.391926 39.906944, 116.391892 39.906915, 116.391878 39.906892, 116"
            + ".391855 39.906859, 116.391842 39.906812, 116.391832 39.906707, 116.391854 39.906041, 116.391893 39"
            + ".905375, 116.391957 39.903899))";
    String wkt2 =
        "POLYGON ((116.388391 39.9061585, 116.3880048 39.9037059, 116.3915024 39"
            + ".9037553, 116.3912556 39.9062079, 116.388391 39.9061585))";
    try {
      Geometry g1 = GeoUtils.fromWKT(wkt1);
      Geometry g2 = GeoUtils.fromWKT(wkt2);
      double distance = GeoUtils.distance(g1, g2);
      Assert.assertTrue(distance < 40);
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }

  @Test
  public void testIsIntersects() {
    try {
      String wkt1 =
          "POLYGON ((116.391957 39.903899, 116.391976 39.903602, 116.39202 39.903511, 116.392074 39"
              + ".90344, 116.392141 39.903372, 116.392205 39.903338, 116.392479 39.903196, 116.392648 39.9031, 116"
              + ".392797 39.903017, 116.392884 39.902985, 116.393052 39.902935, 116.393135 39.902916, 116.393235 39"
              + ".902905, 116.393636 39.902907, 116.395554 39.902979, 116.395596 39.90299, 116.395641 39.903007, 116"
              + ".395679 39.903028, 116.39572 39.903053, 116.395754 39.903084, 116.39579 39.903136, 116.395765 39"
              + ".903648, 116.395676 39.904954, 116.395619 39.905952, 116.395562 39.906959, 116.395527 39.907001, 116"
              + ".395488 39.907035, 116.395409 39.90708, 116.395354 39.907101, 116.395291 39.907108, 116.394617 39"
              + ".907091, 116.393651 39.907065, 116.392053 39.907002, 116.392016 39.906996, 116.391986 39.90698, 116"
              + ".391953 39.906961, 116.391926 39.906944, 116.391892 39.906915, 116.391878 39.906892, 116.391855 39"
              + ".906859, 116.391842 39.906812, 116.391832 39.906707, 116.391854 39.906041, 116.391893 39.905375, 116"
              + ".391957 39.903899))";
      String wkt2 =
          "POLYGON ((116.3937307 39.9044136, 116.393677 39.9017799, 116.397475 39.9016976, 116"
              + ".3973785 39.9044548, 116.3937307 39.9044136))";
      String wkt3 =
          "POLYGON ((116.3883368 39.906094, 116.38809 39.9036332, 116.3913301 39.9035426, 116.3911477"
              + " 39.9062092, 116.3883368 39.906094))";

      Geometry g1 = GeoUtils.fromWKT(wkt1);
      Geometry g2 = GeoUtils.fromWKT(wkt2);
      Geometry g3 = GeoUtils.fromWKT(wkt3);
      Assert.assertTrue(GeoUtils.isIntersects(g1, g2));
      Assert.assertFalse(GeoUtils.isIntersects(g1, g3));
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }

  @Test
  public void testIntersectsArea() {
    try {
      String wkt1 =
          "POLYGON ((116.391957 39.903899, 116.391976 39.903602, 116.39202 39.903511, 116.392074 39"
              + ".90344, 116.392141 39.903372, 116.392205 39.903338, 116.392479 39.903196, 116.392648 39.9031, 116"
              + ".392797 39.903017, 116.392884 39.902985, 116.393052 39.902935, 116.393135 39.902916, 116.393235 39"
              + ".902905, 116.393636 39.902907, 116.395554 39.902979, 116.395596 39.90299, 116.395641 39.903007, 116"
              + ".395679 39.903028, 116.39572 39.903053, 116.395754 39.903084, 116.39579 39.903136, 116.395765 39"
              + ".903648, 116.395676 39.904954, 116.395619 39.905952, 116.395562 39.906959, 116.395527 39.907001, 116"
              + ".395488 39.907035, 116.395409 39.90708, 116.395354 39.907101, 116.395291 39.907108, 116.394617 39"
              + ".907091, 116.393651 39.907065, 116.392053 39.907002, 116.392016 39.906996, 116.391986 39.90698, 116"
              + ".391953 39.906961, 116.391926 39.906944, 116.391892 39.906915, 116.391878 39.906892, 116.391855 39"
              + ".906859, 116.391842 39.906812, 116.391832 39.906707, 116.391854 39.906041, 116.391893 39.905375, 116"
              + ".391957 39.903899))";
      String wkt2 =
          "POLYGON ((116.3937307 39.9044136, 116.393677 39.9017799, 116.397475 39.9016976, 116"
              + ".3973785 39.9044548, 116.3937307 39.9044136))";
      String wkt3 =
          "POLYGON ((116.3883368 39.906094, 116.38809 39.9036332, 116.3913301 39.9035426, 116.3911477"
              + " 39.9062092, 116.3883368 39.906094))";

      Geometry g1 = GeoUtils.fromWKT(wkt1);
      Geometry g2 = GeoUtils.fromWKT(wkt2);
      Geometry g3 = GeoUtils.fromWKT(wkt3);
      Double area = GeoUtils.intersectsArea(g1, g2);
      Double area2 = GeoUtils.intersectsArea(g1, g3);
      Assert.assertTrue(area < 30000);
      Assert.assertTrue(area2 < 0);
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }

  @Test
  public void testIntersectsShape() {
    try {
      String wkt1 =
          "POLYGON ((116.391957 39.903899, 116.391976 39.903602, 116.39202 39.903511, 116.392074 39"
              + ".90344, 116.392141 39.903372, 116.392205 39.903338, 116.392479 39.903196, 116.392648 39.9031, 116"
              + ".392797 39.903017, 116.392884 39.902985, 116.393052 39.902935, 116.393135 39.902916, 116.393235 39"
              + ".902905, 116.393636 39.902907, 116.395554 39.902979, 116.395596 39.90299, 116.395641 39.903007, 116"
              + ".395679 39.903028, 116.39572 39.903053, 116.395754 39.903084, 116.39579 39.903136, 116.395765 39"
              + ".903648, 116.395676 39.904954, 116.395619 39.905952, 116.395562 39.906959, 116.395527 39.907001, 116"
              + ".395488 39.907035, 116.395409 39.90708, 116.395354 39.907101, 116.395291 39.907108, 116.394617 39"
              + ".907091, 116.393651 39.907065, 116.392053 39.907002, 116.392016 39.906996, 116.391986 39.90698, 116"
              + ".391953 39.906961, 116.391926 39.906944, 116.391892 39.906915, 116.391878 39.906892, 116.391855 39"
              + ".906859, 116.391842 39.906812, 116.391832 39.906707, 116.391854 39.906041, 116.391893 39.905375, 116"
              + ".391957 39.903899))";
      String wkt2 =
          "POLYGON ((116.3937307 39.9044136, 116.393677 39.9017799, 116.397475 39.9016976, 116"
              + ".3973785 39.9044548, 116.3937307 39.9044136))";
      String wkt3 =
          "POLYGON ((116.3883368 39.906094, 116.38809 39.9036332, 116.3913301 39.9035426, 116.3911477"
              + " 39.9062092, 116.3883368 39.906094))";
      String wkt4 =
          "LINESTRING (116.3924259 39.9056232, 116.3938207 39.9059442, 116.3945288 39.9050389)";

      Geometry g1 = GeoUtils.fromWKT(wkt1);
      Geometry g2 = GeoUtils.fromWKT(wkt2);
      Geometry g3 = GeoUtils.fromWKT(wkt3);
      Geometry g4 = GeoUtils.fromWKT(wkt4);
      String shape1 = GeoUtils.intersectsShape(g1, g2);
      String shape2 = GeoUtils.intersectsShape(g1, g3);
      String shape3 = GeoUtils.intersectsShape(g1, g4);

      Assert.assertTrue(
          shape1.equals(
              "POLYGON ((116.395711 39.904436, 116.395765 39.903648, 116.39579 39.903136,"
                  + " 116.395754 39.903084, 116.39572 39.903053, 116.395679 39.903028, 116.395641 39.903007, 116.395596 "
                  + "39.90299, 116.395554 39.902979, 116.3937 39.902909, 116.393731 39.904414, 116.395711 39.904436))"));
      Assert.assertTrue(shape2.equals("POLYGON EMPTY"));
      Assert.assertTrue(
          shape3.equals(
              "LINESTRING (116.392426 39.905623, 116.393821 39.905944, 116.394529 39"
                  + ".905039)"));
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }

  @Test
  public void testWithin() {
    try {
      String wkt1 = "POINT (116.3896028 39.905164)";
      String wkt2 =
          "POLYGON ((116.3883368 39.906094, 116.38809 39.9036332, 116.3913301 39.9035426, 116.3911477"
              + " 39.9062092, 116.3883368 39.906094))";
      Geometry g1 = GeoUtils.fromWKT(wkt1);
      Geometry g2 = GeoUtils.fromWKT(wkt2);
      Assert.assertTrue(GeoUtils.within(g1, g2));
      Assert.assertFalse(GeoUtils.within(g2, g1));
    } catch (Exception e) {
      Assert.assertTrue(false);
    }
  }
}
