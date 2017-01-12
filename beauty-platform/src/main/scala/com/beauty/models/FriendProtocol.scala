package com.beauty.models

import java.util.UUID

/**
  * Created by Loc Ngo on 1/10/2017.
  */
object FriendProtocol extends {

  sealed trait FriendMessage

  case class Friend(accountId: UUID, friendId: UUID, status: Short, created: Long, lastUpdated: Long) extends FriendMessage

  case class InviteFriend(accountId: Option[UUID], friendId: UUID) extends FriendMessage

  case class Unfriend(accountId: Option[UUID], friendId: UUID) extends FriendMessage

  case class AcceptFriend(accountId: Option[UUID], friendId: UUID) extends FriendMessage

  case class RemoveRequestFriend(accountId: Option[UUID], friendId: UUID) extends FriendMessage

  case class GetFriends(accountId: UUID, status: Short, from: Int, pageSize: Int, sortBy: String, asc: Boolean, filter: Option[String], filterValue: Option[String]) extends FriendMessage

  /* json (un)marshalling*/
  object Friend extends BaseJsonProtocol {

    implicit val format = jsonFormat5(Friend.apply)

    type M = Friend
    type R = (/*acoountId*/ UUID, /*friendId*/ UUID, /*status*/ Short, /*created*/ Long, /*lastUpdated*/ Long)

    def apply(row: R): M = {
      val (accountId, friendId, status, created, lastUpdated) = row
      new M(accountId, friendId, status, created, lastUpdated)
    }
  }

  object InviteFriend extends BaseJsonProtocol {

    implicit val format = jsonFormat2(InviteFriend.apply)

    type M = InviteFriend
    type R = (/*accountId*/ Option[UUID], /*friendId*/ UUID)

    def apply(row: R): M = {
      val (accountId, friendId) = row
      new M(accountId, friendId)
    }
  }

  object Unfriend extends BaseJsonProtocol {

    implicit val format = jsonFormat2(Unfriend.apply)

    type M = Unfriend
    type R = (/*accountId*/ Option[UUID], /*friendId*/ UUID)

    def apply(row: R): M = {
      val (accountId, friendId) = row
      new M(accountId, friendId)
    }
  }

  object AcceptFriend extends BaseJsonProtocol {

    implicit val format = jsonFormat2(AcceptFriend.apply)

    type M = AcceptFriend
    type R = (/*accountId*/ Option[UUID], /*friendId*/ UUID)

    def apply(row: R): M = {
      val (accountId, friendId) = row
      new M(accountId, friendId)
    }
  }

  object RemoveRequestFriend extends BaseJsonProtocol {

    implicit val format = jsonFormat2(RemoveRequestFriend.apply)

    type M = RemoveRequestFriend
    type R = (/*accountId*/ Option[UUID], /*friendId*/ UUID)

    def apply(row: R): M = {
      val (accountId, friendId) = row
      new M(accountId, friendId)
    }
  }
}
