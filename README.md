![diffx](https://github.com/softwaremill/diffx/raw/master/banner.png)

[![Build Status](https://travis-ci.org/softwaremill/diffx.svg?branch=master)](https://travis-ci.org/softwaremill/diffx)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.softwaremill.diffx/diffx-core_2.13/badge.svg)](https://search.maven.org/search?q=g:com.softwaremill.diffx)
[![Gitter](https://badges.gitter.im/softwaremill/diffx.svg)](https://gitter.im/softwaremill/diffx?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://gh.mergify.io/badges/softwaremill/diffx&style=flat)](https://mergify.io)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-brightgreen.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)

Pretty diffs for case classes. 

The library is published for Scala 2.12 and 2.13.

## Table of contents
- integrations
  - [scalatest](#scalatest-integration)
  - [specs2](#specs2-integration)
- [direct usage](#using-directly)
- [customization](#customization)
- [ignoring](#ignoring)
- [tagging support](#tagging-support)
- [similar projects](#similar-projects)
- [commercial support](#commercial-support)

## Scalatest integration

To use with scalatest, add the following dependency:

```scala
"com.softwaremill.diffx" %% "diffx-scalatest" % "0.3.9"
```

Then, extend the `com.softwaremill.diffx.scalatest.DiffMatcher` trait or `import com.softwaremill.diffx.scalatest.DiffMatcher._`.
After that you will be able to use syntax such as:

```scala
left should matchTo(right)
```

Giving you nice error messages:

![example](https://github.com/softwaremill/diff-x/blob/master/example.png?raw=true)

## Specs2 integration

To use with specs2, add the following dependency:

```scala
"com.softwaremill.diffx" %% "diffx-specs2" % "0.3.9"
```

Then, extend the `com.softwaremill.diffx.specs2.DiffMatcher` trait or `import com.softwaremill.diffx.specs2.DiffMatcher._`.
After that you will be able to use syntax such as:

```scala
left must matchTo(right)
```

## Using directly

Add the following dependency:

```scala
"com.softwaremill.diffx" %% "diffx-core" % "0.3.9"
```

Then call:

```scala
import com.softwaremill.diffx.Diff

Diff[T].apply(o1, o2)
```

## Customization

If you'd like to implement custom matching logic for the given type, create an implicit `Diff` instance for that 
type, and make sure it's in scope when any any `Diff` instances depending on that type are created.


## Ignoring

Fields can be excluded from comparision by calling the `ignore` method on the `Diff` instance.
Since `Diff` instances are immutable, the `ignore` method creates a copy of the instance with modified logic.
You can use this instance explicitly.
If you still would like to use it implicitly, you first need to summon the instance of the `Diff` typeclass using
the `Derived` typeclass wrapper: `Derived[Diff[Person]]`. Thanks to that trick, later you will be able to put your modified
instance of the `Diff` typeclass into the implicit scope. The whole process looks as follows:

```scala
implicit modifiedDiff: Diff[Person] = Derived[Diff[Person]].ignore(_.name)
``` 

## Tagging support

To use with softwaremill-tagging library, add the following dependency:

```scala
"com.softwaremill.diffx" %% "diffx-tagging" % "0.3.9"
```

And then extend `com.softwaremill.diffx.tagging.DiffTaggingSupport` trait or `import com.softwaremill.diffx.tagging.DiffTaggingSupport._`

Diffx should be able to work with any other tagging library. I decided to create this module in order to check
if any future changes won't break this integration.

If you want to use diffx with other tagging library you need to provide a way for creating `Diff` type class for tagged types:
```scala
implicit def taggedDiff[T: Diff, U]: Derived[Diff[T @@ U]] = Derived(Diff[T].contramap[T @@ U](identity))
```

## Similar projects

There is a number of similar projects from which diffx draws inspiration.

Below is a list of some of them, which I am aware of, with their main differences:
- [xotai/diff](https://github.com/xdotai/diff) - based on shapeless, seems not to be activly developed anymore
- [ratatool-diffy](https://github.com/spotify/ratatool/tree/master/ratatool-diffy) - the main purpose is to compare large data sets stored on gs or hdfs

## Commercial Support

We offer commercial support for diffx and related technologies, as well as development services. [Contact us](https://softwaremill.com) to learn more about our offer!

## Copyright

Copyright (C) 2019 SoftwareMill [https://softwaremill.com](https://softwaremill.com).
