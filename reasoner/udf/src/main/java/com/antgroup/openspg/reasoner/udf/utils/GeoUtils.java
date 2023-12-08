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

package com.antgroup.openspg.reasoner.udf.utils;

import static java.lang.Math.toRadians;

import com.google.common.collect.Lists;
import com.google.common.geometry.S2Cell;
import com.google.common.geometry.S2CellId;
import com.google.common.geometry.S2LatLng;
import com.google.common.geometry.S2Loop;
import com.google.common.geometry.S2Point;
import com.google.common.geometry.S2Polygon;
import com.google.common.geometry.S2Polyline;
import com.google.common.geometry.S2Region;
import com.google.common.geometry.S2RegionCoverer;
import java.util.ArrayList;
import java.util.List;
import org.geotools.geometry.jts.JTS;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.WKTWriter;
import org.locationtech.jts.operation.distance.DistanceOp;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;

/** Some geo-related tools */
public class GeoUtils {
  /** polygon */
  public static final String GEO_TYPE_POLYGON = "Polygon";

  /** lineString */
  public static final String GEO_TYPE_LINE_STRING = "LineString";

  /** multiple point */
  public static final String GEO_TYPE_MULTI_POINT = "MultiPoint";

  /** single point */
  public static final String GEO_TYPE_POINT = "Point";

  /** multiple polygon */
  public static final String GEO_TYPE_MULTI_POLYGON = "MultiPolygon";

  /** S2 LEVEL 14 */
  public static final int GOOGLE_S2_BEST_LEVEL = 14;

  /** max cell size */
  public static final int GOOGLE_S2_MAX_CELL_IDS = 40000;

  /** Earth radius */
  private static final double EARTH_RADIUS = 6367000.0;

  /** Circumference of the earth */
  private static final double EARTH_CIRCUMFERENCE = 40075017.0;

  /**
   * PrecisionModel, with 7 decimal places reserved SRID=4326, which stands for WGS84, the
   * coordinate system of GPS
   */
  private static final GeometryFactory GEOMETRY_FACTORY =
      new GeometryFactory(new PrecisionModel(1000000), 4326);

  /** static wkt reader */
  private static final WKTReader WKT_READER = new WKTReader(GEOMETRY_FACTORY);

  /** static wkt writer */
  private static final WKTWriter WKT_WRITER = new WKTWriter();

  /**
   * convert wktString to Geometry
   *
   * @param wktString
   * @return
   * @throws ParseException
   */
  public static Geometry fromWKT(String wktString) throws ParseException {
    return GeoUtils.WKT_READER.read(wktString);
  }

  /**
   * convert Geometry to wktString
   *
   * @param geometry
   * @return
   */
  public static String toWKT(Geometry geometry) {
    return WKT_WRITER.write(geometry);
  }

  /**
   * Given the latitude and a distance, calculate the latitude corresponding to the distance
   *
   * @param latitude
   * @param meter is in meters
   * @return
   */
  public static double meterToDegree(double latitude, double meter) {
    // Calculate the circumference of the Earth at latitude
    double perimeter = EARTH_CIRCUMFERENCE * Math.cos(toRadians(Math.abs(latitude)));
    // Calculate the meter distance in degrees
    return (meter / perimeter) * 360.0;
  }

  /** Buffer Geometry to get a Polygon */
  public static Geometry buffer(Geometry geometry, double distanceInMeters) {
    double latitude = geometry.getCentroid().getCoordinate().y;
    double degree = meterToDegree(latitude, distanceInMeters);
    return geometry.buffer(degree);
  }

  /** convert lineString to S2 */
  public static S2Polyline toS2Polyline(LineString lineString) {
    List<S2Point> s2PointList = new ArrayList<>();
    for (int i = 0; i < lineString.getNumPoints(); ++i) {
      Point point = lineString.getPointN(i);
      s2PointList.add(S2LatLng.fromDegrees(point.getY(), point.getX()).toPoint());
    }
    return new S2Polyline(s2PointList);
  }

  /** Polygon转换为S2格式 */
  public static S2Polygon toS2Polygon(Polygon polygon) {
    List<S2Loop> s2LoopList = new ArrayList<>();

    LinearRing exteriorRing = polygon.getExteriorRing();
    List<S2Point> s2PointListShell = new ArrayList<>();
    for (int i = 0; i < exteriorRing.getNumPoints(); ++i) {
      Point point = exteriorRing.getPointN(i);
      s2PointListShell.add(S2LatLng.fromDegrees(point.getY(), point.getX()).toPoint());
    }
    S2Loop exteriorLoop = new S2Loop(s2PointListShell);
    if (exteriorLoop.containsOrigin()) {
      exteriorLoop.invert();
    }
    s2LoopList.add(exteriorLoop);

    for (int i = 0; i < polygon.getNumInteriorRing(); ++i) {
      LinearRing interiorRing = polygon.getInteriorRingN(i);
      List<S2Point> s2PointListHoles = new ArrayList<>();
      for (int j = 0; j < interiorRing.getNumPoints(); ++j) {
        Point point = interiorRing.getPointN(j);
        s2PointListHoles.add(new S2Point(point.getY(), point.getX(), 0));
      }
      S2Loop interiorLoop = new S2Loop(s2PointListHoles);
      if (interiorLoop.containsOrigin()) {
        interiorLoop.invert();
      }
      s2LoopList.add(interiorLoop);
    }

    return new S2Polygon(s2LoopList);
  }

