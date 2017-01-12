package com.beauty.routes

import java.util.UUID

import akka.pattern._
import akka.actor._
import com.beauty.models.AccountProtocol._
import com.beauty.models.FriendProtocol._
import com.beauty.models.CommonProtocol._
import spray.http.{StatusCode, StatusCodes}
import spray.httpx.SprayJsonSupport._
import spray.routing._
import spray.json._
import spray.json.DefaultJsonProtocol._

import scala.concurrent.duration.DurationLong
import scala.util.{Failure, Success, Try}
import akka.util.Timeout
import com.beauty.actors._
import com.beauty.dao._
import com.beauty.rejections._
import com.beauty.utils.Validators._
import spray.util.LoggingContext

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by Loc Ngo on 1/9/2017.
  */
class ApiService extends HttpServiceActor with RestApi {
  def receive = runRoute(routes)
}

trait RestApi extends HttpService with ActorLogging { actor: Actor =>

  implicit val timeout = Timeout(10 seconds)

  val fActor = context.actorOf(Props(new FriendActor()))
  val aActor = context.actorOf(Props(new AccountActor()))

  def routes: Route =
    pathPrefix("accounts") {
      handleRejections(apiRejectionHandler) {
        pathEnd {
          post {
            entity(as[Account]) { account => requestContext =>
              (aActor ? new CreateAccount(account)).mapTo[Either[Rejection, Boolean]] map {
                case Left(x) => requestContext.reject(x)
                case Right(x) => x match {
                  case true => requestContext.complete(StatusCodes.Created)
                  case false => requestContext.complete(StatusCodes.InternalServerError, "Unknown error")
                }
              }
            }
          } ~
          get {
            parameter('from.as[Int] ? 0, 'pageSize.as[Int] ? 10, 'sortBy ? "lastUpdated", 'asc.as[Boolean] ? true, 'filter.?, 'filterValue.?) { (from, pageSize, sortBy, asc, filter, filterValue) =>
              requestContext =>
                (aActor ? GetAccounts(from, pageSize, sortBy, asc, filter, filterValue)).mapTo[Either[Rejection, List[Account]]].map {
                  case Left(x) => requestContext.reject(x)
                  case Right(result) => requestContext.complete(StatusCodes.OK, result)
                }
            }
          }
        } ~
        pathPrefix(Segment) { accountId =>
          validate(validateUUID(accountId), "Invalid AccountId") {
            (pathEnd & delete) {
              requestContext =>
                (aActor ? new DeleteAccount(UUID.fromString(accountId))).mapTo[Either[Rejection, Boolean]] map {
                  case Left(x) => requestContext.reject(x)
                  case Right(x) => x match {
                    case true => requestContext.complete(StatusCodes.OK)
                    case false => requestContext.complete(StatusCodes.InternalServerError, "Unknown error")
                  }
                }
            } ~
            (pathEnd & get) {
              requestContext =>
                (aActor ? new GetAccount(UUID.fromString(accountId))).mapTo[Either[Rejection, Account]] map {
                  case Left(x) => requestContext.reject(x)
                  case Right(x) => requestContext.complete(StatusCodes.OK, x)
                }
            } ~
            (pathEnd & post) {
              entity(as[Account]) { account =>
                requestContext =>
                  (aActor ? new UpdateAccount(account)).mapTo[Either[Rejection, Boolean]] map {
                    case Left(x) => requestContext.reject(x)
                    case Right(x) => requestContext.complete(StatusCodes.OK)
                  }
              }
            } ~
            pathPrefix("friends") {
              (pathEnd & get) {
                parameter('from.as[Int] ? 0, 'pageSize.as[Int] ? 10, 'sortBy ? "lastUpdated", 'asc.as[Boolean] ? true, 'filter.?, 'filterValue.?) { (from, pageSize, sortBy, asc, filter, filterValue) =>
                  requestContext =>
                    (fActor ? GetFriends(UUID.fromString(accountId), 1.toShort, from, pageSize, sortBy, asc, filter, filterValue)).mapTo[Either[Rejection, List[Account]]].map {
                      case Left(x) => requestContext.reject(x)
                      case Right(result) => requestContext.complete(StatusCodes.OK, result)
                    }
                }
              } ~
              pathPrefix("inviteFriend") {
                (post & pathEnd) {
                  entity(as[InviteFriend]) { inviteFriend =>
                    requestContext =>
                      (fActor ? new InviteFriend(Option(UUID.fromString(accountId)), inviteFriend.friendId)).mapTo[Either[Rejection, Boolean]] map {
                        case Left(x) => requestContext.reject(x)
                        case Right(x) => x match {
                          case true => requestContext.complete(StatusCodes.Created)
                          case false => requestContext.complete(StatusCodes.InternalServerError, "Unknown error")
                        }
                      }
                  }
                }
              } ~
              pathPrefix("unFriend") {
                (delete & pathEnd) {
                  entity(as[Unfriend]) { unfriend =>
                    requestContext =>
                      (fActor ? new Unfriend(Option(UUID.fromString(accountId)), unfriend.friendId)).mapTo[Either[Rejection, Boolean]] map {
                        case Left(x) => requestContext.reject(x)
                        case Right(x) => x match {
                          case true => requestContext.complete(StatusCodes.OK)
                          case false => requestContext.complete(StatusCodes.InternalServerError, "Unknown error")
                        }
                      }
                  }
                }
              }
            } ~
            pathPrefix("requestFriends") {
              (pathEnd & get) {
                parameter('from.as[Int] ? 0, 'pageSize.as[Int] ? 10, 'sortBy ? "lastUpdated", 'asc.as[Boolean] ? true, 'filter.?, 'filterValue.?) { (from, pageSize, sortBy, asc, filter, filterValue) =>
                  requestContext =>
                    (fActor ? GetFriends(UUID.fromString(accountId), 0.toShort, from, pageSize, sortBy, asc, filter, filterValue)).mapTo[Either[Rejection, List[Account]]].map {
                      case Left(x) => requestContext.reject(x)
                      case Right(result) => requestContext.complete(StatusCodes.OK, result)
                    }
                }
              } ~
                pathPrefix("remove") {
                  (delete & pathEnd) {
                    entity(as[RemoveRequestFriend]) { friend =>
                      requestContext =>
                        (fActor ? new RemoveRequestFriend(Option(UUID.fromString(accountId)), friend.friendId)).mapTo[Either[Rejection, Boolean]].map {
                          case Left(x) => requestContext.reject(x)
                          case Right(x) => x match {
                            case true => requestContext.complete(StatusCodes.OK)
                            case false => requestContext.complete(StatusCodes.InternalServerError, "Unknown error")
                          }
                        }
                    }
                  }
                } ~
                pathPrefix("accept") {
                  (post & pathEnd) {
                    entity(as[AcceptFriend]) { friend =>
                      requestContext =>
                        (fActor ? new AcceptFriend(Option(UUID.fromString(accountId)), friend.friendId)).mapTo[Either[Rejection, Boolean]].map {
                          case Left(x) => requestContext.reject(x)
                          case Right(x) => x match {
                            case true => requestContext.complete(StatusCodes.OK)
                            case false => requestContext.complete(StatusCodes.InternalServerError, "Unknown error")
                          }
                        }
                    }
                  }
                }
            }
          }
        }
      }
    }

