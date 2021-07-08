package ServerSideAPI
import ServerSideAPI.GuitarDB.{AddQuantity, CreateGuitar, FindAllGuitars, FindGuitar, GuitarCreated}
import akka.actor.{Actor, ActorLogging, ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri.Query
import akka.util.Timeout
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, StatusCodes, Uri}
import akka.pattern.ask
import akka.stream.ActorMaterializer
import spray.json._

import scala.concurrent.Future
import scala.concurrent.duration._

case class Guitar(name:String , model:String ,  Quantity:Int = 0)

object GuitarDB {
  case class CreateGuitar(guitar:Guitar)
  case class GuitarCreated(id:Int)
  case class FindGuitar(id:Int)
  case class FinalGuitars(id:Int)
  case object FindAllGuitars
  case class AddQuantity(id:Int,Quantity:Int)
  case class FindGuitarsInStock(inStock:Boolean)
}

class GuitarDB extends Actor with ActorLogging
{
 import GuitarDB._
  var guitars: Map[Int,Guitar] = Map()
  var currentGuitarId:Int = 0

  override def receive :Receive = {
    case FindAllGuitars =>
      log.info("Searching for all Guitars")
      sender() ! guitars.values.toList
    case FindGuitar(id) =>
      log.info(s"Searching for Guitar: $id")
      sender() ! guitars.get(id)
    case CreateGuitar(guitar) =>
      log.info(s"adding guitar $guitar with id: $currentGuitarId")
      guitars = guitars + (currentGuitarId -> guitar)
      sender() ! GuitarCreated(currentGuitarId)
      currentGuitarId += 1
    case AddQuantity(id,quantity) =>
      log.info("trying to add quantity...")
      val guitar:Option[Guitar] = guitars.get(id)
      val newGuitar:Option[Guitar] = guitar.map {
        case Guitar(make,model,q) => Guitar(make , model , q + quantity)
      }
      newGuitar.foreach(guitar => guitars = guitars + (id -> guitar))
      sender() ! newGuitar
    case FindGuitarsInStock(inStock) =>
      log.info(s"searching for all Guitars ${if(inStock)"in" else "out of"} stock.")
      if (inStock)
        sender ! guitars.values.filter(_.Quantity > 0)
      else
        sender() ! guitars.values.filter(_.Quantity == 0)
  }
}

trait GuitarStoreJsonProtocol extends DefaultJsonProtocol {
  implicit val GuitarFormat = jsonFormat3(Guitar)
}

object LowLevelRest extends App with GuitarStoreJsonProtocol {
  implicit val system = ActorSystem("LowLevelApi")
  implicit val materializer = ActorMaterializer()
  import system.dispatcher
  import GuitarDB._

  // json marshaling
  val simpleGuitar = Guitar("Fender","Stratocaster",3)
  println(simpleGuitar.toJson.prettyPrint)

  // json unMarshaling
  val simpleGuitarString =
    """
      |{
      |  "model": "Stratocaster",
      |  "name": "Fender",
      |  "Quantity": 3
      |}
    """.stripMargin
    println(simpleGuitarString.parseJson.convertTo[Guitar])
  /*
  * localhost:8080
  * Get on local
  * Post, this will serialize the problems and customer scoring */
  /* Json marshaling process of serializing data */

  val guitardb = system.actorOf(Props[GuitarDB],"LowLevelApi")
  val guitarList = List(
    Guitar("Fender","Stratocaster") ,
    Guitar("Gibson","les Paul") ,
    Guitar("Martin","LX1")
  )


  guitarList.foreach{guitar => guitardb ! CreateGuitar(guitar)}
  implicit val defaultTimeout = Timeout(2 seconds)
  def getGuitar(query:Query):Future[HttpResponse] = {
    val guitarId = query.get("id").map(_.toInt) // Option[Int]
    guitarId match {
      case None => Future(HttpResponse(StatusCodes.NotFound))
      case Some(id: Int) =>
        val guitarFuture: Future[Option[Guitar]] = (guitardb ? FindGuitar(id)).mapTo[Option[Guitar]]
        guitarFuture.map {
          case None => HttpResponse(StatusCodes.NotFound)
          case Some(guitar) => HttpResponse(
            entity = HttpEntity(
              ContentTypes.`application/json`,
              guitar.toJson.prettyPrint
           )
         )
       }
    }
  }

