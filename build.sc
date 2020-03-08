import mill._, scalalib._

object gamedev extends ScalaModule {
  def scalaVersion = "2.12.10"

  def scalacOptions = Seq("-deprecation", "-feature")

  def ivyDeps = Agg(
    ivy"com.regblanc.sgl::sgl-core:0.0.1",
    ivy"com.regblanc.sgl::sgl-desktop-awt:0.0.1"
    // "com.regblanc.sgl::sgl-jvmshared:0.0.1"
  )
}
