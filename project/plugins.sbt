addSbtPlugin("org.foundweekends.giter8" %% "sbt-giter8"      % "0.13.1")
libraryDependencies += "org.scala-sbt"  %% "scripted-plugin" % sbtVersion.value

addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat" % "0.1.15")

addSbtPlugin("org.scalameta" % "sbt-scalafmt" % "2.4.2")

addSbtPlugin("io.chrisdavenport" % "sbt-no-publish" % "0.1.0")

addSbtPlugin("com.eed3si9n" % "sbt-assembly" % "0.15.0")

addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.8.1")

addSbtPlugin("com.dwijnand" % "sbt-dynver" % "4.1.1")
