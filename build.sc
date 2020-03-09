import mill._, scalalib._

object gamedev extends ScalaModule {
  def scalaVersion = "2.12.10"

  def scalacOptions = Seq("-deprecation", "-feature")

  def ivyDeps = Agg(
    ivy"com.regblanc.sgl::sgl-core:0.0.1",
    ivy"com.regblanc.sgl::sgl-desktop-awt:0.0.1"
    // "com.regblanc.sgl::sgl-jvmshared:0.0.1"
  )

  object test extends Tests {

    def ivyDeps = Agg(
      ivy"org.scalactic::scalactic:3.1.1",
      ivy"org.scalatest::scalatest:3.1.1"
    )

    def testFrameworks = Seq("org.scalatest.tools.Framework")
  }
}
