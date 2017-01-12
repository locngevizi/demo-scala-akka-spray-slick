package com.beauty.dao

import java.time.Instant
import java.util.UUID

import com.beauty.Persistence
import com.beauty.models.FriendProtocol.Friend
import com.beauty.DarkroomDriver.simple._
import com.beauty.models.AccountProtocol.Account
import com.beauty.utils.Validators.{getDate, validateDate, validateUUID}

import scala.slick.lifted.{Column, TableQuery}
import scala.util.{Failure, Success, Try}

/**
  * Created by Loc Ngo on 1/10/2017.
  */

class Friends(tag: Tag) extends Table[Friend](tag, "account_friends") {
  def accountId = column[UUID]("account_id", O.NotNull, O.DBType("uuid"))
  def friendId = column[UUID]("friend_id", O.NotNull, O.DBType("uuid"))
  def status = column[Short]("status", O.NotNull)
  def created = column[Long]("created", O.NotNull)
  def lastUpdated = column[Long]("last_updated", O.NotNull)
  def pk = primaryKey("account_friends_pkey", (accountId, friendId))
  def friend =  foreignKey("account_friends_friend_id_fkey", friendId, AccountDAO)(_.id)
  def account =  foreignKey("account_friends_account_id_fkey", accountId, AccountDAO)(_.id)
  def * = (accountId, friendId, status, created, lastUpdated) <> (Friend.apply, Friend.unapply)
}

object FriendDAO extends TableQuery(new Friends(_)) with BaseDAO[Friend] {

  def findById(accountId: UUID, friendId: UUID): Try[Option[Friend]] = db.withSession { implicit session =>
    Try {
      (this filter (f => f.accountId === accountId && f.friendId === friendId)).firstOption
    }
  }

  def insert(friend: Friend): Try[(UUID, UUID)] = db.withSession { implicit session =>
    Try {
      (this returning (this map (row =>  (row.accountId, row.friendId)))) += friend
    }
  }

  def update(friend: Friend): Try[Int] = db.withSession { implicit session =>
    Try {
      val mili: Long = Instant.now.toEpochMilli.toLong
      this.filter(f => f.accountId === friend.accountId && f.friendId === friend.friendId).update(friend.copy(lastUpdated = mili))
    }
  }

  def delete(accountId: UUID, friendId :UUID): Try[Int] = db.withSession { implicit session =>
    Try {
      this.filter(f => f.accountId === accountId && f.friendId === friendId).delete
    }
  }

  def getFriends(accountId: UUID ,status: Short, from: Int, pageSize: Int, sortBy: String, asc: Boolean, filter: Option[String], filterValue: Option[String]): Try[List[Account]] = db.withSession { implicit session =>
    Try {
      (filter, filterValue) match {
        case (Some(filter), Some(filterValue)) =>
          for (elem <- this.filter(row => row.accountId === accountId && row.status === status).leftJoin(AccountDAO).on(_.friendId === _.id).sortBy(
            sortBy match {
              case "firtName" => if (asc) _._2.firtName.asc.nullsFirst else _._2.firtName.desc.nullsFirst
              case "lastName" => if (asc) _._2.lastName.asc.nullsFirst else _._2.lastName.desc.nullsFirst
              case "dateOfBirth" => if (asc) _._2.dateOfBirth.asc.nullsFirst else _._2.dateOfBirth.desc.nullsFirst
              case "sex" => if (asc) _._2.sex.asc.nullsFirst else _._2.sex.desc.nullsFirst
              case "nickName" => if (asc) _._2.nickName.asc.nullsFirst else _._2.nickName.desc.nullsFirst
              case "userName" => if (asc) _._2.userName.asc.nullsFirst else _._2.userName.desc.nullsFirst
              case "email" => if (asc) _._2.email.asc.nullsFirst else _._2.email.desc.nullsFirst
              case "phone" => if (asc) _._2.phone.asc.nullsFirst else _._2.phone.desc.nullsFirst
              case "lastUpdated" => if (asc) _._2.lastUpdated.asc.nullsFirst else _._2.lastUpdated.desc.nullsFirst
              case _ => if (asc) _._2.created.asc.nullsFirst else _._2.created.desc.nullsFirst
            }
          ).drop(from).take(pageSize).filter(getColumnFilter(_, filter, filterValue)).list) yield (elem._2)
        case _ =>
          for (elem <- this.filter(row => row.accountId === accountId && row.status === status).leftJoin(AccountDAO).on(_.friendId === _.id).sortBy(
            sortBy match {
              case "firtName" => if (asc) _._2.firtName.asc.nullsFirst else _._2.firtName.desc.nullsFirst
              case "lastName" => if (asc) _._2.lastName.asc.nullsFirst else _._2.lastName.desc.nullsFirst
              case "dateOfBirth" => if (asc) _._2.dateOfBirth.asc.nullsFirst else _._2.dateOfBirth.desc.nullsFirst
              case "sex" => if (asc) _._2.sex.asc.nullsFirst else _._2.sex.desc.nullsFirst
              case "nickName" => if (asc) _._2.nickName.asc.nullsFirst else _._2.nickName.desc.nullsFirst
              case "userName" => if (asc) _._2.userName.asc.nullsFirst else _._2.userName.desc.nullsFirst
              case "email" => if (asc) _._2.email.asc.nullsFirst else _._2.email.desc.nullsFirst
              case "phone" => if (asc) _._2.phone.asc.nullsFirst else _._2.phone.desc.nullsFirst
              case "lastUpdated" => if (asc) _._2.lastUpdated.asc.nullsFirst else _._2.lastUpdated.desc.nullsFirst
              case _ => if (asc) _._2.created.asc.nullsFirst else _._2.created.desc.nullsFirst
            }
          ).drop(from).take(pageSize).list) yield (elem._2)
      }
    }
  }

  def getColumnFilter(row: (Friends, Accounts), filter: String, filterValue: String): Column[Boolean] = {
    filter match {
      case "id" =>
        if (validateUUID(filterValue)) throw new Exception("Invalid Filter value")
        row._2.id === UUID.fromString(filterValue)
      case "firtName" => row._2.firtName like "%" + filterValue + "%"
      case "lastName" => row._2.lastName like "%" + filterValue + "%"
      case "dateOfBirth" =>
        if (validateDate(filterValue)) throw new Exception("Invalid Filter value")
        row._2.dateOfBirth === getDate(filterValue)
      case "sex" => row._2.sex like "%" + filterValue + "%"
      case "nickName" => row._2.nickName like "%" + filterValue + "%"
      case "userName" => row._2.userName like "%" + filterValue + "%"
      case "email" => row._2.email like "%" + filterValue + "%"
      case "phone" => row._2.phone like "%" + filterValue + "%"
      case _ => throw new Exception("Not Support filter for " + filter)
    }
  }
}
