package org.bbstilson.gamedev.pong

import sgl._
import sgl.util._
import geometry._
import scene._

import scala.util.Random

trait MainScreenComponent {
  self: GraphicsProvider
    with InputProvider
    with AudioProvider
    with WindowProvider
    with SystemProvider
    with GameStateComponent
    with SceneComponent
    with ViewportComponent
    with LoggingProvider =>

  import Graphics._

  val SQUARE_SIZE = 20
  lazy val OBJECT_COLOR = defaultPaint.withColor(Color.White)
  lazy val BACKGROUND_COLOR = defaultPaint.withColor(Color.Black)

  val TOTAL_WIDTH = SQUARE_SIZE * 40
  val TOTAL_HEIGHT = SQUARE_SIZE * 20

  def centerV(objHeight: Int): Int = (TOTAL_HEIGHT - objHeight) / 2
  def centerH(objWidth: Int): Int = (TOTAL_WIDTH - objWidth) / 2

  implicit private val LogTag = Logger.Tag("main-screen")

  class MainScreen(player1Score: Int = 0, player2Score: Int = 0) extends GameScreen {

    def getPlayer1Score: String = player1Score.toString
    def getPlayer2Score: String = player2Score.toString

    override def name: String = "Scala Pong"

    val hud = new Hud(this)

    val r = new Random

    val PADDLE_HEIGHT = SQUARE_SIZE * 5
    val PADDLE_WIDTH = SQUARE_SIZE
    val PADDLE_MID = centerV(PADDLE_HEIGHT)

    // Start players in the middle of the screen.
    var player1 = Rect(0, PADDLE_MID, PADDLE_WIDTH, PADDLE_HEIGHT)
    var player2 = Rect(TOTAL_WIDTH - PADDLE_WIDTH, PADDLE_MID, PADDLE_WIDTH, PADDLE_HEIGHT)
    var playerDirection: Option[Vec] = None

    // Start ball headed in a random direction within 45º at either player.
    val ballRadius = SQUARE_SIZE.toFloat / 2
    var ball = Circle(centerH(SQUARE_SIZE), centerV(SQUARE_SIZE), ballRadius)

    var BALL_SPEED = 4
    var PLAYER2_SPEED = 8
    val PLAYER1_SPEED = 8

    var ballDirection = {
      val rX = if (r.nextBoolean) -BALL_SPEED else BALL_SPEED
      val rY = if (r.nextBoolean) -BALL_SPEED else BALL_SPEED
      Vec(rX, rY)
    }

    val UP = Vec(0, -PLAYER1_SPEED)
    val DOWN = Vec(0, PLAYER1_SPEED)

    def gameOver(p1S: Int, p2S: Int): Unit = {
      gameState.newScreen(new MainScreen(p1S, p2S))
    }

    def updatePlayerPosition(p: Rect): Unit = player1 = p
    def playerCanMove(p: Rect): Boolean = p.top >= 0 && p.bottom <= TOTAL_HEIGHT

    def updatePlayer2Position(): Unit = {
      // Player 2 always moves tries to get under the ball.
      val player2Y = player2.centerY
      // + for up; - for down; 0 for don't move
      val desiredDirection = ball.center.y match {
        case ballY if (ballY > player2Y) => PLAYER2_SPEED
        case ballY if (ballY < player2Y) => -PLAYER2_SPEED
        case _                           => 0
      }

      if ((player2.bottom + desiredDirection <= TOTAL_HEIGHT)
          && (player2.top + desiredDirection >= 0)) {
        player2 = player2 + Vec(0, desiredDirection)
      }

    }

    def moveBall(): Unit = {
      val (nextBall, nextBallDirection) = (ball + ballDirection) match {
        case nb if (nb.bottom >= TOTAL_HEIGHT || nb.top <= 0) => {
          (Circle(nb.x, ball.y, ballRadius), ballDirection.copy(y = ballDirection.y * -1))
        }
        case nb if (nb.intersect(player1) || nb.intersect(player2)) => {
          val paddleCollidedWith = List(player1, player2).find(nb.intersect).get
          val nextBallDirection = Collision.calcBallVecFromPaddleCollision(
            b = nb,
            d = ballDirection,
            p = paddleCollidedWith
          )
          (Circle(ball.x, nb.y, ballRadius), nextBallDirection)
        }
        case nb if (nb.left < PADDLE_WIDTH / 2) => {
          gameOver(player1Score, player2Score + 1)
          (nb, ballDirection)
        }
        case nb if (nb.right > TOTAL_WIDTH - PADDLE_WIDTH / 2) => {
          gameOver(player1Score + 1, player2Score)
          (nb, ballDirection)
        }
        case nb => (nb, ballDirection)
      }

      ball = nextBall
      ballDirection = nextBallDirection
    }

