package $package$

import cats.effect.{ ContextShift, IO, Timer }
import org.http4s.HttpApp
import org.http4s.server.Router
import org.http4s.server.middleware.{ RequestLogger, ResponseLogger }
import cats.implicits._
import org.http4s.implicits._
import sttp.tapir.server.http4s.Http4sServerInterpreter
import sttp.tapir.docs.openapi.OpenAPIDocsInterpreter
import sttp.tapir.openapi.circe.yaml.RichOpenAPI
import sttp.tapir.swagger.http4s.SwaggerHttp4s
import $package$.Endpoints

class Routes(implicit C: ContextShift[IO], T: Timer[IO]) {

  private val healthCheckRoute =
    Http4sServerInterpreter.toRoutes(Endpoints.healthcheck)(_ => IO("Up and running".asRight[Unit]))

  private val swaggerRoutes = new SwaggerHttp4s(
    OpenAPIDocsInterpreter
      .toOpenAPI(
        List(Endpoints.healthcheck),
        title = "The $name$ API",
        version = "0.0.1"
      )
      .toYaml
  ).routes[IO]

  private val routes = Router(
    "/" -> healthCheckRoute,
    "/" -> swaggerRoutes
  ).orNotFound

  private val loggers: HttpApp[IO] => HttpApp[IO] = {
    { http: HttpApp[IO] =>
      RequestLogger.httpApp(true, true)(http)
    } andThen { http: HttpApp[IO] =>
      ResponseLogger.httpApp(true, true)(http)
    }
  }

  val httpApp: HttpApp[IO] = loggers(routes)
}

object Routes {
  def make(implicit C: ContextShift[IO], T: Timer[IO]) = new Routes()
}
