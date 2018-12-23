package org.ardlema.enrichment

import java.util.Collections

import JavaSessionize.avro.{Sale, SaleAndStore}
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde
import org.apache.kafka.common.serialization.{Serde, Serdes}
import org.apache.kafka.streams.Topology
import org.apache.kafka.streams.scala.ImplicitConversions._
import org.apache.kafka.streams.scala.StreamsBuilder
import org.apache.kafka.streams.scala.kstream.KStream

object EnrichmentTopologyBuilder {

  case class StoreInformation(storeAddress: String, storeCity: String)

  val storesInformation = Map(1234 -> StoreInformation("C/ Narvaez, 78", "Madrid"),
    5678 -> StoreInformation("C/ Pradillo, 33", "Madrid"))

  def getAvroSaleSerde(schemaRegistryHost: String, schemaRegistryPort: String) = {
    val specificAvroSerde = new SpecificAvroSerde[Sale]()
    specificAvroSerde.configure(
      Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, s"""http://$schemaRegistryHost:$schemaRegistryPort/"""),
      false)
    specificAvroSerde
  }

  def getAvroSaleAndStoreSerde(schemaRegistryHost: String, schemaRegistryPort: String) = {
    val specificAvroSerde = new SpecificAvroSerde[SaleAndStore]()
    specificAvroSerde.configure(
      Collections.singletonMap(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, s"""http://$schemaRegistryHost:$schemaRegistryPort/"""),
      false)
    specificAvroSerde
  }


  def createTopology(schemaRegistryHost: String,
    schemaRegistryPort: String,
    inputTopic: String,
    outputTopic: String,
    outputTopicError: String): Topology = {

    implicit val serdes: Serde[String] = Serdes.String()
    implicit val avroSaleSerde: SpecificAvroSerde[Sale] = getAvroSaleSerde(schemaRegistryHost, schemaRegistryPort)
    implicit val avroSaleAndStoreSerde: SpecificAvroSerde[SaleAndStore] = getAvroSaleAndStoreSerde(schemaRegistryHost, schemaRegistryPort)

    val existsStoreId: (String, Sale) => Boolean = (_, sale) => storesInformation.contains(sale.getStoreid)

    val notExistsStoreId: (String, Sale) => Boolean = (_, sale) => !storesInformation.contains(sale.getStoreid)


    val builder = new StreamsBuilder()
    val initialStream: KStream[String, Sale] = builder.stream(inputTopic)

    //TODO: Check out whether the store id from the sales event exists within the storesInformation hashmap. If it exists you should modify the event,
    // convert it to a SaleAndStore object and redirect it to the outputTopic topic. If it does not exist you should redirect the event to the outputTopicError topic.

    val splittedStream: Array[KStream[String, Sale]] = initialStream.branch(existsStoreId, notExistsStoreId)

    val saleAndStoreStream: KStream[String, SaleAndStore] = splittedStream(0)
      .mapValues[SaleAndStore]((sale: Sale) => new SaleAndStore(
      sale.getAmount,
      sale.getProduct,
      storesInformation(sale.getStoreid).storeAddress,
      storesInformation(sale.getStoreid).storeCity))

    val errorStream = splittedStream(1)

    saleAndStoreStream.to(outputTopic)
    errorStream.to(outputTopicError)

    builder.build()
  }
}
