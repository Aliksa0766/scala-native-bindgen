package org.scalanative.bindgen.samples

import utest._
import scalanative.native._

object ExternTests extends TestSuite {
  val tests = Tests {
    'forty_two - {
      assert(Extern.forty_two == 42)
    }

    'mode - {
      assert(Extern.mode == Extern.enum_mode.USER)
    }

    'semver - {
      import Extern.implicits._
      assert(Extern.semver.major == 1 && Extern.semver.minor == 2 && Extern.semver.patch == 3)
    }
  }
}
