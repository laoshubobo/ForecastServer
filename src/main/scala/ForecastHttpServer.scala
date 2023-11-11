import WeatherAPI.{getForecastResult, getGridForecastURI}
import cats._
import cats.effect._
import org.http4s.circe._
import org.http4s._
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s.dsl._
import org.http4s.dsl.impl._
import org.http4s.server._
import org.http4s.server.blaze.BlazeServerBuilder

object ForecastHttpServer extends IOApp {
  case class ForecastResult(shortForecast: String, CharacterizedTemperature: String)

  private object LatitudeQueryParamMatcher extends ValidatingQueryParamDecoderMatcher[Float]("latitude")

  private object LongitudeQueryParamMatcher extends ValidatingQueryParamDecoderMatcher[Float]("longitude")

  private def forecastRoutes[F[_] : Monad]: HttpRoutes[F] = {
    val dsl = Http4sDsl[F]
    import dsl._
    HttpRoutes.of[F] {
      //Usage: http://localhost:8080/api/forecast?latitude=38.6484&longitude=-121.7339
      case GET -> Root / "forecast" :? LatitudeQueryParamMatcher(latitude) +& LongitudeQueryParamMatcher(longitude) =>
        latitude match {
          case validatedLatitude =>
            validatedLatitude.fold(_ => BadRequest("The latitude was badly formatted"),
              validatedLatitude =>
                longitude match {
                  case validatedLongitude =>
                    validatedLongitude.fold(_ => BadRequest("The longitude was badly formatted"),
                      validatedLongitude => {
                        getGridForecastURI(validatedLatitude, validatedLongitude) match {
                          case Left(value) =>
                            BadRequest(s"Cannot retrieve forecast information, $value")
                          case Right(value) =>
                            getForecastResult(value.properties.forecast) match {
                              case Some(forecastResult) => Ok(forecastResult.asJson)
                              case _ => BadRequest("Cannot retrieve forecast information, please try again")
                            }
                          case _ =>
                            BadRequest("Cannot retrieve forecast information, please try again")
                        }
                      })
                })
        }
    }
  }

  override def run(args: List[String]): IO[ExitCode] = {
    import org.http4s.implicits._
    val apis = Router(
      "/api" -> forecastRoutes[IO],
    ).orNotFound

    BlazeServerBuilder[IO](runtime.compute)
      .bindHttp(8080, "localhost")
      .withHttpApp(apis)
      .resource
      .use(_ => IO.never)
      .as(ExitCode.Success)
  }
}