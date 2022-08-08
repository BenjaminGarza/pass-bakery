package models

case class Status(service: String, environment: String, serverTime: String) {

  object Status {
    def apply(service: String, environment: String, serverTime: String): Status ={
      var status = new Status(service, environment, serverTime)
      status
    }
  }
}




