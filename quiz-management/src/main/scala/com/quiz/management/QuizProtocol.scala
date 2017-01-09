package com.quiz.management


/**
  * Created by Loc Ngo on 12/29/2016.
  */

object QuizProtocol {

  import spray.json._

  case class Quiz(id: Option[String], question: Option[String], correctAnswer: Option[String], lastUpdated: Option[Long], created: Option[Long])

  case object QuizCreated

  case object QuizUpdated

  case class QuizAlreadyExists(message: String)

  case object QuizDeleted

  case object QuizNotFound

  case class QuizError(message: String)

  /* json (un)marshalling*/
  object Quiz extends DefaultJsonProtocol {
    implicit val format = jsonFormat5(Quiz.apply)

    type M = Quiz
    //type R = (/* id */ String, /* question */ String, /* correctAnswer */ String, /* data */ JsValue)
    type R = (/* id */ String, /* data */ JsValue)

    def apply(row: R): M = {
      val (id, data) = row
      val combinedJson = new JsObject(data.asJsObject.fields + ("id" -> JsString(id)))
      combinedJson.convertTo[M]
    }

    def unapply1(model: M): Option[R] = {
      val combinedJson = model.toJson.asJsObject.fields - "id"
      Option(model.id.get, combinedJson.toJson)
    }
  }
}