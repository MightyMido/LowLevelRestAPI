package Recap

import akka.actor.ActorSystem
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}
import akka.stream.{ActorMaterializer, OverflowStrategy}

import scala.util.{Failure, Success}

object AkkaStreams extends App {

  import system.dispatcher

  implicit val system = ActorSystem("AkkaStreamsRecap")
  implicit val materializer = ActorMaterializer()
  val source = Source(1 to 100)
  val sink = Sink.foreach[Int](println)
  val flow = Flow[Int].map(x => x + 1)

  // Todo we need to understand akka streams
  // Todo apparently need to install a database on our os

  val graph = source.via(flow).to(sink)
  val simpleMaterializedValue = graph.run() //materialization
  val sumSink = Sink.fold[Int, Int](0)((currentSum, element) => currentSum + element)
  val sumFuture = source.runWith(sumSink)

  sumFuture.onComplete {
    case Success(sum) => println(s"success with sum $sum")
    case Failure(ex) => println(s"Failure with exception : $ex")
  }
  val anotherMvalue = source.viaMat(flow)(Keep.right).toMat(sink)(Keep.left).run()
  val bufferFlow = Flow[Int].buffer(10, OverflowStrategy.dropHead)
  source.async.via(bufferFlow).async.runForeach {
    e =>
      Thread.sleep(100)
      println(e)
  }
  /* intermediate of back pressure  */
  /* Akka Http is not a Framework */
  /* create some kind of logic favorite project create package */
}
