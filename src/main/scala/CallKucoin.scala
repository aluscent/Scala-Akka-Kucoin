package org

import apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.stream.scaladsl.{Sink, Source}
import spray.json._

import scala.concurrent.duration._
import java.util.UUID
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Failure, Success}

class CallKucoin {
  /**
   * This class provides required methods to call Kucoin servers
   */

  implicit val system: ActorSystem = ActorSystem()
  import system.dispatcher

  // this method calls API of Kucoin and then runs a function on received data
  def callAPI[A,B](requests: HttpRequest)(toIterable: A => Iterable[B])(function: (B, String) => Unit)
                  (implicit AtoJson: RootJsonFormat[A]): Future[Done] =
    Source(List(Tuple2(requests, UUID.randomUUID().toString)))
      .via(Http().cachedHostConnectionPoolHttps[String]("api.kucoin.com"))
      .runWith(Sink.foreach {
        case (Success(response@HttpResponse(StatusCodes.OK, _, _, _)), value) =>
          println(s"[ACTOR] Response to request $value is alright (Code 202).")
          response.entity.toStrict(15 seconds).map(x =>
            toIterable(x.data.utf8String.parseJson.convertTo[A]).foreach(symbol_ => function(symbol_, value)))
        case (Failure(exception), value) =>
          println(s"[ACTOR] Response to request $value failed (Code 402) with: $exception")
        case response =>
          println(s"[ACTOR] Request was incorrect (Code 502): $response")
      })
}
