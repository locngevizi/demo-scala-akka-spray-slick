package com.beauty.utils

import java.sql.Date
import java.text.SimpleDateFormat
import java.util.UUID

import scala.util.Try

/**
  * Created by Loc Ngo on 1/10/2017.
  */
object Validators {
  def validateUUID(maybeUUID: String): Boolean = Try(UUID.fromString(maybeUUID)).isSuccess

  // Lifted regex from play.api.data.validation.Constraint (https://github.com/playframework/playframework/blob/master/framework/src/play/src/main/scala/play/api/data/validation/Validation.scala)
  val emailMatcher = "^[a-zA-Z0-9\\.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$".r
  def validateEmail(email: String): Boolean = email match {
    case emailMatcher() => true
    case _ => false
  }

  val dateFormat = "MM-dd-yyyy"
  def validateDate(date:String): Boolean = {
    Try( new SimpleDateFormat(dateFormat).parse(date)).isSuccess
  }

  def getDate(date: String): Date = {
    new Date(new SimpleDateFormat("MM-dd-yyyy").parse(date).getTime)
  }
}
