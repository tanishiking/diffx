# diffx: Pretty diffs for case classes

Welcome!

[diffx](https://github.com/softwaremill/diffx) is an open-source library which aims to display differences between 
complex structures in a way that they are easily noticeable.

Here's a quick example of diffx in action:

```scala
sealed trait Parent
case class Bar(s: String, i: Int) extends Parent
case class Foo(bar: Bar, b: List[Int], parent: Option[Parent]) extends Parent

val right: Foo = Foo(
    Bar("asdf", 5),
    List(123, 1234),
    Some(Bar("asdf", 5))
)
// right: Foo = Foo(
//   bar = Bar(s = "asdf", i = 5),
//   b = List(123, 1234),
//   parent = Some(value = Bar(s = "asdf", i = 5))
// )

val left: Foo = Foo(
    Bar("asdf", 66),
    List(1234),
    Some(right)
)
// left: Foo = Foo(
//   bar = Bar(s = "asdf", i = 66),
//   b = List(1234),
//   parent = Some(
//     value = Foo(
//       bar = Bar(s = "asdf", i = 5),
//       b = List(123, 1234),
//       parent = Some(value = Bar(s = "asdf", i = 5))
//     )
//   )
// )
 
import com.softwaremill.diffx.generic.auto._
import com.softwaremill.diffx._
compare(left, right)
// res0: DiffResult = DiffResultObject(
//   name = "Foo",
//   fields = ListMap(
//     "bar" -> DiffResultObject(
//       name = "Bar",
//       fields = ListMap(
//         "s" -> IdenticalValue(value = "asdf"),
//         "i" -> DiffResultValue(left = 66, right = 5)
//       )
//     ),
//     "b" -> DiffResultObject(
//       name = "List",
//       fields = ListMap(
//         "0" -> DiffResultValue(left = 1234, right = 123),
//         "1" -> DiffResultMissing(value = 1234)
//       )
//     ),
//     "parent" -> DiffResultValue(
//       left = "repl.MdocSession.App.Foo",
//       right = "repl.MdocSession.App.Bar"
//     )
//   )
// )
```

Will result in:

![](https://github.com/softwaremill/diffx/blob/master/example.png?raw=true)

`diffx` is available for Scala 3, 2.13 and 2.12 both jvm and js.

The core of `diffx` comes in a single jar.

To integrate with the test framework of your choice, you'll need to use one of the integration modules.
See the section on [test-frameworks](test-frameworks/summary.md) for a brief overview of supported test frameworks.

*Auto-derivation is used throughout the documentation for the sake of clarity. Head over to [derivation](usage/derivation.md) for more details*

## Tips and tricks

You may need to add `-Wmacros:after` Scala compiler option to make sure to check for unused implicits
after macro expansion.
If you get warnings from Magnolia which looks like `magnolia: using fallback derivation for TYPE`,
you can use the [Silencer](https://github.com/ghik/silencer) compiler plugin to silent the warning
with the compiler option `"-P:silencer:globalFilters=^magnolia: using fallback derivation.*$"`

## Similar projects

There is a number of similar projects from which diffx draws inspiration.

Below is a list of some of them, which I am aware of, with their main differences:
- [xotai/diff](https://github.com/xdotai/diff) - based on shapeless, seems not to be activly developed anymore
- [ratatool-diffy](https://github.com/spotify/ratatool/tree/master/ratatool-diffy) - the main purpose is to compare large data sets stored on gs or hdfs
- [difflicious](https://github.com/jatcwang/difflicious) - very similar feature set, different design under the hood, no auto-derivation

## Sponsors

Development and maintenance of diffx is sponsored by [SoftwareMill](https://softwaremill.com), 
a software development and consulting company. We help clients scale their business through software. Our areas of expertise include backends, distributed systems, blockchain, machine learning and data analytics.

[![](https://files.softwaremill.com/logo/logo.png "SoftwareMill")](https://softwaremill.com)

# Table of contents

```eval_rst
.. toctree::
   :maxdepth: 1
   :caption: Test frameworks
   
   test-frameworks/scalatest
   test-frameworks/specs2
   test-frameworks/utest
   test-frameworks/munit
   test-frameworks/summary
   
.. toctree::
   :maxdepth: 1
   :caption: Integrations
   
   integrations/cats
   integrations/tagging
   integrations/refined
   
.. toctree::
   :maxdepth: 1
   :caption: usage
   
   usage/derivation
   usage/ignoring
   usage/replacing
   usage/extending
   usage/sequences
   usage/output
```

## Copyright

Copyright (C) 2019 SoftwareMill [https://softwaremill.com](https://softwaremill.com).
