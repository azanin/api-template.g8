// General Settings
inThisBuild(
  List(
    organization := "$organization$",
    developers := List(
      Developer(
        "$contributorUsername$",
        "$contributorName$",
        "$contributorEmail$",
        url("https://github.com/$contributorUsername$")
      )
    ),
    licenses += ("MIT", url("http://opensource.org/licenses/MIT")),
    pomIncludeRepository := { _ => false }
  )
)

// General Settings
lazy val commonSettings = Seq(
  scalaVersion := "$scala_version$",
  scalafmtOnCompile := true,
  addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.11.0" cross CrossVersion.full),
  addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
  libraryDependencies ++= Seq(
    "org.typelevel"               %% "cats-effect"                   % "2.3.1",
    "org.http4s"                  %% "http4s-dsl"                    % "0.21.19",
    "org.http4s"                  %% "http4s-blaze-server"           % "0.21.19",
    "com.softwaremill.sttp.tapir" %% "tapir-core"                    % "0.17.7",
    "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"           % "0.17.7",
    "com.softwaremill.sttp.tapir" %% "tapir-json-circe"              % "0.17.7",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"            % "0.17.7",
    "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml"      % "0.17.7",
    "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"       % "0.17.7",
    "io.circe"                    %% "circe-generic"                 % "0.13.0",
    "ch.qos.logback"               % "logback-classic"               % "1.2.3" % Runtime,
    "com.codecommit"              %% "cats-effect-testing-scalatest" % "0.4.2",
    "org.http4s"                  %% "http4s-blaze-client"           % "0.21.19",
    "org.http4s"                  %% "http4s-circe"                  % "0.21.19"
  )
)

lazy val core = project
  .in(file("core"))
  .settings(commonSettings)
  .settings(name := "$name$")
  .settings(parallelExecution in Test := false)
  .settings(test in assembly := {})
  .settings(assemblyJarName in assembly := "$name$.jar")
  .settings(assemblyMergeStrategy in assembly := {
    case PathList("META-INF", "maven", "org.webjars", "swagger-ui", "pom.properties") => MergeStrategy.singleOrError
    case x                                                                            =>
      val oldStrategy = (assemblyMergeStrategy in assembly).value
      oldStrategy(x)
  })
  .enablePlugins(DockerPlugin)
  .settings(dockerfile in docker := {
    dockerFile(assembly.value)
  })
  .settings(
    imageNames in docker := Seq(
      // Sets the latest tag
      ImageName(
        namespace = Some(organization.value + "/$name$"),
        repository = "$name$",
        registry = Some("ghcr.io"),
        tag = Some("latest")
      ), // Sets a name with a tag that contains the project version
      ImageName(
        namespace = Some(organization.value + "/$name$"),
        repository = "$name$",
        registry = Some("ghcr.io"),
        tag = Some("v" + version.value.replace('+', '-'))
      )
    )
  )
  .settings(
    publish in docker := Some("Github container registry" at "https://ghcr.io")
  )

lazy val tests = project
  .in(file("tests"))
  .settings(name := "$name$-tests")
  .configs(IntegrationTest)
  .settings(Defaults.itSettings)
  .settings(commonSettings)
  .settings(
    libraryDependencies ++= Seq(
      "com.dimafeng" %% "testcontainers-scala-scalatest" % "0.39.0"
    )
  )
  .settings(parallelExecution in IntegrationTest := false)
  .enablePlugins(NoPublishPlugin)
  .settings(fork in IntegrationTest := true)
  .enablePlugins(DockerPlugin)
  .settings(dockerfile in docker := dockerFile((assembly in core).value))
  .dependsOn(core)

lazy val `$name$` = project
  .in(file("."))
  .enablePlugins(NoPublishPlugin)
  .aggregate(core, tests)

def dockerFile(dependsOn: File) = {
  val artifactTargetPath = s"/app/\${dependsOn.name}"

  new Dockerfile {
    from("openjdk:11-jre")
    add(dependsOn, artifactTargetPath)
    entryPoint("java", "-jar", artifactTargetPath)
    expose(80)
    label("org.containers.image.source", "https://github.com/$contributorUsername$/$name$")
  }
}

addCommandAlias("integrationTests", ";project tests;docker;it:test")
addCommandAlias("tests", "project core;test;project tests;docker;it:test")
addCommandAlias("dockerBuildAndPublish", ";project core;dockerBuildAndPush")
