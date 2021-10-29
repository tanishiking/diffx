package com.softwaremill.diffx.generic

import com.softwaremill.diffx.Diff
import magnolia.Magnolia

package object auto extends AutoDerivation

trait AutoDerivation extends DiffMagnoliaDerivation {
  implicit def diffForCaseClass[T]: Diff[T] = macro Magnolia.gen[T]
}
