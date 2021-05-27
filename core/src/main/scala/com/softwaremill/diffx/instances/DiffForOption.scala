package com.softwaremill.diffx.instances

import com.softwaremill.diffx._

private[diffx] class DiffForOption[T](dt: Diff[T]) extends Diff[Option[T]] {
  override def compare(left: Option[T], right: Option[T]): DiffResult = {
    (left, right) match {
      case (Some(l), Some(r)) => dt.compare(l, r)
      case (None, None)       => Identical(None)
      case (l, r)             => DiffResultValue(l, r)
    }
  }
}
