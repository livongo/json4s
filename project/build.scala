import sbt._
import Keys._

import xml.Group
import MimaSettings.mimaSettings
import com.typesafe.tools.mima.plugin.MimaKeys.{mimaPreviousArtifacts, mimaReportBinaryIssues}
import livongo.build.project.{Artifactory, ArtifactoryPublisherPlugin}

object build {
  import Dependencies._

  val manifestSetting = packageOptions += {
    val (title, v, vendor) = (name.value, version.value, organization.value)
      Package.ManifestAttributes(
        "Created-By" -> "Simple Build Tool",
        "Built-By" -> System.getProperty("user.name"),
        "Build-Jdk" -> System.getProperty("java.version"),
        "Specification-Title" -> title,
        "Specification-Version" -> v,
        "Specification-Vendor" -> vendor,
        "Implementation-Title" -> title,
        "Implementation-Version" -> v,
        "Implementation-Vendor-Id" -> vendor,
        "Implementation-Vendor" -> vendor
      )
  }

  enablePlugins(ArtifactoryPublisherPlugin)

  val mavenCentralFrouFrou = Seq(
    publishTo := Some(Artifactory.artifactoryResolver),
    publishConfiguration := publishConfiguration.value.withOverwrite(true),
    credentials ++= Artifactory.artifactoryCredentials,
    homepage := Some(new URL("https://github.com/livongo/json4s")),
    startYear := Some(2009),
    licenses := Seq(("Apache-2.0", new URL("http://www.apache.org/licenses/LICENSE-2.0"))),
    pomExtra := {
      pomExtra.value ++ Group(
      <developers>
        <developer>
          <id>casualjim</id>
          <name>Ivan Porto Carrero</name>
          <url>http://flanders.co.nz/</url>
        </developer>
        <developer>
          <id>seratch</id>
          <name>Kazuhiro Sera</name>
          <url>http://git.io/sera</url>
        </developer>
      </developers>
      )
    }
  )

  val json4sSettings = mavenCentralFrouFrou ++ mimaSettings ++ Def.settings(
    organization := "org.json4s",
    scalaVersion := "2.12.8",
    crossScalaVersions := Seq("2.10.7", "2.11.12", "2.12.8", "2.13.0"),
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-feature", "-language:existentials", "-language:implicitConversions", "-language:higherKinds", "-language:postfixOps"),
    scalacOptions ++= PartialFunction.condOpt(CrossVersion.partialVersion(scalaVersion.value)) {
      case Some((2, 10)) => "-optimize"
    }.toList,
    scalacOptions in (Compile, doc) ++= {
      val base = (baseDirectory in LocalRootProject).value.getAbsolutePath
      val hash = sys.process.Process("git rev-parse HEAD").lineStream_!.head
      Seq("-sourcepath", base, "-doc-source-url", "https://github.com/livongo/json4s/tree/" + hash + "€{FILE_PATH}.scala")
    },
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, v)) if v <= 12 =>
          Seq("-Xfuture")
        case _ =>
          Nil
      }
    },
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, v)) if v <= 10 =>
          Nil
        case Some((2, 11)) =>
          Seq("-Ywarn-unused-import")
        case _ =>
          Seq("-Ywarn-unused:imports")
      }
    },
    version := "3.6.7-livongo-1.0.0",
    javacOptions ++= Seq("-target", "1.8", "-source", "1.8"),
    Seq(Compile, Test).map { scope =>
      unmanagedSourceDirectories in scope += {
        val base = (sourceDirectory in scope).value.getParentFile / Defaults.nameForSrc(scope.name)
        CrossVersion.partialVersion(scalaVersion.value) match {
          case Some((2, v)) if v >= 13 && scalaVersion.value != "2.13.0-M3" =>
            base / s"scala-2.13+"
          case _ =>
            base / s"scala-2.13-"
        }
      }
    },
    parallelExecution in Test := false,
    manifestSetting,
    resolvers ++= Seq(Opts.resolver.sonatypeReleases),
    crossVersion := CrossVersion.binary
  )

  val noPublish = Seq(
    mimaReportBinaryIssues := {},
    mimaPreviousArtifacts := Set(),
    publishArtifact := false,
    publish := {},
    publishLocal := {}
  )
}
