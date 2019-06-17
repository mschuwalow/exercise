val ZioVersion        = "1.0.0-RC8-5"
val Slf4jVersion      = "1.7.26"
val ScalaTestVersion  = "3.0.5"
val CatsVersion       = "1.6.1"

lazy val root = (project in file("."))
  .settings(
    organization := "com.schuwalow",
    name := "simplaex-exercise",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.8",
    scalacOptions := Seq(
      "-encoding", "UTF-8",
      "-unchecked",
      "-deprecation",
      "-explaintypes",
      "-feature",
      "-Yrangepos",
      "-language:existentials",
      "-language:higherKinds",
      "-Ypartial-unification",
      "-Xfatal-warnings",
      "-Xfuture",
      "-Xlint:_,-type-parameter-shadow,-infer-any",
      "-Ywarn-numeric-widen",
      "-Ywarn-value-discard",
      "-opt:l:inline",
    ),
    libraryDependencies ++= Seq(
      "dev.zio"                       %% "zio"                      % ZioVersion,
      "dev.zio"                       %% "zio-interop-cats"         % ZioVersion,
      "dev.zio"                       %% "zio-streams"              % ZioVersion,

      "org.typelevel"                 %% "cats-core"                % CatsVersion,

      "org.slf4j"                     %  "slf4j-api"                % Slf4jVersion,
      "org.slf4j"                     %  "slf4j-log4j12"            % Slf4jVersion,

      "com.lihaoyi"                   %% "sourcecode"               % "0.1.7",
      "com.github.pathikrit"          %% "better-files"             % "3.8.0",
      "com.github.pureconfig"         %% "pureconfig"               % "0.10.2",

      "org.scalatest"                 %% "scalatest"                % ScalaTestVersion % "test",

      compilerPlugin("org.typelevel"  %% "kind-projector"     % "0.10.2"),
      compilerPlugin("com.olegpy"     %% "better-monadic-for" % "0.3.0")
    ),
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs @ _*) => MergeStrategy.discard
      case "application.conf"            => MergeStrategy.concat
      case "reference.conf"              => MergeStrategy.concat
      case x                             => MergeStrategy.first
      //case x => (assemblyMergeStrategy in assembly).value(x)
     }
  )
