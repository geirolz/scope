import sbt._

object ProjectDependencies {

  object Plugins {
    val compilerPluginsFor2_13: Seq[ModuleID] = Seq(
      compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    )

    val compilerPluginsFor3: Seq[ModuleID] = Nil
  }

  lazy val common: Seq[ModuleID] = Seq(
    effects,
    tests
  ).flatten

  private val effects: Seq[ModuleID] = {
    Seq(
      "org.typelevel" %% "cats-core" % "2.6.1"
    )
  }

  private val tests: Seq[ModuleID] = Seq(
    "org.scalactic" %% "scalactic" % "3.2.10",
    "org.scalatest" %% "scalatest" % "3.2.10" % Test,
    "org.scalameta" %% "munit" % "0.7.29" % Test
  )
}
