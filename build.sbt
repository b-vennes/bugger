import scala.sys.process._
import scala.language.postfixOps

import sbtwelcome._
import indigoplugin._

Global / onChangedBuildSource := ReloadOnSourceChanges

Test / scalaJSLinkerConfig ~= { _.withModuleKind(ModuleKind.CommonJSModule) }

lazy val gameOptions: IndigoOptions =
  IndigoOptions.defaults
    .withTitle("Bugger")
    .withWindowSize(550, 400)
    .withBackgroundColor("black")
    .withAssetDirectory("assets")
    .excludeAssets {
      case p if p.endsWith(os.RelPath.rel / ".gitkeep") => true
      case _                                            => false
    }

lazy val bugger =
  (project in file("."))
    .enablePlugins(ScalaJSPlugin, SbtIndigo)
    .settings(
      logo := "Bugger (v" + version.value.toString + ")",
      usefulTasks := Seq(
        UsefulTask("runGame", "Run the game").noAlias,
        UsefulTask("buildGame", "Build web version").noAlias,
        UsefulTask("runGameFull", "Run the fully optimised game").noAlias,
        UsefulTask("buildGameFull", "Build the fully optimised web version").noAlias,
      ),
      logoColor        := scala.Console.MAGENTA,
      aliasColor       := scala.Console.YELLOW,
      commandColor     := scala.Console.CYAN,
      descriptionColor := scala.Console.WHITE,
      version      := "0.0.1",
      scalaVersion := "3.4.1",
      organization := "com.brandenvennes",
      libraryDependencies ++= Seq(
        "org.scalameta" %%% "munit" % "0.7.29" % Test
      ),
      testFrameworks += new TestFramework("munit.Framework"),
      scalafixOnCompile  := true,
      semanticdbEnabled  := true,
      semanticdbVersion  := scalafixSemanticdb.revision,
      indigoOptions := gameOptions,
      Compile / sourceGenerators += Def.task {
        IndigoGenerators("com.brandenvennes.generated")
          .generateConfig("Config", gameOptions)
          .listAssets("Assets", gameOptions.assets)
          .toSourceFiles((Compile / sourceManaged).value)
      },
      libraryDependencies ++= Seq(
        "io.indigoengine" %%% "indigo-json-circe" % "0.17.0",
        "io.indigoengine" %%% "indigo"            % "0.17.0",
        "io.indigoengine" %%% "indigo-extras"     % "0.17.0",
        "dev.optics" %%% "monocle-core"  % "3.2.0",
        "dev.optics" %%% "monocle-macro" % "3.2.0",
      ),
    )

addCommandAlias(
  "buildGame",
  List(
    "compile",
    "fastLinkJS",
    "indigoBuild"
  ).mkString(";", ";", "")
)
addCommandAlias(
  "buildGameFull",
  List(
    "compile",
    "fullLinkJS",
    "indigoBuildFull"
  ).mkString(";", ";", "")
)
addCommandAlias(
  "runGame",
  List(
    "compile",
    "fastLinkJS",
    "indigoRun"
  ).mkString(";", ";", "")
)
addCommandAlias(
  "runGameFull",
  List(
    "bugger/compile",
    "bugger/fullLinkJS",
    "bugger/indigoRunFull"
  ).mkString(";", ";", "")
)