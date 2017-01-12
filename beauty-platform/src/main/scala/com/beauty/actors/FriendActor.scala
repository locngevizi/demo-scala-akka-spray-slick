package com.beauty.actors

import java.time.Instant
import java.util.UUID

import akka.actor.{Actor, ActorLogging}
import com.beauty.dao.FriendDAO
import com.beauty.models.CommonProtocol.Error
import com.beauty.models.FriendProtocol._
import com.beauty.rejections._
import spray.http.{StatusCode, StatusCodes}
import spray.httpx.SprayJsonSupport._
import spray.routing.Rejection

import scala.util.{Failure, Success}

/**
  * Created by Loc Ngo on 1/11/2017.
  */
class FriendActor extends Actor with ActorLogging {
  def receive = {
    case message: FriendMessage => message match {
      case m: InviteFriend =>
        val actorSender = sender()
        val now: Long = Instant.now.toEpochMilli.toLong
        val friend: Friend = new Friend(m.accountId.get, m.friendId, 0.toShort, now, now)
        FriendDAO.insert(friend) match {
          case Failure(t) => actorSender ! Left(new BeautyRejection(Option(StatusCodes.Conflict), t))
          case Success(result) => actorSender ! Right(true)
        }

      case m: Unfriend =>
        val actorSender = sender()
        FriendDAO.findById(m.accountId.get, m.friendId) match {
          case Failure(t) => actorSender ! Left(new BeautyRejection(Option(StatusCodes.InternalServerError), t))
          case Success(s) => s match {
            case Some(friend: Friend) =>
              if (friend.status == 1.toShort)
                FriendDAO.delete(m.accountId.get, m.friendId) match {
                  case Failure(t) => actorSender ! Left(new BeautyRejection(Option(StatusCodes.InternalServerError), t))
                  case Success(s) => s match {
                    case 0 => actorSender ! Left(new NotFoundRejection())
                    case _ => actorSender ! Right(true)
                  }
                }
              else
                actorSender ! Left(new BeautyRejection(Option(StatusCodes.NotFound), new Exception(m.friendId + " is not your friend")))
            case _ => actorSender ! Left(new NotFoundRejection())
          }
        }

      case m: RemoveRequestFriend =>
        val actorSender = sender()
        FriendDAO.findById(m.accountId.get, m.friendId) match {
          case Failure(t) => actorSender ! Left(new BeautyRejection(Option(StatusCodes.InternalServerError), t))
          case Success(s) => s match {
            case Some(friend: Friend) =>
              if (friend.status == 0.toShort)
                FriendDAO.delete(m.accountId.get, m.friendId) match {
                  case Failure(t) => actorSender ! Left(new BeautyRejection(Option(StatusCodes.InternalServerError), t))
                  case Success(s) => s match {
                    case 0 => actorSender ! Left(new NotFoundRejection())
                    case _ => actorSender ! Right(true)
                  }
                }
              else
                actorSender ! Left(new BeautyRejection(Option(StatusCodes.NotFound), new Exception(m.friendId + " does not send your friend request")))
            case _ => actorSender ! Left(new NotFoundRejection())
          }
        }

      case m: AcceptFriend =>
        val actorSender = sender()
        FriendDAO.findById(m.accountId.get, m.friendId) match {
          case Failure(t) => actorSender ! Left(new BeautyRejection(Option(StatusCodes.InternalServerError), t))
          case Success(s) => s match {
            case Some(friend: Friend) =>
              if (friend.status == 0.toShort) {
                val now: Long = Instant.now.toEpochMilli.toLong
                val friend: Friend = new Friend(m.accountId.get, m.friendId, 1.toShort, now, now)
                FriendDAO.update(friend) match {
                  case Failure(t) => actorSender ! Left(new BeautyRejection(Option(StatusCodes.InternalServerError), t))
                  case Success(result) => result match {
                    case 0 => actorSender ! Left(new NotFoundRejection())
                    case _ => actorSender ! Right(true)
                  }
                }
              }
              else
                actorSender ! Left(new BeautyRejection(Option(StatusCodes.NotFound), new Exception(m.friendId + " does not send your friend request")))
            case _ => actorSender ! Left(new BeautyRejection(Option(StatusCodes.NotFound), new Exception(m.friendId + " does not send your friend request")))
          }
        }

      case m: GetFriends =>
        val actorSender = sender()
        FriendDAO.getFriends(m.accountId, m.status, m.from, m.pageSize, m.sortBy, m.asc, m.filter, m.filterValue) match {
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
