package bbstilson.org.gamedev.pong

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
    with LoggingProvider =>

  import Graphics._

  val squareSize = 20
  val ballRadius = squareSize.toFloat / 2
  val paddleHeight = squareSize * 5
  val paddleWidth = squareSize
  lazy val objectColor = defaultPaint.withColor(Color.White)
  lazy val backgroundColor = defaultPaint.withColor(Color.Black)

  val TotalWidth = squareSize * 40
  val TotalHeight = squareSize * 20

  def centerV(objHeight: Int): Int = (TotalHeight - objHeight) / 2
  def centerH(objWidth: Int): Int = (TotalWidth - objWidth) / 2

  implicit private val LogTag = Logger.Tag("main-screen")

  class MainScreen extends GameScreen {

    override def name: String = "Scala Pong"

    val r = new Random

    // Start players in the middle of the screen.
    val paddleMid = centerV(paddleHeight)
    var player1Pos = Point(0, paddleMid)
    var player2Pos = Point(TotalWidth - paddleWidth, paddleMid)
    var moveDir: Option[Vec] = None

    // Start ball headed in a random direction within 45º at either player.
    var ballPos = Point(centerH(squareSize), centerV(squareSize))

    var BALL_SPEED = 4
    val PADDLE_SPEED = 8

    var ballDir = {
      val rX = if (r.nextBoolean) -BALL_SPEED else BALL_SPEED
      val rY = if (r.nextBoolean) -BALL_SPEED else BALL_SPEED
      Vec(rX, rY)
    }

    val Up = Vec(0, -PADDLE_SPEED)
    val Down = Vec(0, PADDLE_SPEED)

    val colliders = new Colliders(TotalHeight, TotalWidth, ballRadius, paddleHeight, paddleWidth)

    def gameOver(): Unit = {
      gameState.newScreen(new MainScreen())
    }

    def updatePlayerPosition(newPos: Point): Unit = player1Pos = newPos

    def playerCanMove(newPos: Point): Boolean = {
      newPos.y >= 0 && (newPos.y + paddleHeight) <= TotalHeight
    }

    def moveBall(): Unit = {
      val nextPos = ballPos + ballDir
      if (colliders.outY(nextPos)) {
        ballDir = ballDir.copy(y = ballDir.y * -1)
        ballPos = Point(nextPos.x, ballPos.y)
      } else if (colliders.outX(nextPos)) {
        gameOver()
      } else if (colliders.hitPaddle(nextPos, player1Pos, player2Pos)) {
        ballDir = ballDir.copy(x = ballDir.x * -1)
        ballPos = Point(ballPos.x, nextPos.y)
      } else {
        ballPos = nextPos
      }
    }

    private def handleInput(e: Input.InputEvent): Unit = {
      e match {
        case Input.KeyDownEvent(Input.Keys.Up)   => moveDir = Some(Up)
        case Input.KeyDownEvent(Input.Keys.Down) => moveDir = Some(Down)
        case Input.KeyUpEvent(Input.Keys.Up)     => moveDir = None
        case Input.KeyUpEvent(Input.Keys.Down)   => moveDir = None
        case _                                   => ()
      }
    }

    override def update(dt: Long): Unit = {
      Input.processEvents(handleInput)

      moveDir
        .map(player1Pos + _)
        .filter(playerCanMove)
        .foreach(updatePlayerPosition)

      moveBall()
    }

    def drawPaddle(canvas: Canvas, point: Point, paint: Paint) = {
      canvas.drawRect(point.x, point.y, paddleWidth, paddleHeight, paint)
    }

    def drawPlayer1(canvas: Canvas): Unit = {
      drawPaddle(canvas, player1Pos, objectColor)
    }

    def drawPlayer2(canvas: Canvas): Unit = {
      drawPaddle(canvas, player2Pos, objectColor)
    }

    def drawBall(canvas: Canvas): Unit = {
      canvas.drawCircle(ballPos.x, ballPos.y, ballRadius, objectColor)
    }

    override def render(canvas: Canvas): Unit = {
      canvas.drawRect(0, 0, Window.width, Window.height, backgroundColor)
      drawPlayer1(canvas)
      drawPlayer2(canvas)
      drawBall(canvas)
    }

    // class Debug extends SceneNode(WindowWidth - dp2px(15), dp2px(25), 0, 0) {
    //   def update(dt: Long): Unit = ()

    //   def render(canvas: Graphics.Canvas): Unit = {
    //     canvas.drawString("hello", x.toInt, y.toInt, textPaint.withAlignment(Alignments.Right))
    //   }
    // }

  }

}

class Colliders(
  totalHeight: Int,
  totalWidth: Int,
  ballRadius: Float,
  paddleHeight: Int,
  paddleWidth: Int
) {
  def outY(p: Point): Boolean = p.y + ballRadius >= totalHeight || p.y - ballRadius < 0

  def outX(p: Point): Boolean = p.x - ballRadius <= 0 || p.x + ballRadius >= totalWidth

  def hitPaddle(p: Point, p1: Point, p2: Point): Boolean = {
    val hitPlayer1 = (p.x - ballRadius < paddleWidth) && collidesY(p, p1)
    val hitPlayer2 = (p.x + ballRadius > totalWidth - paddleWidth) && collidesY(p, p2)

    hitPlayer1 || hitPlayer2
  }

  private def collidesY(p: Point, player: Point): Boolean =
    (p.y >= player.y && p.y <= player.y + paddleHeight)

}
