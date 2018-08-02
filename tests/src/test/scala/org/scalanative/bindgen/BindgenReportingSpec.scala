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

    def assertBindgenError(input: String, errors: Seq[String]): Unit = {
      val tempFile = File.createTempFile("scala-native-bindgen-tests", ".h")
      try {
        writeToFile(tempFile, input)

        val result = Bindgen()
          .bindgenExecutable(new File(bindgenPath))
          .header(tempFile)
          .name("BindgenTests")
          .link("bindgentests")
          .packageName("org.scalanative.bindgen.samples")
          .excludePrefix("__")
          .generate()

        result match {
          case Right(binding)      => assert(binding.errors == errors)
          case Left(bindingErrors) => assert(bindingErrors == errors)
        }
      } finally {
        tempFile.delete()
      }
    }

    it("Skips functions that pass struct or union by value") {
      assertBindgenError(
        """struct s { int a; };
          |void useStruct(struct s);
          |typedef struct s s;
          |s returnStruct();
          |
          |union u { int a; };
          |void useUnion(union u);
          |typedef union u u;
          |u returnUnion();
          |""".stripMargin,
        Seq(
          "Warning: Function useStruct is skipped because Scala Native does not support passing structs and arrays by value.",
          "Warning: Function returnStruct is skipped because Scala Native does not support passing structs and arrays by value.",
          "Warning: Function useUnion is skipped because Scala Native does not support passing structs and arrays by value.",
          "Warning: Function returnUnion is skipped because Scala Native does not support passing structs and arrays by value."
        )
      )
    }

    it("Skips variable with opaque type") {
      assertBindgenError(
        """struct undefinedStruct;
          |extern struct undefinedStruct removedExtern;
          |#define removedExternAlias removedExtern
          |""".stripMargin,
        Seq(
          "Error: Variable removedExtern is skipped because it has incomplete type.",
          "Error: Variable alias removedExternAlias is skipped because it has incomplete type."
        )
      )
    }

    it("Skips unused alias for opaque type") {
      assertBindgenError(
        """union undefinedUnion;
          |typedef union undefinedUnion aliasForUndefinedUnion;
          |""".stripMargin,
        Seq(
          "Warning: type alias aliasForUndefinedUnion is skipped because it is an unused alias for incomplete type.")
      )
    }
  }
}
