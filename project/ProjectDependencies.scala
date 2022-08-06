import sbt._

object ProjectDependencies {

  private val catsVersion  = "2.8.0"
  private val munitVersion = "0.7.29"

  lazy val common: Seq[ModuleID] = Seq(
    "org.typelevel" %% "cats-core" % catsVersion,
    "org.scalameta" %% "munit" % munitVersion % Test
  )

  object Plugins {
    val compilerPluginsFor2_13: Seq[ModuleID] = Seq(
      compilerPlugin("org.typelevel" %% "kind-projector" % "0.13.2" cross CrossVersion.full),
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    )

    val compilerPluginsFor3: Seq[ModuleID] = Nil
  }

  object Docs {
    lazy val dedicated: Seq[ModuleID] = Nil
  }

  object Generic {
    val scala2: Seq[ModuleID] = Seq(
      "com.softwaremill.magnolia1_2" %% "magnolia" % "1.1.2",
      "org.scala-lang" % "scala-reflect" % "2.13.8",
      "com.chuusai" %% "shapeless" % "2.3.9"
    )
    val scala3: Seq[ModuleID] = Seq(
      "com.softwaremill.magnolia1_3" %% "magnolia" % "1.1.4"
    )
  }

}
