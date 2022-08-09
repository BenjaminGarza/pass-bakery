package services

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class APIService {
}

object APIService{
  //get DateTime from server
  def getDateTime: String ={
    val dateTime: String =  LocalDateTime.now().toString
    val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    val parsedDateTime: LocalDateTime = LocalDateTime.parse(dateTime,formatter)
    //Truncate to remove nano seconds
    parsedDateTime.truncatedTo(ChronoUnit.SECONDS).toString
  }
}
