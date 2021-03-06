package org.ardlema.joining

import JavaSessionize.avro.{Coupon, Purchase}
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.kafka.streams.processor.TimestampExtractor

class GenericTimeStampExtractor extends TimestampExtractor {

  override def extract(record: ConsumerRecord[AnyRef, AnyRef], l: Long): Long = {
    val topic = record.topic()

    topic match {
      case "purchase-input" | "purchase-notjoin-input" => {
        val purchase = record.value().asInstanceOf[Purchase]
        purchase.getTimestamp
      }
      case "coupon-input" | "coupon-notjoin-input" => {
        val coupon = record.value().asInstanceOf[Coupon]
        coupon.getTimestamp
      }
    }

  }
}
