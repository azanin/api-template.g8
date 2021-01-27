package $package$

import cats.effect.{ ExitCode, IO, IOApp }
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext
object Application extends IOApp {

  override def run(args: List[String]): IO[ExitCode] = {

    BlazeServerBuilder[IO](ExecutionContext.global)
      .bindHttp(80, "0.0.0.0")
      .withHttpApp(Routes.make.httpApp)
      .serve
      .compile
      .drain
      .as(ExitCode.Success)
  }

}
