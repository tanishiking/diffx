package com.softwaremill.diffx.instances

import com.softwaremill.diffx._

private[diffx] class DiffForNumeric[T: Numeric] extends Diff[T] {
  override def compare(left: T, right: T): DiffResult = {
    val numeric = implicitly[Numeric[T]]
    if (!numeric.equiv(left, right)) {
      DiffResultValue(left, right)
    } else {
      Identical(left)
    }
  }
}
