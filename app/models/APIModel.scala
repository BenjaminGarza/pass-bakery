package models

import io.circe.{Decoder, Encoder}
import io.circe.generic.JsonCodec
import io.circe.syntax._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import java.time.OffsetDateTime

case class ServiceStatus(
    service: String,
    environment: String,
    serverTime: String
)
object ServiceStatus {
  implicit val statusDecoder: Decoder[ServiceStatus] =
    deriveDecoder[ServiceStatus]
  implicit val statusEncoder: Encoder[ServiceStatus] =
    deriveEncoder[ServiceStatus]
}

case class ProductFromJson(
    name: Option[String],
    quantity: Option[Int],
    price: Option[Double]
)
object ProductFromJson {
  implicit val productFromJsonDecoder: Decoder[ProductFromJson] =
    deriveDecoder[ProductFromJson]
  implicit val productFromJsonEncoder: Encoder[ProductFromJson] =
    deriveEncoder[ProductFromJson]
}
case class Product(
    id: String,
    name: String,
    quantity: Int,
    price: Double,
    createdAt: OffsetDateTime,
    updatedAt: OffsetDateTime
)
object Product {
  implicit val productDecoder: Decoder[Product] =
    deriveDecoder[Product]
  implicit val productEncoder: Encoder[Product] =
    deriveEncoder[Product]
}
