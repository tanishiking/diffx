package com.softwaremill.diffx.ziotest

import zio.test._
import zio.test.Assertion._

object TestData {
  sealed trait Parent
  case class Bar(s: String, i: Int) extends Parent
  case class Foo(bar: Bar, b: List[Int], parent: Option[Parent]) extends Parent

  val right: Foo = Foo(
    Bar("asdf", 5),
    List(123, 1234),
    Some(Bar("asdf", 5))
  )
  val left: Foo = Foo(
    Bar("asdf", 66),
    List(1234),
    Some(right)
  )

}

import TestData._

import DiffAssertion._

object DiffAssertionSpec
  extends DefaultRunnableSpec(
    suite("DiffAssertion")(
      test("should pretty print the output") {
        assert(right, matchTo(left))
      }
    )
  )
