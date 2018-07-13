package org.scalanative.bindgen.samples

import scala.scalanative._
import scala.scalanative.native._

@native.link("bindgentests")
@native.extern
object Struct {
  type struct_point = native.CStruct2[native.CInt, native.CInt]
  type point = struct_point
  type struct_points = native.CStruct2[struct_point, point]
  type enum_pointIndex = native.CUnsignedInt
  type point_s = native.Ptr[struct_point]
  type struct_bigStruct = native.CArray[Byte, native.Nat.Digit[native.Nat._1, native.Nat.Digit[native.Nat._1, native.Nat._2]]]
  type struct_structWithAnonymousStruct = native.CStruct2[native.CInt, native.CArray[Byte, native.Nat._8]]
  def setPoints(points: native.Ptr[struct_points], x1: native.CInt, y1: native.CInt, x2: native.CInt, y2: native.CInt): Unit = native.extern
  def getPoint(points: native.Ptr[struct_points], pointIndex: enum_pointIndex): native.CInt = native.extern
  def createPoint(): native.Ptr[struct_point] = native.extern
  def getBigStructSize(): native.CInt = native.extern
  def getCharFromAnonymousStruct(s: native.Ptr[struct_structWithAnonymousStruct]): native.CChar = native.extern
  def getIntFromAnonymousStruct(s: native.Ptr[struct_structWithAnonymousStruct]): native.CChar = native.extern
}

import Struct._

object StructEnums {
  final val enum_pointIndex_X1: enum_pointIndex = 0.toUInt
  final val enum_pointIndex_Y1: enum_pointIndex = 1.toUInt
  final val enum_pointIndex_X2: enum_pointIndex = 2.toUInt
  final val enum_pointIndex_Y2: enum_pointIndex = 3.toUInt
}

object StructHelpers {

  implicit class struct_point_ops(val p: native.Ptr[struct_point]) extends AnyVal {
    def x: native.CInt = !p._1
    def x_=(value: native.CInt): Unit = !p._1 = value
    def y: native.CInt = !p._2
    def y_=(value: native.CInt): Unit = !p._2 = value
  }

  def struct_point()(implicit z: native.Zone): native.Ptr[struct_point] = native.alloc[struct_point]

  implicit class struct_points_ops(val p: native.Ptr[struct_points]) extends AnyVal {
    def p1: native.Ptr[struct_point] = p._1
    def p1_=(value: native.Ptr[struct_point]): Unit = !p._1 = !value
    def p2: native.Ptr[point] = p._2
    def p2_=(value: native.Ptr[point]): Unit = !p._2 = !value
  }

  def struct_points()(implicit z: native.Zone): native.Ptr[struct_points] = native.alloc[struct_points]

  implicit class struct_structWithAnonymousStruct_ops(val p: native.Ptr[struct_structWithAnonymousStruct]) extends AnyVal {
    def a: native.CInt = !p._1
    def a_=(value: native.CInt): Unit = !p._1 = value
    def anonymousStruct: native.Ptr[native.CArray[Byte, native.Nat._8]] = p._2
    def anonymousStruct_=(value: native.Ptr[native.CArray[Byte, native.Nat._8]]): Unit = !p._2 = !value
  }

  def struct_structWithAnonymousStruct()(implicit z: native.Zone): native.Ptr[struct_structWithAnonymousStruct] = native.alloc[struct_structWithAnonymousStruct]
}