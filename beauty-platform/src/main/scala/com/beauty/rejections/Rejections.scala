package com.beauty.rejections

import spray.http.StatusCode
import spray.routing.Rejection

/**
  * Created by Loc Ngo on 1/12/2017.
  */

case class BeautyRejection(statusCode: Option[StatusCode], error: Throwable) extends Rejection
case class NotFoundRejection() extends Rejection
case class NotImplementRejection() extends Rejection