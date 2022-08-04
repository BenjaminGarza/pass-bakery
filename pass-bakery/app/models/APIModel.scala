package models

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import io.circe.generic.extras.auto._
import io.circe.syntax._
import io.circe.parser
import io.circe.generic.extras.Configuration
import io.circe

case class Status(service: "pass-bakery", environment: String, serverTime: String)

object Status{

  //get DateTime from server
  def getDateTime: String ={
    var dateTime: String =  LocalDateTime.now().toString
    var formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    var parsedDateTime: LocalDateTime = LocalDateTime.parse(dateTime,formatter)
    return parsedDateTime.toString
  }


  //Need json format
def getStatus: String ={
  //We need to return this info
  val status = Status("pass-bakery","environment variable", getDateTime)


  //Set implicit value for encoder parameter (Otherwise we get "could not find implicit value" error when serializing)
  implicit val customConfig: Configuration = {
    Configuration.default.withDiscriminator("type")
   }
  //Serialize to Json
  return status.asJson.spaces2

}
  def test: String={
    return "test OK"
  }
}
