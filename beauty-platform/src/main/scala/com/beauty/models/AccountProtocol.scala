package com.beauty.models

import java.sql.Date
import java.util.UUID
import com.beauty.utils.Validators._

/**
  * Created by Loc Ngo on 1/9/2017.
  */
object AccountProtocol {
  import spray.json._

  sealed trait AccountMessage

  case class Account(id: UUID, firtName: String, lastName: String, dateOfBirth: Date, sex: String, nickName: String, userName: String, email: String, phone: String, created: Long, lastUpdated: Long)

  case class CreateAccount(account: Account) extends AccountMessage

  case class GetAccounts(from: Int, pageSize: Int, sortBy: String, asc: Boolean, filter: Option[String], filterValue: Option[String]) extends AccountMessage

  case class GetAccount(accountId: UUID) extends AccountMessage

  case class DeleteAccount(accountId: UUID) extends AccountMessage

  case class UpdateAccount(account: Account) extends AccountMessage

  /* json (un)marshalling*/
  object Account extends BaseJsonProtocol {

    implicit val format = jsonFormat11(Account.apply)

    type M = Account
    type R = (/*id*/ UUID, /*firtName*/ String, /*lastName*/ String, /*dateOfBirth*/ Date, /*sex*/ String, /*nickName*/ String, /*userName*/ String, /*email*/ String, /*phone*/ String, /*created*/ Long, /*lastUpdated*/ Long)

    def apply(row: R): M = {
      val (id, firtName, lastName, dateOfBirth, sex, nickName, userName, email, phone, created, lastUpdated) = row
      new M(id, firtName, lastName, dateOfBirth, sex, nickName, userName, email, phone, created, lastUpdated)
    }
  }
}