  /**
   * Get the s2CellId list of geometry
   *
   * @param geometry
   * @return
   */
  public static List<String> getCoveredS2CellIdList(Geometry geometry) {
    List<S2Region> s2RegionList;
    String type = geometry.getGeometryType();
    if (GeoUtils.GEO_TYPE_POLYGON.equals(type)) {
      Polygon polygon = (Polygon) geometry;
      s2RegionList = Lists.newArrayList(GeoUtils.toS2Polygon(polygon));
    } else if (GeoUtils.GEO_TYPE_POINT.equals(type)) {
      Point point = (Point) geometry;
      S2CellId cellId = S2CellId.fromLatLng(S2LatLng.fromDegrees(point.getY(), point.getX()));
      s2RegionList = Lists.newArrayList(new S2Cell(cellId));
    } else if (GeoUtils.GEO_TYPE_LINE_STRING.equals(type)) {
      LineString lineString = (LineString) geometry;
      s2RegionList = Lists.newArrayList(GeoUtils.toS2Polyline(lineString));
    } else if (GeoUtils.GEO_TYPE_MULTI_POINT.equals(type)) {
      MultiPoint multiPoint = (MultiPoint) geometry;
      s2RegionList = new ArrayList<>(multiPoint.getNumGeometries());
      for (int i = 0; i < multiPoint.getNumGeometries(); ++i) {
        Point point = (Point) multiPoint.getGeometryN(i);
        S2CellId cellId = S2CellId.fromLatLng(S2LatLng.fromDegrees(point.getY(), point.getX()));
        s2RegionList.add(new S2Cell(cellId));
      }
    } else if (GeoUtils.GEO_TYPE_MULTI_POLYGON.equals(type)) {
      MultiPolygon multiPolygon = (MultiPolygon) geometry;
      s2RegionList = new ArrayList<>(multiPolygon.getNumGeometries());
      for (int i = 0; i < multiPolygon.getNumGeometries(); ++i) {
        Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
        s2RegionList.add(GeoUtils.toS2Polygon(polygon));
      }
    } else {
      throw new RuntimeException("unsupported geometry type, " + type);
    }

    S2RegionCoverer regionCover =
        S2RegionCoverer.builder()
            .setMaxLevel(GeoUtils.GOOGLE_S2_BEST_LEVEL)
            .setMinLevel(GeoUtils.GOOGLE_S2_BEST_LEVEL)
            .setMaxCells(GeoUtils.GOOGLE_S2_MAX_CELL_IDS)
            .build();

    List<String> rst = new ArrayList<>();

    for (S2Region s2Region : s2RegionList) {
      ArrayList<S2CellId> covering = new ArrayList<>();
      regionCover.getCovering(s2Region, covering);
      for (S2CellId cellId : covering) {
        rst.add(cellId.toToken());
      }
    }

    return rst;
  }

  /** compute the distance between two geometry */
  public static double distance(Geometry fromGeometry, Geometry toGeometry) {
    DistanceOp distanceOp = new DistanceOp(fromGeometry, toGeometry);
    Coordinate[] coordinates = distanceOp.nearestPoints();
    return GeoUtils.distance(
        coordinates[0].y, coordinates[0].x, coordinates[1].y, coordinates[1].x);
  }

  /**
   * Accurate distance calculation
   *
   * @param lat1
   * @param lng1
   * @param lat2
   * @param lng2
   * @return
   */
  public static double distance(double lat1, double lng1, double lat2, double lng2) {
    // longitude difference
    double dx = lng1 - lng2;
    // latitude difference
    double dy = lat1 - lat2;
    // mean latitude
    double b = (lat1 + lat2) / 2.0;
    // east-west distance
    double lx = toRadians(dx) * EARTH_RADIUS * Math.cos(toRadians(b));
    // north-south distance
    double ly = EARTH_RADIUS * toRadians(dy);
    // rectangular diagonal distance
    return Math.sqrt(lx * lx + ly * ly);
  }

  /**
   * check whether two polygons intersect
   *
   * @param geo1
   * @param geo2
   * @return
   */
  public static Boolean isIntersects(Geometry geo1, Geometry geo2) {
    return geo1.intersects(geo2);
  }

  /**
   * calculate shape between two polygons
   *
   * @param geo1
   * @param geo2
   * @return
   */
  public static String intersectsShape(Geometry geo1, Geometry geo2) {
    // not intersect return Non-Intersects
    if (!geo1.intersects(geo2)) {
      return "POLYGON EMPTY";
    }

    Geometry intersect = geo1.intersection(geo2);
    return toWKT(intersect);
  }

  /**
   * calculate area between two polygons
   *
   * @param geo1
   * @param geo2
   * @return
   */
  public static Double intersectsArea(Geometry geo1, Geometry geo2) {
    // not intersect return -1
    if (!geo1.intersects(geo2)) {
      return -1.0;
    }

    // only calculate area between two polygons
    if (!(GeoUtils.GEO_TYPE_POLYGON.equals(geo1.getGeometryType())
        && GeoUtils.GEO_TYPE_POLYGON.equals(geo2.getGeometryType()))) {
      return 0.0;
    }

    Point c = geo1.getCentroid();
    double x = c.getCoordinate().x;
    double y = c.getCoordinate().y;
    String code = "AUTO:42001," + x + "," + y;
    Geometry p1Trans;
    Geometry p2Trans;
    try {
      CoordinateReferenceSystem auto = CRS.decode(code);
      MathTransform transform = CRS.findMathTransform(DefaultGeographicCRS.WGS84, auto);
      p1Trans = JTS.transform(geo1, transform);
      p2Trans = JTS.transform(geo2, transform);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    Geometry intersect = p1Trans.intersection(p2Trans);
    return intersect.getArea();
  }

  /** check whether geo1 within geo2 */
  public static boolean within(Geometry geo1, Geometry geo2) {
    return geo1.within(geo2);
  }
}
