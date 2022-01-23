package org

import apache.kafka.clients.consumer.KafkaConsumer

import java.time.Duration
import scala.concurrent.duration._
import java.util.Properties
import scala.collection.JavaConverters._
import spray.json._
import apache.kafka.clients.admin.Admin

import scala.language.postfixOps

object FetchSymbolKafka extends KucoinJsonLibrary {

  def main(args: Array[String]): Unit = {

    val props: Properties = new Properties()
    props.put("group.id", "g1")
    props.put("bootstrap.servers", "localhost:9092")
    props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer")
    props.put("enable.auto.commit", "true")
    props.put("auto.commit.interval.ms", "1000")
    val consumer = new KafkaConsumer(props)
    val topics = List("kucoin-symbols")

    try {
      consumer.subscribe(asJavaCollection(topics))
      while (true) {
        val records: List[Symbol] = consumer.poll(Duration.ofMillis(100)).asScala
          .map(_.value().toString.parseJson.convertTo[Symbol]).toList
        records.foreach { record =>
          println(record)

          val tickerFetcher = new FetchTickersKucoin(record.name, 5 seconds)
          tickerFetcher.start()
          println(s"Name of this pair is: ${record.name}")
        }
      }
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }
}
