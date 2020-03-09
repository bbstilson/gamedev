package org.bbstilson.gamedev.pong

import sgl.geometry._
import scala.math.abs

object Collision {
  private val PADDLE_COLLISION_MOD: Float = 0.8f
  private val MOD_CONST = 1f

  def calcBallVecFromPaddleCollision(b: Circle, d: Vec, p: Rect): Vec = {
    val nextX = d.x * -1
    val ballCenter = b.center.y
    val paddleTop = p.top
    val paddleSixth = p.height / 6
    val goingUp = d.y > 0

    val accelerateUp = d.y + (d.y * PADDLE_COLLISION_MOD * 0.5f)
    val accelerateDown = d.y - (d.y * PADDLE_COLLISION_MOD * 0.5f)

    // Modify ball direction if the corner of the paddle is hit.
    val nextY = ballCenter - paddleTop match {
      case y if y <= paddleSixth && goingUp      => accelerateUp
      case y if y <= paddleSixth && !goingUp     => accelerateUp
      case y if y >= paddleSixth * 5 && goingUp  => accelerateDown
      case y if y >= paddleSixth * 5 && !goingUp => accelerateDown
      case _                                     => d.y
    }

    d.copy(x = nextX, y = nextY)
  }
}
