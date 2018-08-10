addSbtPlugin("org.scala-native"  % "sbt-scala-native"           % "0.3.8")
addSbtPlugin("com.typesafe.sbt"  % "sbt-site"                   % "1.3.2")
addSbtPlugin("io.github.jonas"   % "sbt-paradox-material-theme" % "0.4.0")
addSbtPlugin("com.typesafe.sbt"  % "sbt-ghpages"                % "0.6.2")
addSbtPlugin("com.typesafe.sbt"  % "sbt-git"                    % "1.0.0")
addSbtPlugin("com.github.gseitz" % "sbt-release"                % "1.0.9")
addSbtPlugin("org.foundweekends" % "sbt-bintray"                % "0.5.4")
addSbtPlugin("com.eed3si9n"      % "sbt-buildinfo"              % "0.9.0")

libraryDependencies += "org.scala-sbt" %% "scripted-plugin" % sbtVersion.value