package net.macolabo.sform2.form

import net.macolabo.sform2.helper.SformTestHelper
import org.scalatestplus.play.PlaySpec
import play.api.db.evolutions.{Evolution, Evolutions}

class FormSpec extends PlaySpec with SformTestHelper {

  withSformDatabase { database =>
    Evolutions.applyEvolutions(database)
    // Evolutions.cleanupEvolutions(database)
  }
}
