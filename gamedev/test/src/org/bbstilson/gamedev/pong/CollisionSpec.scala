package org.bbstilson.gamedev.pong

import sgl.geometry._

class CollisionSpec extends UnitSpec {
  val CIRCLE_HI = Circle(1, 1, 1)
  val CIRCLE_LOW = Circle(1, 11, 1)
  val UP_LEFT = Vec(-1, 1)
  val DOWN_LEFT = Vec(-1, -1)
  val PADDLE = Rect(0, 0, 1, 12)

  "calcBallVecFromPaddleCollision" should "increase speed when moving up and hit the top corner" in {
    val newY = Collision.calcBallVecFromPaddleCollision(CIRCLE_HI, UP_LEFT)(PADDLE).y
    newY > UP_LEFT.y shouldBe true
  }

  it should "decrease speed when moving down and hit the top corner" in {
    val newY = Collision.calcBallVecFromPaddleCollision(CIRCLE_HI, DOWN_LEFT)(PADDLE).y
    newY < DOWN_LEFT.y shouldBe true
  }

  it should "decrease speed when moving up and hit the bottom corner" in {
    val newY = Collision.calcBallVecFromPaddleCollision(CIRCLE_LOW, UP_LEFT)(PADDLE).y
    newY < UP_LEFT.y shouldBe true
  }

  it should "increase speed when moving down and hit the bottom corner" in {
    val newY = Collision.calcBallVecFromPaddleCollision(CIRCLE_LOW, DOWN_LEFT)(PADDLE).y
    newY > DOWN_LEFT.y shouldBe true
  }
}
