package org

import elasticsearch.client.{Client, Request, RestClient}

import org.apache.http.HttpHost
import org.elasticsearch.client.transport.TransportClient
import org.elasticsearch.common.settings.Settings

import java.net.InetSocketAddress

object TestElasticsearch {


  def main(args: Array[String]): Unit = {
    val jsonString = """{
      "title": "Elastic",
      "price": 2000,
      "author":{
        "first": "Zachary",
        "last": "Tong";
      }
    }""".stripMargin

    val settings = Settings.builder.put("cluster.name", "elasticsearch").build()
    // val client = RestClient.builder(new HttpHost("localhost", 9200)).build()
    val client = RestClient
      .builder(new HttpHost("localhost", 9200)).build()
    val request = new Request("GET", "/?format=JSON")
    val response = client.performRequest(request)
    println(response.getEntity.getContent)
    client.close()

  }
}
