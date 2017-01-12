package com.beauty.dao

import com.beauty.Persistence
import com.beauty.models.FriendProtocol.Friend

import scala.util.{Failure, Try}

/**
  * Created by Loc Ngo on 1/10/2017.
  */
trait BaseDAO[T] {
  val db = Persistence.db

  def tryWithError(errorMessage: String)(f: => T): Try[T] = {
    Try(f) recoverWith {
      case e => {
        Failure(e)
      }
    }
  }
}
