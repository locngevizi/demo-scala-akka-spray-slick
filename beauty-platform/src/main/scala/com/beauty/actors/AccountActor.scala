package com.beauty.actors

import java.time.Instant
import java.util.UUID

import akka.actor.{Actor, ActorLogging}
import com.beauty.dao.AccountDAO
import com.beauty.models.AccountProtocol._
import com.beauty.rejections._
import spray.http.StatusCodes

import scala.util.{Failure, Success}

/**
  * Created by Loc Ngo on 1/11/2017.
  */
class AccountActor extends Actor with ActorLogging {
  def receive = {
    case message: AccountMessage => message match {
      case m: CreateAccount =>
        val actorSender = sender()
        val now: Long = Instant.now.toEpochMilli.toLong
        AccountDAO.insert(m.account.copy(lastUpdated = now, created = now)) match {
          case Failure(t) => actorSender ! Left(new BeautyRejection(Option(StatusCodes.Conflict), t))
          case Success(result) => actorSender ! Right(true)
        }

      case m: DeleteAccount =>
        val actorSender = sender()
        AccountDAO.delete(m.accountId) match {
          case Failure(t) => actorSender ! Left(new BeautyRejection(Option(StatusCodes.InternalServerError), t))
          case Success(result) => result match {
            case 0 => actorSender ! Left(new NotFoundRejection())
            case _ => actorSender ! Right(true)
          }
        }

      case m: UpdateAccount =>
        val actorSender = sender()
        val now: Long = Instant.now.toEpochMilli.toLong
        AccountDAO.update(m.account.copy(lastUpdated = now)) match {
          case Failure(t) => actorSender ! Left(new BeautyRejection(Option(StatusCodes.InternalServerError), t))
          case Success(result) => result match {
            case 0 => actorSender ! Left(new NotFoundRejection())
            case _ => actorSender ! Right(true)
          }
        }

      case m: GetAccount =>
        val actorSender = sender()
        AccountDAO.findById(m.accountId) match {
          case Some(account: Account) => actorSender ! Right(account)
          case _ => actorSender ! Left(new NotFoundRejection())
        }

      case m: GetAccounts =>
        val actorSender = sender()
        AccountDAO.getAll(m.from, m.pageSize, m.sortBy, m.asc, m.filter, m.filterValue) match {
          case Failure(t) => actorSender ! Left(new BeautyRejection(Option(StatusCodes.InternalServerError), t))
          case Success(result) => actorSender ! Right(result)
        }

      case _ =>
        val actorSender = sender()
        actorSender ! Left(new NotImplementRejection())
    }
    case _ =>
      val actorSender = sender()
      actorSender ! Left(new NotImplementRejection())
  }
}
