package models

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import java.time.OffsetDateTime
import java.util.UUID

case class Product(
    id: UUID,
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