    private def handleInput(e: Input.InputEvent): Unit = {
      e match {
        case Input.KeyDownEvent(Input.Keys.Up)   => playerDirection = Some(UP)
        case Input.KeyDownEvent(Input.Keys.Down) => playerDirection = Some(DOWN)
        case Input.KeyUpEvent(Input.Keys.Up)     => playerDirection = None
        case Input.KeyUpEvent(Input.Keys.Down)   => playerDirection = None
        case _                                   => ()
      }
    }

    override def update(dt: Long): Unit = {
      Input.processEvents(handleInput)

      playerDirection
        .map(player1 + _)
        .filter(playerCanMove)
        .foreach(updatePlayerPosition)

      // Only react when the ball is on their side.
      if (ball.center.x >= TOTAL_WIDTH / 2) {
        updatePlayer2Position()
      }

      moveBall()
    }

    override def render(canvas: Canvas): Unit = {
      canvas.drawRect(0, 0, Window.width, Window.height, BACKGROUND_COLOR)

      drawPaddle(canvas, player1, OBJECT_COLOR)
      drawPaddle(canvas, player2, OBJECT_COLOR)
      drawBall(canvas)
      drawNet(canvas)

      hud.sceneGraph.render(canvas)
    }

    private def drawPaddle(canvas: Canvas, paddle: Rect, paint: Paint) = {
      canvas.drawRect(paddle.left, paddle.top, paddle.width, paddle.height, paint)
    }

    private def drawBall(canvas: Canvas): Unit = {
      canvas.drawCircle(ball.x, ball.y, ballRadius, OBJECT_COLOR)
    }

    val NET_SPACING = SQUARE_SIZE / 2
    val NET_PIECE_DIST = SQUARE_SIZE + NET_SPACING
    val NUM_NET_PIECES = TOTAL_HEIGHT / NET_PIECE_DIST
    val NET_PIECES = List.fill(NUM_NET_PIECES)(NET_PIECE_DIST).scanLeft(0)(_ + _)
    val NET_PIECE_SIDE = SQUARE_SIZE / 2
    val NET_PIECE_CENTER = TOTAL_WIDTH / 2 - NET_PIECE_SIDE

    private def drawNet(canvas: Canvas): Unit = {
      NET_PIECES.foreach(y =>
        canvas.drawRect(NET_PIECE_CENTER, y, NET_PIECE_SIDE, NET_PIECE_SIDE, OBJECT_COLOR)
      )
    }
  }

  class Hud(mainScreen: MainScreen) {
    val viewport = new Viewport(Window.width, Window.height)
    val sceneGraph = new SceneGraph(Window.width, Window.height, viewport)

    private val group = new SceneGroup(0, 0, Window.width, Window.height)

    private val player1Score = new Player1Score
    private val player2Score = new Player2Score

    group.addNode(player1Score)
    group.addNode(player2Score)
    sceneGraph.addNode(group)

    private lazy val scoreFontSize = Window.dp2px(100)

    private val scorePaint = defaultPaint
      .withColor(Color.White)
      .withFont(Font.Default.withSize(scoreFontSize))

    private lazy val player1TextPosX = (Window.width / 2) - Window.dp2px(50)
    private lazy val player2TextPosX = (Window.width / 2) + Window.dp2px(50)
    private lazy val playerScoreY = scoreFontSize

    class Player1Score extends SceneNode(player1TextPosX, playerScoreY, 0, 0) {
      def update(dt: Long): Unit = ()

      def render(canvas: Graphics.Canvas): Unit = {
        canvas.drawString(
          mainScreen.getPlayer1Score,
          x.toInt,
          y.toInt,
          scorePaint.withAlignment(Alignments.Right)
        )
      }
    }

    class Player2Score extends SceneNode(player2TextPosX, playerScoreY, 0, 0) {

      def update(dt: Long): Unit = ()

      def render(canvas: Graphics.Canvas): Unit = {
        canvas.drawString(
          mainScreen.getPlayer2Score,
          x.toInt,
          y.toInt,
          scorePaint.withAlignment(Alignments.Left)
        )
      }
    }

  }
}
