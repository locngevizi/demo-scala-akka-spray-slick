package com.quiz.management

/**
  * Created by Loc Ngo on 12/29/2016.
  */

import akka.actor._
import akka.util.Timeout
import spray.http.StatusCodes
import spray.httpx.SprayJsonSupport._
import spray.routing._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success}

class RestInterface extends HttpServiceActor with RestApi{
  def receive = runRoute(routes)
}

trait RestApi extends HttpService with ActorLogging { actor: Actor =>
  import QuizProtocol._

  implicit val timeout = Timeout(10 seconds)

  def routes: Route =
    pathPrefix("quizzes") {
      pathEnd {
        post {
          entity(as[Quiz]) { quiz => requestContext =>
            val responder = createResponder(requestContext)
            QuizsDAO.insert(quiz) match {
              case Failure(t) => responder ! QuizAlreadyExists(t.getMessage)
              case Success(result) => responder ! QuizCreated
            }
          }
        } ~
          get {
            parameter('from.as[Int] ? 0, 'pageSize.as[Int] ? 10, 'sortBy ? "lastUpdated", 'asc.as[Boolean] ? true, 'filter.?, 'filterValue.?) { (from, pageSize, sortBy, asc, filter, filterValue) =>
              requestContext =>
                val responder = createResponder(requestContext)
                QuizsDAO.getAll(from, pageSize, sortBy, asc, filter, filterValue) match {
                  case Failure(t) => responder ! QuizError(t.getMessage)
                  case Success(result) => responder ! result
                }
            }
          }
      } ~
        path(Segment) { id =>
          delete { requestContext =>
            val responder = createResponder(requestContext)
            QuizsDAO.delete(id) match {
              case 0 => responder ! QuizNotFound
              case _ => responder ! QuizDeleted
            }
          } ~
            get { requestContext =>
              val responder = createResponder(requestContext)
              QuizsDAO.findById(id) match {
                case Failure(t) => requestContext.complete(StatusCodes.NotFound, t.getMessage)
                case Success(result) => result.getOrElse(None) match {
                  case quiz: Quiz => responder ! quiz
                  case None => responder ! QuizNotFound
                }
              }
            } ~
            post {
              entity(as[Quiz]) { quiz => requestContext =>
                val responder = createResponder(requestContext)
                QuizsDAO.update(quiz) match {
                  case Failure(t) => requestContext.complete(StatusCodes.InternalServerError, t.getMessage)
                  case Success(result) => result match {
                    case 0 => responder ! QuizNotFound
                    case _ => responder ! QuizUpdated
                  }
                }
              }
            }
        }
    }
  private def createResponder(requestContext:RequestContext) = {
    context.actorOf(Props(new Responder(requestContext)))
  }
}

class Responder(requestContext:RequestContext) extends Actor with ActorLogging {
  import QuizProtocol._

  def receive = {
    case QuizCreated =>
      requestContext.complete(StatusCodes.Created)
      killYourself

    case QuizUpdated =>
      requestContext.complete(StatusCodes.OK)
      killYourself

    case QuizDeleted =>
      requestContext.complete(StatusCodes.OK)
      killYourself

    case message :QuizAlreadyExists =>
      requestContext.complete(StatusCodes.Conflict, message.message)
      killYourself

    case quiz: Quiz =>
      requestContext.complete(StatusCodes.OK, quiz)
      killYourself

    case QuizNotFound =>
      requestContext.complete(StatusCodes.NotFound)
      killYourself

    case quizzes: List[Quiz] =>
      requestContext.complete(StatusCodes.OK, quizzes)
      killYourself

    case error: QuizError =>
      requestContext.complete(StatusCodes.InternalServerError, error.message)
      killYourself

    case _ =>
      requestContext.complete(StatusCodes.BadRequest)
      killYourself
  }

  private def killYourself = self ! PoisonPill
}