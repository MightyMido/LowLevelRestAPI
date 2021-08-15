package ServerSideAPI
/* import asynchronized request Handler */
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.Http.IncomingConnection
import akka.http.scaladsl.model._
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Sink}
import scala.concurrent.Future


import OracleDBConnection.{oracleDBConnection}

object PFM_Low_level_API extends App {
  val V:oracleDBConnection = new oracleDBConnection
  val T = V.execute_sql_query(List("000096411017", "000096461008", "000097211017"))
  println(T)

  //  println("ready to start masoud lets go")
//  implicit val system = ActorSystem("PFMLowLevelAPI")
//  implicit val materializer = ActorMaterializer()
//  import system.dispatcher
//
//  /*val serverSource = Http().bind("localhost" , 8080)*/
//  val connectionSink = Sink.foreach[IncomingConnection]
//    {
//      connection => println(s"Accepted Incoming connection from:${connection.remoteAddress}")
//    }
////  val serverBindingFuture = serverSource.to(connectionSink).run()
////  serverBindingFuture.onComplete
////  {
////    case Success(binding) =>
////      println("successful binding")
////      binding.terminate(2 seconds)
////    case Failure(ex) => println(s"failed with code : $ex")
////  }
//
//  val requestHandler:HttpRequest => HttpResponse = {
//    case HttpRequest(HttpMethods.GET, _, _, _, _) =>
//      HttpResponse(
//        StatusCodes.OK, //Http 200
//        entity = HttpEntity(
//          ContentTypes.`text/html(UTF-8)`,
//        """
//          |<html>
//          | <body>
//          | Oops I am going to be great you Bet!!!
//          | </body>
//          |</html>
//        """.stripMargin
//        )
//        )
//    case request: HttpRequest => request.discardEntityBytes()
//      HttpResponse(
//        StatusCodes.NotFound, // error 404
//        entity = HttpEntity(ContentTypes.`text/html(UTF-8)`,
//      """
//        |<html>
//        | <body>
//        | Oops No terminal Found!!!
//        | </body>
//        |</html>
//      """.stripMargin
//    )
//    )
//  }
//
//  val httpSyncConnectionHandler = Sink.foreach[IncomingConnection]{
//    connection => connection.handleWithSyncHandler(requestHandler)
//  }
////  Http().bind("localhost",8080).runWith(httpSyncConnectionHandler)
//
//  val asyncRequestHandler:HttpRequest => Future[HttpResponse] = {
//    case HttpRequest(HttpMethods.GET, Uri.Path("/home"), _, _, _) =>
//      Future(HttpResponse(
//        StatusCodes.OK, //Http 200
//        entity = HttpEntity(
//          ContentTypes.`text/html(UTF-8)`,
//          """
//            |<html>
//            | <body>
//            | Oops I am going to be great you Bet!!!
//            | </body>
//            |</html>
//        """.stripMargin
//        )
//      )
//    )
//
//    case request: HttpRequest => request.discardEntityBytes()
//      Future(HttpResponse(
//        StatusCodes.NotFound, // error 404
//        entity = HttpEntity(ContentTypes.`text/html(UTF-8)`,
//          """
//            |<html>
//            | <body>
//            | Oops Akka says Hello!!!
//            | </body>
//            |</html>
//          """.stripMargin
//       )
//      )
//    )
//  }
//  val httpAsyncConnectionHandler = Sink.foreach[IncomingConnection]{
//    connection => connection.handleWithAsyncHandler(asyncRequestHandler)
//  }
////  Http().bind("localhost",8080).runWith(httpAsyncConnectionHandler)
////  Http().bindAndHandleAsync(asyncRequestHandler,"localhost",8081)
//
//  val streamsBasedRequestHandler:Flow[HttpRequest,HttpResponse, _]= Flow[HttpRequest].map{
//    case HttpRequest(HttpMethods.GET,Uri.Path("/home"),_,_,_) => HttpResponse(
//      StatusCodes.OK, // Http 200
//      entity = HttpEntity(
//        ContentTypes.`text/html(UTF-8)`,
//        """
//          |<html>
//          | <body>
//          | hello, How di, your talking to the greatest Iranian Programmer ever!!!
//          | </body>
//          |</html>
//        """.stripMargin
//      )
//    )
//
//    case request: HttpRequest => request.discardEntityBytes()
//      HttpResponse(
//        StatusCodes.NotFound, // error 404
//        entity = HttpEntity(ContentTypes.`text/html(UTF-8)`,
//          """
//            |<html>
//            | <body>
//            | Oops Akka says Hello To the greatest Akka !!!
//            | </body>
//            |</html>
//      """.stripMargin
//        )
//      )
//    }
////  Http().bind("localhost",8082).runForeach{
////    connection => connection.handleWith(streamsBasedRequestHandler)}
//  Http().bindAndHandle(streamsBasedRequestHandler,"localhost",251251)
}
