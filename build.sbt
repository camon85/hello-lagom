import java.nio.file.{Files, StandardCopyOption}

organization in ThisBuild := "com.camon"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.11.8"

lazy val `hello-lagom` = (project in file("."))
  .aggregate(`friend-api`, `friend-impl`)

lazy val `friend-api` = (project in file("friend-api"))
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )

lazy val `friend-impl` = (project in file("friend-impl"))
  .enablePlugins(LagomJava)
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslKafkaBroker,
      lagomJavadslTestKit,
      lombok
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`friend-api`)

lazy val `front-end` = (project in file("front-end"))
  .enablePlugins(PlayJava, LagomPlay)
  .disablePlugins(PlayLayoutPlugin)
  .settings(
    version := "1.0-SNAPSHOT",
    routesGenerator := InjectedRoutesGenerator,
    libraryDependencies ++= Seq(
      "org.webjars" % "foundation" % "5.5.2",
      "org.webjars" %% "webjars-play" % "2.5.0",
      lagomJavadslClient
    ),

    includeFilter in webpack := "*.js" || "*.jsx",
    compile in Compile := (compile in Compile).dependsOn(webpack.toTask("")).value,
    mappings in (Compile, packageBin) := {
      val compiledJsFiles = (WebKeys.public in Assets).value.listFiles().toSeq

      val publicJsFileMappings = compiledJsFiles.map { jsFile =>
        jsFile -> s"public/${jsFile.getName}"
      }

      val webJarsPathPrefix = SbtWeb.webJarsPathPrefix.value
      val compiledWebJarsBaseDir = (classDirectory in Assets).value / webJarsPathPrefix
      val compiledFilesWebJars = compiledJsFiles.map { compiledJs =>
        val compiledJsWebJar = compiledWebJarsBaseDir / compiledJs.getName
        Files.copy(compiledJs.toPath, compiledJsWebJar.toPath, StandardCopyOption.REPLACE_EXISTING)
        compiledJsWebJar
      }
      val webJarJsFileMappings = compiledFilesWebJars.map { jsFile =>
        jsFile -> s"${webJarsPathPrefix}/${jsFile.getName}"
      }

      (mappings in (Compile, packageBin)).value ++ publicJsFileMappings ++ webJarJsFileMappings
    },
    sourceDirectory in Assets := baseDirectory.value / "src" / "main" / "resources" / "assets",
    resourceDirectory in Assets := baseDirectory.value / "src" / "main" / "resources" / "public",

    PlayKeys.playMonitoredFiles ++=
      (sourceDirectories in (Compile, TwirlKeys.compileTemplates)).value :+
        (sourceDirectory in Assets).value :+
        (resourceDirectory in Assets).value,

    WebpackKeys.envVars in webpack += "BUILD_SYSTEM" -> "sbt",

    // Remove to use Scala IDE
    EclipseKeys.createSrc := EclipseCreateSrc.ValueSet(EclipseCreateSrc.ManagedClasses, EclipseCreateSrc.ManagedResources)
  )

val lombok = "org.projectlombok" % "lombok" % "1.16.10"

def common = Seq(
  javacOptions in compile += "-parameters"
)

