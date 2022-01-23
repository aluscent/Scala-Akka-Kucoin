package org

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, Uri}
import apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import spray.json._

import scala.concurrent.Future

object FetchAllTickersKucoin extends KucoinJsonLibrary {
  implicit val system = ActorSystem()
  import system.dispatcher

  def getSymbolsList(producer: KafkaProducer[String,String]): Future[Done] = {
    val caller = new CallKucoin

    caller.callAPI[CodeWrapper[Seq[Symbol]], Symbol](HttpRequest(HttpMethods.GET, Uri("/api/v1/symbols"))
    )(symbolList => symbolList.data)((symbol_, value) => {
      println(symbol_)
      val metadata = producer.send(new ProducerRecord("kucoin-symbols", value, symbol_.toJson.prettyPrint)).get()
      producer.flush()
      println(s"sent record with key $value metadata: partition ${metadata.partition()} and offset ${metadata.offset()}")
    })
  }

  def main(args: Array[String]): Unit = {

  }
}
