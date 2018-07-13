package org.scalanative.bindgen.samples

import scala.scalanative._
import scala.scalanative.native._

@native.link("bindgentests")
@native.extern
object ReservedWords {
  type `match` = native.CInt
  type `var` = native.CArray[`match`, native.Nat._5]
  type struct_object = native.CStruct2[`match`, native.CInt]
  type `object` = struct_object
  type `type` = struct_object
  type union_lazy = native.CArray[Byte, native.Nat.Digit[native.Nat._1, native.Nat._6]]
  type `lazy` = union_lazy
  type `def` = `match`
  type struct_finally = native.CStruct2[`def`, `lazy`]
  type `finally` = struct_finally
  def `with`(`sealed`: `match`, `implicit`: native.Ptr[`match`], `forSome`: `lazy`): `type` = native.extern
  def `implicit`(`type`: native.Ptr[`finally`]): `match` = native.extern
  def _1(): Unit = native.extern
}

import ReservedWords._

object ReservedWordsHelpers {

  implicit class struct_object_ops(val p: native.Ptr[struct_object]) extends AnyVal {
    def `yield`: `match` = !p._1
    def `yield_=`(value: `match`): Unit = !p._1 = value
    def `val`: native.CInt = !p._2
    def `val_=`(value: native.CInt): Unit = !p._2 = value
  }

  def struct_object()(implicit z: native.Zone): native.Ptr[struct_object] = native.alloc[struct_object]

  implicit class struct_finally_ops(val p: native.Ptr[struct_finally]) extends AnyVal {
    def `val`: `def` = !p._1
    def `val_=`(value: `def`): Unit = !p._1 = value
    def `finally`: native.Ptr[`lazy`] = p._2
    def `finally_=`(value: native.Ptr[`lazy`]): Unit = !p._2 = !value
  }

  def struct_finally()(implicit z: native.Zone): native.Ptr[struct_finally] = native.alloc[struct_finally]

  implicit class union_lazy_pos(val p: native.Ptr[union_lazy]) extends AnyVal {
    def instance: native.Ptr[native.Ptr[`object`]] = p.cast[native.Ptr[native.Ptr[`object`]]]
    def instance_=(value: native.Ptr[`object`]): Unit = !p.cast[native.Ptr[native.Ptr[`object`]]] = value
    def `forSome`: native.Ptr[`match`] = p.cast[native.Ptr[`match`]]
    def `forSome_=`(value: `match`): Unit = !p.cast[native.Ptr[`match`]] = value
    def `implicit`: native.Ptr[native.CArray[Byte, native.Nat.Digit[native.Nat._1, native.Nat._6]]] = p.cast[native.Ptr[native.CArray[Byte, native.Nat.Digit[native.Nat._1, native.Nat._6]]]]
    def `implicit_=`(value: native.CArray[Byte, native.Nat.Digit[native.Nat._1, native.Nat._6]]): Unit = !p.cast[native.Ptr[native.CArray[Byte, native.Nat.Digit[native.Nat._1, native.Nat._6]]]] = value
  }
}