package models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

import java.util.UUID

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
    price: Option[Double],
    uuid: Option[UUID]
)
object ProductFromJson {
  implicit val productFromJsonDecoder: Decoder[ProductFromJson] =
    deriveDecoder[ProductFromJson]
  implicit val productFromJsonEncoder: Encoder[ProductFromJson] =
    deriveEncoder[ProductFromJson]
}
