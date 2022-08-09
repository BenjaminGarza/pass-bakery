package models

case class Status(service: String, environment: String, serverTime: String) {

  object Status {
    def apply(service: String, environment: String, serverTime: String) ={
    }
  }
}






