package $package$

import cats.effect.testing.scalatest.AsyncIOSpec
import org.scalatest.freespec.AsyncFreeSpec
import com.dimafeng.testcontainers.{ Container, ForAllTestContainer, GenericContainer }
import cats.effect.{ Blocker, IO }
import org.http4s.{ Request, Uri }
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.Wait
import org.http4s.client.JavaNetClientBuilder
import org.slf4j.LoggerFactory
import org.http4s.Method.GET
import org.http4s.Status.Ok

class ApplicationIT extends AsyncFreeSpec with ForAllTestContainer with AsyncIOSpec {

  val blocker: Blocker = Blocker.liftExecutionContext(executionContext)

  val dockerImage = "$organization$/$name$-tests"

  lazy val apiContainer = GenericContainer(
    dockerImage = dockerImage,
    exposedPorts = Seq(80),
    waitStrategy = Wait.forLogMessage(".*started at http://0.0.0.0:80/.*", 1)
  ).configure { provider =>
    provider.withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("api server")))
    ()
  }

  override def container: Container = apiContainer

  def httpClient(blocker: Blocker) = JavaNetClientBuilder[IO](blocker).resource

  "healthcheck 200 status" in {

    val httpRequest: Request[IO] = Request(
      method = GET,
      uri = Uri.unsafeFromString(
        s"http://\${apiContainer.container.getHost}:\${apiContainer.mappedPort(80)}/health"
      )
    )

    val actual = httpClient(blocker).use(client => client.run(httpRequest).use(r => IO(r)))

    actual.asserting(response => assert(response.status == Ok))
  }

}
