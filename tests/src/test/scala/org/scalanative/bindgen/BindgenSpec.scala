package org.scalanative.bindgen

import java.io.File
import org.scalatest.FunSpec
import scala.io.Source

class BindgenSpec extends FunSpec {
  describe("Bindgen") {
    val bindgenPath    = System.getProperty("bindgen.path")
    val inputDirectory = new File("samples")

    val outputDir = new File("target/bindgen-samples")
    Option(outputDir.listFiles()).foreach(_.foreach(_.delete()))
    outputDir.mkdirs()

    it("should exist") {
      assert(new File(bindgenPath).exists, s"$bindgenPath does not exist")
    }

    def bindgen(inputFile: File, name: String, outputFile: File): Unit = {
      val result = Bindgen()
        .bindgenExecutable(new File(bindgenPath))
        .header(inputFile)
        .name(name)
        .link("bindgentests")
        .packageName("org.scalanative.bindgen.samples")
        .excludePrefix("__")
        .generate()

      result match {
        case Right(binding) =>
          binding.writeToFile(outputFile)
        case Left(error) =>
          fail("scala-native-bindgen failed: " + error.mkString("\n"))
      }
    }

    def contentOf(file: File) =
      Source.fromFile(file).getLines.mkString("\n").trim()

    for (input <- inputDirectory.listFiles() if input.getName.endsWith(".h")) {
      it(s"should generate bindings for ${input.getName}") {
        val testName = input.getName.replace(".h", "")
        val expected = new File(inputDirectory, testName + ".scala")
        val output   = new File(outputDir, testName + ".scala")

        bindgen(input, testName, output)

        assert(output.exists())
        assert(contentOf(output) == contentOf(expected))
      }
    }
  }
}
