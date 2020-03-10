package org.bbstilson.gamedev.pong

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
    with PartsResourcePathProvider
    with LoggingProvider =>

  import Graphics._
  import Audio._
  import Assets.Sound._
  import Assets.Music._

  lazy val sounds = Map(
    BALL_HIT -> loadSound(PartsResourcePath(BALL_HIT_PATH)),
    PLAYER_SCORED -> loadSound(PartsResourcePath(PLAYER_SCORED_PATH))
  )

  class SoundLoader(toLoad: Map[String, Loader[Sound]]) extends LoadingScreen(toLoad) {

    override def render(canvas: Canvas): Unit = println("Loading sounds...")

    override def nextScreen(sounds: Map[String, Sound]): GameScreen = {
      new MainScreen(0, 0, sounds)
    }
  }

  override def startingScreen: GameScreen = new SoundLoader(sounds)

}

object Assets {

  object Music {
    val THEME_MUSIC = "THEME_MUSIC"
    val THEME_MUSIC_PATH = toPath("pong/theme.wav")
  }

  object Sound {
    val BALL_HIT = "BALL_HIT"
    val BALL_HIT_PATH = toPath("pong/ball_hit.wav")
    val PLAYER_SCORED = "PLAYER_SCORED"
    val PLAYER_SCORED_PATH = toPath("pong/player_scored.wav")

  }

  def toPath(s: String): Vector[String] = s.split("/").toVector
}
