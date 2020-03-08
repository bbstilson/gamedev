package bbstilson.org.gamedev.pong

import sgl.InputHelpersComponent
import sgl.awt._
import sgl.awt.util._
import sgl.scene.SceneComponent
import sgl.SaveComponent

object Main extends AbstractApp with AWTApp with SceneComponent with VerboseStdErrLoggingProvider {

  override val TargetFps = Some(60)

  override val frameDimension = (TotalWidth, TotalHeight)

}