  /** Wraps the api route to handle rejections.
    *
    * This should only handle rejections generated in the 'api' route
    * subtree. It should never handle the Nil (spray representation of a 404)
    * rejections list! That would cause requests destined for HtmlService to
    * never make it there.
    *
    * We can only respond with an appropriate status code here - redirections
    * will not work inside an ajax request.
    */
  def apiRejectionHandler(implicit log: LoggingContext): RejectionHandler = RejectionHandler {
    case rejections @ BeautyRejection(statuscode, error) :: _ => {
      respondAndCompleteWithStatus(statuscode.getOrElse(StatusCodes.InternalServerError), Option(error.getMessage))
    }

    case rejections @ NotFoundRejection() :: _ => {
      respondAndCompleteWithStatus(StatusCodes.NotFound)
    }

    case rejections @ NotImplementRejection() :: _ => {
      respondAndCompleteWithStatus(StatusCodes.NotImplemented)
    }

    case rejections @ (rejection: Rejection) :: _ => {
      respondAndCompleteWithStatus(StatusCodes.NotFound)
    }
  }

  def apiRejectionResponse(statusCode: StatusCode, message: Option[String] = None, info: Option[JsObject] = None): JsObject = {
    JsObject("statusCode" -> statusCode.value.toJson, "message" -> message.toJson, "info" -> info.toJson)
  }

  def respondAndCompleteWithStatus(code: StatusCode, message: Option[String] = None, info: Option[JsObject] = None): Route = {
    respondWithStatus(code) {
      complete { apiRejectionResponse(code, message, info) }
    }
  }
}