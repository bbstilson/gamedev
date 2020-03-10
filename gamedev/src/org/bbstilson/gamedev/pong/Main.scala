package org.bbstilson.gamedev.pong

import sgl.InputHelpersComponent
import sgl.awt._
import sgl.awt.util._
import sgl.scene.SceneComponent
import sgl.SaveComponent

object Main extends AbstractApp with AWTApp with SceneComponent with VerboseStdErrLoggingProvider {

  override val frameDimension = (TOTAL_WIDTH, TOTAL_HEIGHT)

}
