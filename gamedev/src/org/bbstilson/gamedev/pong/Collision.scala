package org.bbstilson.gamedev.pong

import sgl.geometry._
import scala.math.abs

object Collision {
  private val PADDLE_COLLISION_MOD: Float = 0.8f

  def calcBallVecFromPaddleCollision(b: Circle, d: Vec)(p: Rect): Vec = {
    val nextX = d.x * -1
    val paddleThird = p.height / 3

    // Modify ball direction if the corner of the paddle is hit.
    val nextY = b.center.y - p.top match {
      case y if y <= paddleThird     => d.y + (d.y * PADDLE_COLLISION_MOD)
      case y if y >= paddleThird * 2 => d.y - (d.y * PADDLE_COLLISION_MOD)
      case _                         => d.y
    }

    d.copy(x = nextX, y = nextY)
  }
}
