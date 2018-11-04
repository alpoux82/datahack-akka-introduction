package com.datahack.akka.streams.example4

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source

import scala.concurrent.{ExecutionContext, Future}

/*
 * Vamos a aprender como recuperarnos de un error en un stream con el método recover
 */

object RecoverFlow {

  // TODO: creamos de forma implícita el actor system
  implicit val system: ActorSystem = ActorSystem("RecoverFlow")

  // TODO: creamos de forma implícita el execution context para gestionar el bind del actor Http
  implicit val executionContext: ExecutionContext = system.dispatcher

  // TODO: creamos de forma implícita el materializador que necesitamos para materializar el stream
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  // TODO: creamos un source que emita valores enteros del 0 al 6
  // si el valor es menor de 5 lo pasamos a string
  // sino lanzamos una RuntimeException
  // utilizamos el método recover del source para en caso de capturar la RuntimeException añadir el mensje
  // "stream truncated" en el stream
  val source: Source[String, NotUsed] = Source(0 to 6).map(n ⇒
    if (n < 5) n.toString
    else throw new RuntimeException("Boom!")
  ).recover {
    case _: RuntimeException ⇒ "stream truncated"
  }

  // TODO: creamos el método runFlow que materializa el source y por cada elemento del stream lo pinta por pantalla
  def runFlow: Future[Done] = {
    source.runForeach(println)
  }

  // TODO: creamos el método main que llama al método runFlow y cuando se completa el flujo terminamos el actor system
  // vamos ha sobrescribir este método en vez de utilizar el trait App para poder testear el flujo
  def main(args: Array[String]) = {
    runFlow.onComplete { _ =>
      system.terminate()
    }
  }

}
