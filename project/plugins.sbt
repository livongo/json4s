addSbtPlugin("com.typesafe"     % "sbt-mima-plugin"      % "0.6.4")
addSbtPlugin("com.eed3si9n"     % "sbt-buildinfo"        % "0.9.0")
addSbtPlugin("org.xerial.sbt"   % "sbt-sonatype"         % "2.5")
addSbtPlugin("com.jsuereth"     % "sbt-pgp"              % "1.1.2")
addSbtPlugin("com.timushev.sbt" % "sbt-updates"          % "0.4.1")

scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature")

resolvers += Resolver.url(
  "Artifactory ivy",
  url("http://artifactory.prod.livongo.com/artifactory/plugins-release-local")
)(Resolver.ivyStylePatterns)

addSbtPlugin("livongo" %% "build-plugins" % "0.2.0")