  val requestHandler:HttpRequest => Future[HttpResponse] = {
    case HttpRequest(HttpMethods.POST,
    uri@Uri.Path("/api/Guitar/Inventory"), _, _, _) =>
      val query = uri.query()
      val guitarId: Option[Int] = query.get("id").map(_.toInt)
      val guitarQuantity: Option[Int] = query.get("Quantity").map(_.toInt)
      val validResponseFuture:Option[Future[HttpResponse]] = for {
       id <- guitarId
       quantity <- guitarQuantity
      } yield {
        // Todo Construct Http Response
        val newGuitarFuture:Future[Option[Guitar]] =
          (guitardb ? AddQuantity(id , quantity)).mapTo[Option[Guitar]]
        newGuitarFuture.map(_ => HttpResponse(StatusCodes.OK))
      }
      validResponseFuture.getOrElse(Future(HttpResponse(StatusCodes.BadRequest)))
    case HttpRequest(HttpMethods.GET,uri@Uri.Path("/api/Guitar/Inventory"), _, _, _) =>
         val query = uri.query()
         val inStockOption = query.get("inStock").map(_.toBoolean)
         // Todo send a message to the guitarDB to fetch all Guitars in or out of stock
         inStockOption match {
           case Some(inStockOption) =>
             val GuitarsFuture:Future[List[Guitar]] =
               (guitardb ? FindGuitarsInStock(inStockOption)).mapTo[List[Guitar]]
             GuitarsFuture.map{guitars =>
               HttpResponse(
                 entity = HttpEntity(
                   ContentTypes.`application/json`,
                   guitars.toJson.prettyPrint
                   )
                 )
               }
           case None => Future(HttpResponse(StatusCodes.BadRequest))
             }

    case HttpRequest(HttpMethods.GET,uri@Uri.Path("/api/Guitar"), _, _, _) =>
      /*
      * query parameter handling code
      * */
      val query = uri.query()
      /* the consumer Scoring problems will solve this issue from the begining */
      if (query.isEmpty){
      val guitarsFuture:Future[List[Guitar]] = (guitardb ? FindAllGuitars).mapTo[List[Guitar]]
      guitarsFuture.map {guitars =>
        HttpResponse(
          entity = HttpEntity(
            ContentTypes.`application/json`,
            guitars.toJson.prettyPrint
            )
          )
        }
      }
      else {
        getGuitar(query)
      }
    case HttpRequest(HttpMethods.POST, Uri.Path("/api/guitar"), _, entity, _) =>
         //entities are a source[]
         val strictEntityFuture = entity.toStrict(3 seconds)
         strictEntityFuture.flatMap{ strictEntity =>
           val guitarJsonString  = strictEntity.data.utf8String
           val guitar = guitarJsonString.parseJson.convertTo[Guitar]
           val guitarCreatedFuture:Future[GuitarCreated] = (guitardb ? CreateGuitar(guitar)).mapTo[GuitarCreated]
           guitarCreatedFuture.map{ _ => HttpResponse(StatusCodes.OK)}
         }

    case request: HttpRequest => request.discardEntityBytes()
      Future {
        HttpResponse(status = StatusCodes.NotFound)
      }
  }
  Http().bindAndHandleAsync(requestHandler,"localHost",8080)
  /**
   * - enhance guitar case class with a quantity field by default value is zero
   * - Get to /api/guitar/inventory/id=X and Quantity=Y which adds Guitars to the stock
   * - POST to /api/guitar/inventory/id=X&quantity=Y which adds Y guitars to the stock with id X
   * - consumer Scoring platforms and services
   **/

}
