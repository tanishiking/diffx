package com.softwaremill.diffx.instances

import com.softwaremill.diffx.{Diff, DiffResult, DiffResultValue}

private[diffx] class DiffForEither[L, R](ld: Diff[L], rd: Diff[R]) extends Diff[Either[L, R]] {
  override def compare(left: Either[L, R], right: Either[L, R]): DiffResult = {
    (left, right) match {
      case (Left(v1), Left(v2))   => ld.compare(v1, v2)
      case (Right(v1), Right(v2)) => rd.compare(v1, v2)
      case (v1, v2)               => DiffResultValue(v1, v2)
    }
  }
}
