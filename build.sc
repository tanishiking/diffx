import mill._
import mill.scalalib._
import mill.scalalib.scalafmt.ScalafmtModule
import $ivy.`io.github.davidgregory084::mill-tpolecat:0.1.4`
import io.github.davidgregory084.TpolecatModule

val scalatestVersion = "3.2.2"

object core extends Cross[CoreModule]( "2.12.8","2.13.1")
class CoreModule(val crossScalaVersion: String) extends CrossSbtModule with ScalafmtModule with TpolecatModule {
  def scalacOptions = T { super.scalacOptions().filterNot(Set("-Xfatal-warnings","-Wunused:nowarn" )) }
  override def ivyDeps = Agg(
    ivy"com.lihaoyi::acyclic:0.2.0",
    ivy"com.propensive::magnolia:0.17.0",
    ivy"org.scala-lang:scala-reflect:$crossScalaVersion"
  )
  override def scalacPluginIvyDeps = Agg(ivy"com.lihaoyi::acyclic:0.2.0",
    ivy"org.typelevel::kind-projector:0.10.3")

  object test extends Tests {
    override def ivyDeps = Agg(
      ivy"org.scalatest::scalatest-flatspec:$scalatestVersion",
      ivy"org.scalatest::scalatest-freespec:$scalatestVersion",
      ivy"org.scalatest::scalatest-shouldmatchers:$scalatestVersion"
    )
    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }


}



