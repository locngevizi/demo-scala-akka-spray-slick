package com.beauty.models

import java.sql.Date
import java.util.UUID

import com.beauty.utils.Validators._
import spray.json._

/**
  * Created by Loc Ngo on 1/10/2017.
  */
trait BaseJsonProtocol extends DefaultJsonProtocol {

  implicit object UuidJsonFormat extends RootJsonFormat[UUID] {
    def write(x: UUID) = JsString(x.toString) //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) => if (validateUUID(x)) UUID.fromString(x) else deserializationError("Expected UUID, but got " + x)
      case x           => deserializationError("Expected UUID as JsString, but got " + x)
    }
  }

  implicit object DateJsonFormat extends RootJsonFormat[Date] {
    def write(x: Date) = JsString(x.toString) //Never execute this line
    def read(value: JsValue) = value match {
      case JsString(x) =>
        if (validateDate(x)) getDate(x) else deserializationError("Expected format Date is MM-dd-yyyy, but got " + x)
      case x           => deserializationError("Expected Date as JsString, but got " + x)
    }
  }
}
