package HighLevelServer
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives.{complete, path}
import akka.http.scaladsl.server.RouteResult.Complete
import akka.stream.ActorMaterializer


object HighLevelIntro extends App {
  implicit val system = ActorSystem("HighLevelAPI")
  implicit val materializer = ActorMaterializer
  import system.dispatcher

  // Directives
  import akka.http.scaladsl.server.Directives._
  val simpleRoute:Route =
    path("home"){//Directive
      complete(StatusCodes.OK)
    }

  val pathGetRoute:Route = path("home")
  {
    get{
      complete(StatusCodes.OK)
    }
  }

  //changing Directives
  val changeRoute:Route = path("myEndpoint"){
    get{
      complete(StatusCodes.OK)
    } ~
    post {
        complete(StatusCodes.Forbidden)
      }
  } ~
    path("home"){
      complete(
      HttpEntity (
        ContentTypes.`text/html(UTF-8)`,
        """
          |<html>
          | <body>
          | hello from high level Api
          | </body>
          |</html>
          |""".stripMargin
      )
     )
    }
  Http().bindAndHandle(simpleRoute,"localhost",8080)
}
