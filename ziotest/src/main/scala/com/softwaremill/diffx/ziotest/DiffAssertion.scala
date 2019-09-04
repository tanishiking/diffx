package com.softwaremill.diffx.ziotest

import com.softwaremill.diffx.{DiffFor, DiffForInstances, DiffResultDifferent}
import zio.test.{AssertResult, Assertion}

object DiffAssertion extends DiffForInstances {

  final def matchTo[A: DiffFor](expected: A): Assertion[A] =
    Assertion.assertion(s"matchTo(${expected})") { actual =>
      implicitly[DiffFor[A]].apply(actual, expected) match {
        case c: DiffResultDifferent =>
          println(c.show)
          AssertResult.failure(())
        case _ => AssertResult.success(())
      }
    }
}
