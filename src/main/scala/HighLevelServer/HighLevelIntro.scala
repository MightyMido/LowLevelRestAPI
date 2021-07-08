package HighLevelServer
import akka.actor.ActorSystem
import akka.http.javadsl.server.Route
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives.{complete, path}
import akka.stream.ActorMaterializer


object HighLevelIntro extends App {
  implicit val system = ActorSystem("HighLevelAPI")
  implicit val materializer = ActorMaterializer
  import system.dispatcher

  //directives
  // server logic
  import akka.http.scaladsl.server.directives
  val simpleRoute:Route =
    path("home")
    { //Directive
      complete(StatusCodes.OK)
    }
  // Sending Back Responses
  // complete responses
  // http response
}
