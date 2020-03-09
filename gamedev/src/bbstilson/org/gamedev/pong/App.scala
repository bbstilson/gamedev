package bbstilson.org.gamedev.pong

import sgl._
import sgl.util._
import scene._

trait AbstractApp extends MainScreenComponent with ViewportComponent {
  this: GraphicsProvider
    with InputProvider
    with AudioProvider
    with WindowProvider
    with SystemProvider
    with GameStateComponent
    with SceneComponent
    with LoggingProvider =>

  override def startingScreen: GameScreen = new MainScreen(0, 0)

}
