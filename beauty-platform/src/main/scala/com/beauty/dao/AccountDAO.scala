package com.beauty.dao

import java.sql.Date
import java.time.{Instant, LocalDate}
import java.util.UUID

import com.beauty.Persistence
import com.beauty.DarkroomDriver.simple._
import com.beauty.models.AccountProtocol.Account
import com.beauty.utils.Validators._

import scala.slick.lifted.{Column, ColumnOrdered, TableQuery}
import scala.util.{Failure, Success, Try}

/**
  * Created by Loc Ngo on 1/9/2017.
  */

class Accounts(tag: Tag) extends Table[Account](tag, "accounts") {
  def id = column[UUID]("id", O.PrimaryKey, O.NotNull, O.DBType("uuid"))
  def firtName = column[String]("firt_name", O.NotNull)
  def lastName = column[String]("last_name", O.NotNull)
  def dateOfBirth = column[Date]("date_of_birth", O.NotNull, O.DBType("date"))
  def sex = column[String]("sex", O.NotNull)
  def nickName = column[String]("nick_name", O.NotNull)
  def userName = column[String]("user_name", O.NotNull)
  def email = column[String]("email", O.NotNull)
  def phone = column[String]("phone", O.NotNull)
  def created = column[Long]("created", O.NotNull)
  def lastUpdated = column[Long]("last_updated", O.NotNull)
  def * = (id, firtName, lastName, dateOfBirth, sex, nickName, userName, email, phone, created, lastUpdated) <> (Account.apply, Account.unapply)
}

object AccountDAO extends TableQuery(new Accounts(_)) with BaseDAO[Account] {

  def findById(id: UUID): Option[Account] = db.withSession { implicit session =>
    (this filter (_.id === id)).firstOption
  }

  def insert(account: Account): Try[UUID] = db.withSession { implicit session =>
    Try {
      (this returning this.map(_.id)) += account
    }
  }

  def update(account: Account): Try[Int] = db.withSession { implicit session =>
    Try {
      this.filter(_.id === account.id).update(account)
    }
  }

  def delete(id: UUID): Try[Int] = db.withSession { implicit session =>
    Try {
      this.filter(_.id === id).delete
    }
  }

  def getAll(from: Int, pageSize: Int, sortBy: String, asc: Boolean, filter: Option[String], filterValue: Option[String]): Try[List[Account]] = db.withSession { implicit session =>
    Try {
      (filter, filterValue) match {
        case (Some(filter), Some(filterValue)) =>
          this.sortBy(
            sortBy match {
              case "firtName" => if(asc) _.firtName.asc.nullsFirst else _.firtName.desc.nullsFirst
              case "lastName" => if(asc) _.lastName.asc.nullsFirst else _.lastName.desc.nullsFirst
              case "dateOfBirth" => if(asc) _.dateOfBirth.asc.nullsFirst else _.dateOfBirth.desc.nullsFirst
              case "sex" => if(asc) _.sex.asc.nullsFirst else _.sex.desc.nullsFirst
              case "nickName" => if(asc) _.nickName.asc.nullsFirst else _.nickName.desc.nullsFirst
              case "userName" => if(asc) _.userName.asc.nullsFirst else _.userName.desc.nullsFirst
              case "email" => if(asc) _.email.asc.nullsFirst else _.email.desc.nullsFirst
              case "phone" => if(asc) _.phone.asc.nullsFirst else _.phone.desc.nullsFirst
              case "lastUpdated" => if(asc) _.lastUpdated.asc.nullsFirst else _.lastUpdated.desc.nullsFirst
              case _ => if(asc) _.created.asc.nullsFirst else _.created.desc.nullsFirst
            }
          ).drop(from).take(pageSize).filter(getColumnFilter(_, filter, filterValue)).list
        case _ =>
          this.sortBy(
            sortBy match {
              case "firtName" => if(asc) _.firtName.asc.nullsFirst else _.firtName.desc.nullsFirst
              case "lastName" => if(asc) _.lastName.asc.nullsFirst else _.lastName.desc.nullsFirst
              case "dateOfBirth" => if(asc) _.dateOfBirth.asc.nullsFirst else _.dateOfBirth.desc.nullsFirst
              case "sex" => if(asc) _.sex.asc.nullsFirst else _.sex.desc.nullsFirst
              case "nickName" => if(asc) _.nickName.asc.nullsFirst else _.nickName.desc.nullsFirst
              case "userName" => if(asc) _.userName.asc.nullsFirst else _.userName.desc.nullsFirst
              case "email" => if(asc) _.email.asc.nullsFirst else _.email.desc.nullsFirst
              case "phone" => if(asc) _.phone.asc.nullsFirst else _.phone.desc.nullsFirst
              case "lastUpdated" => if(asc) _.lastUpdated.asc.nullsFirst else _.lastUpdated.desc.nullsFirst
              case _ => if(asc) _.created.asc.nullsFirst else _.created.desc.nullsFirst
            }
          ).drop(from).take(pageSize).list
      }
    }
  }

  def getColumnFilter(account: Accounts, filter: String, filterValue: String): Column[Boolean] = {
    filter match {
      case "id" =>
        if (validateUUID(filterValue)) throw new Exception("Invalid Filter value")
        account.id === UUID.fromString(filterValue)
      case "firtName" => account.firtName like "%" + filterValue + "%"
      case "lastName" => account.lastName like "%" + filterValue + "%"
      case "dateOfBirth" =>
        if (validateDate(filterValue)) throw new Exception("Invalid Filter value")
        account.dateOfBirth === getDate(filterValue)
      case "sex" => account.sex like "%" + filterValue + "%"
      case "nickName" => account.nickName like "%" + filterValue + "%"
      case "userName" => account.userName like "%" + filterValue + "%"
      case "email" => account.email like "%" + filterValue + "%"
      case "phone" => account.phone like "%" + filterValue + "%"
      case _ => throw new Exception("Not Support filter for " + filter)
    }
  }
}

