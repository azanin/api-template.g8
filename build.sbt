lazy val root = (project in file("."))
  .enablePlugins(ScriptedPlugin)
  .settings(
    name := "api-template.g8",
    test in Test := {
      val _ = (g8Test in Test).toTask("").value
    },
    addCompilerPlugin("org.typelevel" %% "kind-projector"     % "0.13.0" cross CrossVersion.full),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.1"),
    libraryDependencies ++= Seq(
      "org.typelevel"               %% "cats-effect"                    % "2.5.1",
      "org.http4s"                  %% "http4s-dsl"                     % "0.21.25",
      "org.http4s"                  %% "http4s-blaze-server"            % "0.21.25",
      "com.softwaremill.sttp.tapir" %% "tapir-core"                     % "0.17.20",
      "com.softwaremill.sttp.tapir" %% "tapir-http4s-server"            % "0.17.20",
      "com.softwaremill.sttp.tapir" %% "tapir-json-circe"               % "0.17.20",
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-docs"             % "0.17.20",
      "com.softwaremill.sttp.tapir" %% "tapir-openapi-circe-yaml"       % "0.17.20",
      "com.softwaremill.sttp.tapir" %% "tapir-swagger-ui-http4s"        % "0.17.20",
      "io.circe"                    %% "circe-generic"                  % "0.14.1",
      "ch.qos.logback"               % "logback-classic"                % "1.2.4" % Runtime,
      "com.codecommit"              %% "cats-effect-testing-scalatest"  % "0.5.4",
      "org.http4s"                  %% "http4s-blaze-client"            % "0.21.25",
      "org.http4s"                  %% "http4s-circe"                   % "0.21.25",
      "com.dimafeng"                %% "testcontainers-scala-scalatest" % "0.39.5"
    ),
    scriptedLaunchOpts ++= List(
      "-Xms1024m",
      "-Xmx1024m",
      "-XX:ReservedCodeCacheSize=128m",
      "-Xss2m",
      "-Dfile.encoding=UTF-8"
    ),
    resolvers += Resolver.url("typesafe", url("https://repo.typesafe.com/typesafe/ivy-releases/"))(
      Resolver.ivyStylePatterns
    )
  )
