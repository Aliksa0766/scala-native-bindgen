package org.scalanative.bindgen

import java.io.{File, PrintWriter}

import org.scalatest.FunSpec

class BindgenReportingSpec extends FunSpec {
  describe("Bindgen") {

    val bindgenPath = System.getProperty("bindgen.path")

    def writeToFile(file: File, input: String): Unit = {
      new PrintWriter(file) {
        try {
          write(input)
        } finally {
          close()
        }
      }
    }

    def bindgen(input: String): Bindings = {
      val tempFile = File.createTempFile("scala-native-bindgen-tests", ".h")
      try {
        writeToFile(tempFile, input)

        Bindgen()
          .bindgenExecutable(new File(bindgenPath))
          .header(tempFile)
          .name("BindgenTests")
          .link("bindgentests")
          .packageName("org.scalanative.bindgen.samples")
          .excludePrefix("__")
          .generate()

      } finally {
        tempFile.delete()
      }
    }

    it("Skips functions that pass struct or union by value") {
      val bindings =
        bindgen(input = """struct s { int a; };
                          |void useStruct(struct s);
                          |typedef struct s s;
                          |s returnStruct();
                          |
                          |union u { int a; };
                          |void useUnion(union u);
                          |typedef union u u;
                          |u returnUnion();
                          |""".stripMargin)
      assert(
        bindings.errs ==
          """Warning: Function useStruct is skipped because Scala Native does not support passing structs and arrays by value.
            |Warning: Function returnStruct is skipped because Scala Native does not support passing structs and arrays by value.
            |Warning: Function useUnion is skipped because Scala Native does not support passing structs and arrays by value.
            |Warning: Function returnUnion is skipped because Scala Native does not support passing structs and arrays by value.""".stripMargin
      )
    }
  }
}