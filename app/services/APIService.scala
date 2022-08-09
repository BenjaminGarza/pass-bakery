package services

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class APIService {}

object APIService {
  //get DateTime from server
  def getDateTime: String = {
    val parsedDateTime: LocalDateTime = LocalDateTime.parse(
      LocalDateTime.now().toString,
      DateTimeFormatter.ISO_LOCAL_DATE_TIME
    )
    //Truncate to remove nano seconds
    parsedDateTime.truncatedTo(ChronoUnit.SECONDS).toString
  }
}
