package org.scalanative.bindgen.sbt

import sbt._
import sbt.Keys._

import java.nio.file.Files
import java.nio.file.attribute.{PosixFileAttributeView, PosixFilePermission}

import org.scalanative.bindgen.Bindgen

/**
 * Generate Scala bindings from C headers.
 *
 * == Usage ==
 *
 * This plugin must be explicitly enabled. To enable it add the following line
 * to your `.sbt` file:
 * {{{
 * enablePlugins(ScalaNativeBindgenPlugin)
 * }}}
 *
 * By default, the plugin reads the configured header file and generates
 * a single Scala source file in the managed source directory.
 *
 * See the [[https://github.com/kornilova-l/scala-native-bindgen/tree/master/sbt-scala-native-bindgen/src/sbt-test/bindgen/generate example project]].
 *
 * == Configuration ==
 *
 * Keys are defined in [[ScalaNativeBindgenPlugin.autoImport]].
 *
 *  - `nativeBindgenPath`: Path to the `scala-native-bindgen` executable.
 *  - `nativeBindings`: Settings for each binding to generate.
 *  - `target in nativeBindgen`: Output folder of the generated code.
 *  - `version in nativeBindgen`: Version of the `scala-native-bindgen`
 *    to use when automatically downloading the executable.
 *  - `nativeBindgen`: Generate Scala Native bindings.
 *
 * @example
 * {{{
 * nativeBindgenHeader in Compile := file("/usr/include/ctype.h")
 * nativeBindgenPackage in Compile := Some("org.example.app")
 * name in (Compile, nativeBindgen) := "ctype"
 * }}}
 */
object ScalaNativeBindgenPlugin extends AutoPlugin {

  object autoImport {
    case class NativeBinding(
        name: String,
        header: File,
        packageName: Option[String],
        link: Option[String],
        excludePrefix: Option[String]
    )
    val ScalaNativeBindgen = config("scala-native-bindgen").hide
    val nativeBindgenPath =
      taskKey[File]("Path to the scala-native-bindgen executable")
    val nativeBindings =
      settingKey[Seq[NativeBinding]]("Configuration for each bindings")
    val nativeBindgen = taskKey[Seq[File]]("Generate Scala Native bindings")
  }
  import autoImport._

  override def requires = plugins.JvmPlugin

  override def projectSettings: Seq[Setting[_]] =
    inConfig(ScalaNativeBindgen)(Defaults.configSettings) ++
      nativeBindgenScopedSettings(Compile) ++
      Def.settings(
        ivyConfigurations += ScalaNativeBindgen,
        version in nativeBindgen := BuildInfo.version,
        libraryDependencies ++= {
          artifactName.map { name =>
            val bindgenVersion = (version in nativeBindgen).value
            val url =
              s"${BuildInfo.projectUrl}/releases/download/v$bindgenVersion/$name"

            BuildInfo.organization % name % bindgenVersion % ScalaNativeBindgen from (url)
          }.toSeq
        },
        nativeBindgenPath := {
          val scalaNativeBindgenUpdate = (update in ScalaNativeBindgen).value

          val artifactFile = artifactName match {
            case None =>
              sys.error(
                "No downloadable binaries available for your OS, " +
                  "please provide path via `nativeBindgenPath`")
            case Some(name) =>
              scalaNativeBindgenUpdate
                .select(artifact = artifactFilter(name = name))
                .head
          }

          // Set the executable bit on the expected path to fail if it doesn't exist
          for (view <- Option(
                 Files.getFileAttributeView(artifactFile.toPath,
                                            classOf[PosixFileAttributeView]))) {
            val permissions = view.readAttributes.permissions
            if (permissions.add(PosixFilePermission.OWNER_EXECUTE))
              view.setPermissions(permissions)
          }

          artifactFile
        }
      )

  private implicit class BindgenOps(val bindgen: Bindgen) extends AnyVal {
    def maybe[T](opt: Option[T], f: Bindgen => T => Bindgen): Bindgen =
      opt match {
        case None        => bindgen
        case Some(value) => f(bindgen)(value)
      }
  }

  private val artifactName =
    Option(System.getProperty("os.name")).collect {
      case "Mac OS X" => "scala-native-bindgen-darwin"
      case "Linux"    => "scala-native-bindgen-linux"
    }

  def nativeBindgenScopedSettings(conf: Configuration): Seq[Setting[_]] =
    inConfig(conf)(
      Def.settings(
        nativeBindings := Seq.empty,
        sourceGenerators += Def.task { nativeBindgen.value },
        target in nativeBindgen := sourceManaged.value / "sbt-scala-native-bindgen",
        nativeBindgen := {
          val bindgenPath     = nativeBindgenPath.value
          val bindings        = nativeBindings.value
          val outputDirectory = (target in nativeBindgen).value
          val logger          = streams.value.log

          bindings.map {
            binding =>
              val output = outputDirectory / s"${binding.name}.scala"
              val result = Bindgen()
                .bindgenExecutable(bindgenPath)
                .header(binding.header)
                .name(binding.name)
                .maybe(binding.link, _.link)
                .maybe(binding.packageName, _.packageName)
                .maybe(binding.excludePrefix, _.excludePrefix)
                .generate()

              result match {
                case Right(bindings) =>
                  bindings.writeToFile(output)
                  bindings.errors.foreach(error => logger.error(error))
                case Left(errors) =>
                  errors.foreach(error => logger.error(error))
                  sys.error(
                    "scala-native-bindgen failed with non-zero exit code")
              }

              output
          }
        }
      ))
}
