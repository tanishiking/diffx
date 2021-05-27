package com.softwaremill.diffx
import com.softwaremill.diffx.generic.{DiffMagnoliaDerivation, MagnoliaDerivedMacro}
import com.softwaremill.diffx.instances._
import magnolia.SealedTrait

import scala.collection.immutable.ListMap

trait Diff[-T] {
  def apply(left: T, right: T): DiffResult = compare(left, right)
  def compare(left: T, right: T): DiffResult
  def contramap[TT](g: TT => T): Diff[TT] = DiffMapped(this, g)
}

object Diff extends MiddlePriorityDiff {
  def apply[T: Diff]: Diff[T] = implicitly[Diff[T]]

  def identical[T]: Diff[T] = (left: T, _: T) => Identical(left)

  def compare[T: Diff](left: T, right: T): DiffResult = apply[T].compare(left, right)

  /** Create a Diff instance using [[Object#equals]] */
  def useEquals[T]: Diff[T] = Diff.fallback[T]

  def derived[T]: Derived[Diff[T]] = macro MagnoliaDerivedMacro.derivedGen[T]

  implicit val diffForString: Diff[String] = new DiffForString
  implicit val diffForRange: Diff[Range] = Diff.useEquals[Range]
  implicit val diffForChar: Diff[Char] = Diff.useEquals[Char]
  implicit val diffForBoolean: Diff[Boolean] = Diff.useEquals[Boolean]

  implicit def diffForNumeric[T: Numeric]: Diff[T] = new DiffForNumeric[T]
  implicit def diffForMap[K, V, C[KK, VV] <: scala.collection.Map[KK, VV]](implicit
      ddot: Diff[Option[V]],
      ddk: Diff[K],
      matcher: ObjectMatcher[K]
  ): Diff[C[K, V]] = new DiffForMap[K, V, C](matcher, ddk, ddot)
  implicit def diffForOptional[T](implicit ddt: Diff[T]): Diff[Option[T]] = new DiffForOption[T](ddt)
  implicit def diffForSet[T, C[W] <: scala.collection.Set[W]](implicit
      ddt: Diff[T],
      matcher: ObjectMatcher[T]
  ): Diff[C[T]] = new DiffForSet[T, C](ddt, matcher)
  implicit def diffForEither[L, R](implicit ld: Diff[L], rd: Diff[R]): Diff[Either[L, R]] =
    new DiffForEither[L, R](ld, rd)
}

trait ProductField[T] {
  type FieldType
  def name: String
  def get: T => FieldType
  def diff: Diff[FieldType]
  def map(f: Diff[FieldType] => Diff[FieldType]): ProductField[T]
}
object ProductField {
  def apply[F, T](_name: String, _diff: Diff[F], _get: T => F): ProductField[T] = new ProductField[T] {
    override type FieldType = F
    override def name: String = _name
    override def get: T => FieldType = _get
    override def diff: Diff[FieldType] = _diff
    override def map(f: Diff[F] => Diff[F]): ProductField[T] = ProductField(_name, f(_diff), _get)
  }
}

case class DiffProduct[T](name: String, fields: ListMap[String, ProductField[T]]) extends Diff[T] {
  def compare(left: T, right: T): DiffResult = {
    nullGuard(left, right) { (leftNn, rightNn) =>
      val result = fields.map { case (fieldName, fieldValue) =>
        val lValue = fieldValue.get(leftNn)
        val rValue = fieldValue.get(rightNn)
        fieldName -> fieldValue.diff.compare(lValue, rValue)
      }.toList
      if (result.forall { case (_, v) => v.isIdentical }) {
        Identical(leftNn)
      } else {
        DiffResultObject(name, ListMap(result: _*))
      }
    }
  }
  def modify[U](path: T => U)(f: Diff[T] => Diff[T]): Diff[U] = ???

  // person.age.value
  def unsafeModifyAtPath[F](fieldPath: FieldPath)(f: Diff[F] => Diff[F]): Diff[T] = {
    fieldPath match {
      case Nil => f(this.asInstanceOf[Diff[F]]).asInstanceOf[Diff[T]]
      case head :: tail =>
        val productField = fields.getOrElse(head, throw new RuntimeException(s"Cannot find field: $head in $name"))
        val updated = productField.diff match {
          case d: DiffProduct[_] => d.unsafeModifyAtPath(tail)(f)
        }
        copy(fields = fields + (head -> productField.map(_ => updated.asInstanceOf[Diff[productField.FieldType]])))
    }
  }

}

case class DiffMapped[T, TT](wrapped: Diff[T], g: TT => T) extends Diff[TT] {
  override def compare(left: TT, right: TT): DiffResult = {
    wrapped.compare(g(left), g(right))
  }
}

case class DiffCoproduct[T](name: String, ctx: SealedTrait[Diff, T]) extends Diff[T] {
  override def compare(left: T, right: T): DiffResult = {
    nullGuard(left, right) { (left, right) =>
      val lType = ctx.dispatch(left)(a => a)
      val rType = ctx.dispatch(right)(a => a)
      if (lType == rType) {
        lType.typeclass.compare(lType.cast(left), lType.cast(right))
      } else {
        DiffResultValue(lType.typeName.full, rType.typeName.full)
      }
    }
  }
}

class DiffAny[T] extends Diff[T] {
  override def compare(left: T, right: T): DiffResult = {
    if (left != right) {
      DiffResultValue(left, right)
    } else {
      Identical(left)
    }
  }
}

trait MiddlePriorityDiff extends DiffMagnoliaDerivation with LowPriorityDiff {

  implicit def diffForIterable[T, C[W] <: Iterable[W]](implicit
      ddot: Diff[Option[T]]
  ): Diff[C[T]] = new DiffForIterable[T, C](ddot)
}

trait LowPriorityDiff {
  // Implicit instance of Diff[T] created from implicit Derived[Diff[T]]
  implicit def derivedDiff[T](implicit dd: Derived[Diff[T]]): Diff[T] = dd.value

  implicit class RichDerivedDiff[T](val dd: Derived[Diff[T]]) {
    def contramap[R](f: R => T): Derived[Diff[R]] = Derived(dd.value.contramap(f))
  }
}

case class Derived[T](value: T)

object Derived {
  def apply[T: Derived]: Derived[T] = implicitly[Derived[T]]
}
