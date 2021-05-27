package com.softwaremill.diffx.generic

import com.softwaremill.diffx.{Diff, DiffAny, DiffCoproduct, DiffProduct, ProductField}
import magnolia._

trait DiffMagnoliaDerivation extends LowPriority {
  type Typeclass[T] = Diff[T]

  def combine[T](ctx: ReadOnlyCaseClass[Typeclass, T]): Diff[T] = {
    DiffProduct(
      ctx.typeName.short,
      ctx.parameters.map { p =>
        ProductField(p.label, p.typeclass, t => p.dereference(t))
      }.toList
    )
  }

  def dispatch[T](ctx: SealedTrait[Typeclass, T]): Diff[T] = {
    DiffCoproduct(ctx.typeName.short, ctx)
  }
}

trait LowPriority {
  def fallback[T]: Diff[T] = new DiffAny[T]
}
