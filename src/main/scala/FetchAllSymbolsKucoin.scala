package org

import apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}

import akka.Done
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, Uri}
import spray.json._

import java.util.Properties
import scala.concurrent.Future
import scala.language.{implicitConversions, postfixOps}
import scala.concurrent.duration._


object FetchAllSymbolsKucoin extends KucoinJsonLibrary {

  implicit val system: ActorSystem = ActorSystem()
  import system.dispatcher

  // function that gets list of all symbols by calling CallKucoin class CallAPI method
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

    val props:Properties = new Properties()
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
    props.put("acks", "all")
    val producer = new KafkaProducer[String, String](props)

    system.scheduler.scheduleAtFixedRate(0 seconds, 60 seconds) {
      new Runnable {
        override def run(): Unit = {
          try {
            getSymbolsList(producer)
          } catch {
            case exception: Exception => exception.printStackTrace()
          }
        }
      }
    }
  }
}
