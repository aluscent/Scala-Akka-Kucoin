package org

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, Uri}
import akka.stream.scaladsl.{Sink, Source}
import apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import akka.http.scaladsl.model.Uri.Query
import spray.json._

import scala.concurrent.duration._
import java.util.{Properties, UUID}
import scala.concurrent.Future
import scala.language.postfixOps
import scala.util.{Failure, Success}



class FetchTickersKucoin(pairName: String, intervalSeconds: FiniteDuration) extends KucoinJsonLibrary {
  
  implicit val system: ActorSystem = ActorSystem()
  import system.dispatcher

  def getSymbolsList(producer: KafkaProducer[String,String]): Future[Done] = {
    val caller = new CallKucoin
    caller.callAPI[CodeWrapper[Ticker], Ticker](
      HttpRequest(HttpMethods.GET, Uri("/api/v1/market/orderbook/level1").withQuery(Query("symbol" -> pairName)))
    )(ticker => Iterable(ticker.data))((symbol_, value) => {
      println(symbol_)
      producer.send(new ProducerRecord(pairName, value, symbol_.toJson.prettyPrint)).get()
      producer.flush()
      println(s"record $value uploaded to kafka.")
    })
  }


  def start(): Unit = {
    val props:Properties = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("acks", "all")
    val producer = new KafkaProducer[String, String](props)

    system.scheduler.scheduleAtFixedRate(0 seconds, intervalSeconds) {
      new Runnable {
        override def run(): Unit = {
          getSymbolsList(producer)
        }
      }
    }
  }
}
