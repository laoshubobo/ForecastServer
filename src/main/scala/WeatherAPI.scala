import ForecastHttpServer.ForecastResult
import spray.json._
import sttp.client3._
import sttp.client3.sprayJson._

object WeatherAPI {
  case class PointsResponse(properties: PointsProperties)

  case class PointsProperties(forecast: String)

  def getGridForecastURI(latitude: Float, longitude: Float): Either[ResponseException[String, Exception], PointsResponse] = {

    val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()
    object PointsJsonProtocol extends DefaultJsonProtocol {
      implicit val pointsPropertiesFmt: RootJsonFormat[PointsProperties] = jsonFormat1(PointsProperties)
      implicit val pointsResponseFmt: RootJsonFormat[PointsResponse] = jsonFormat1(PointsResponse)
    }
    import PointsJsonProtocol._
    val response =
      basicRequest
        .get(uri"$weatherPath$latitude,$longitude")
        .response(asJson[PointsResponse])
        .send(backend)
    response.body
  }


  case class GridPointsResponse(properties: GridPointsProperties)

  case class Period(temperature: Int, shortForecast: String)

  case class GridPointsProperties(periods: List[Period])

  def getForecastResult(uriStr: String): Option[ForecastResult] = {
    val backend: SttpBackend[Identity, Any] = HttpClientSyncBackend()

    object GridPointsJsonProtocol extends DefaultJsonProtocol {
      implicit val periodFmt: RootJsonFormat[Period] = jsonFormat2(Period)
      implicit val gridPointsPropertiesFmt: RootJsonFormat[GridPointsProperties] = jsonFormat1(GridPointsProperties)
      implicit val gridPointsResponseFmt: RootJsonFormat[GridPointsResponse] = jsonFormat1(GridPointsResponse)
    }
    import GridPointsJsonProtocol._
    val response2 =
      basicRequest
        .get(uri"$uriStr")
        .response(asJson[GridPointsResponse])
        .send(backend)
    val forecastResult = response2.body match {
      case Left(_) =>
        None
      case Right(currentForecast) =>
        Some(ForecastResult(currentForecast.properties.periods.head.shortForecast,
          characterizeTemperature(currentForecast.properties.periods.head.temperature)))
    }
    forecastResult
  }

  private def characterizeTemperature(temp: Int) = temp match {
    case t if t <= 60 => "Cold"
    case t if t > 60 & t <= 90 => "Moderate"
    case t if t > 90 => "Hot"
  }

  private val weatherPath = "https://api.weather.gov/points/"
}