package Recap

import akka.actor.SupervisorStrategy.{Restart, Stop}
import akka.actor.{Actor, ActorLogging, ActorSystem, OneForOneStrategy, Props, Stash, SupervisorStrategy}
import akka.util.Timeout

object AkkaRecap extends App {
  class SimpleActor extends Actor with ActorLogging with Stash {
    override def receive: Receive = {
      case "stash" => stash()
      case "change handler now" =>
        unstashAll()
        context.become(anotherHandler)
      case "create child" =>
        val childActor = context.actorOf(Props[SimpleActor], "myChild")
        childActor ! "Hello"
      case "change" => context.become(anotherHandler)
      case message => println(s"message received : $message")
    }

    override def preStart(): Unit = {
      log.info("Starting a new child actor")
    }

    override def supervisorStrategy: SupervisorStrategy = OneForOneStrategy() {
      case _: RuntimeException => Restart
      case _ => Stop
    }

    def anotherHandler: Receive = {
      case message => println(s"In another receive handler: $message")
    }
  }

  val system = ActorSystem("AkkaRecap")
  //
  val actor = system.actorOf(Props[SimpleActor], "simpleActor")
  // sending message
  actor ! "Hello"
  //change context

  // Spwaning other actors
  // /system guardian /user guardian
  // defined life cycle resumed restarted
  //actor ! PoisonPill
  // supervision : handling child actor behavior
  // configure Akka infrastructures: dispatchers,routers , mailboxes

  import system.dispatcher

  import scala.concurrent.duration._

  system.scheduler.scheduleOnce(2 seconds) {
    actor ! "delayed welcome"
  }

  //akka patterns : including FSM + ask pattern

  import akka.pattern.ask

  implicit val timeout = Timeout(3 seconds)
  val future = actor ? "question"

  import akka.pattern.pipe

  val another = system.actorOf(Props[SimpleActor], "s")
  future.mapTo[String].pipeTo(another)

}
