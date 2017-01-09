package com.quiz.management

import java.time.Instant

import com.quiz.management.DarkroomDriver.simple._
import com.quiz.management.QuizProtocol.Quiz
import spray.json.JsValue

import scala.slick.lifted.{ColumnOrdered, TableQuery}
import scala.util.{Failure, Success, Try}

/**
  * Created by Loc Ngo on 12/30/2016.
  */

class Quizs(tag: Tag) extends Table[Quiz](tag, "quizs") {
  def id = column[String]("id", O.PrimaryKey, O.NotNull)
  def data = column[JsValue]("data", O.DBType("jsonb"), O.Nullable)
  def * = (id, data) <> (Quiz.apply, Quiz.unapply1)
}

object QuizsDAO extends TableQuery(new Quizs(_)){
  val db = Persistence.db

  def findById(id: String): Try[Option[Quiz]] = db.withSession { implicit session =>
    try {
      Success((this filter (_.id === id)).firstOption)
    } catch {
      case e: Exception => {
        Failure(e)
      }
    }

  }

  def insert(quiz: Quiz): Try[String] = db.withSession { implicit session =>
    val mili: Option[Long] = Option(Instant.now.toEpochMilli.toLong)
    try {
      Success((this returning this.map(_.id)) += quiz.copy(lastUpdated = mili, created = mili))
    } catch {
      case e: Exception => {
        Failure(e)
      }
    }

  }

  def update(quiz: Quiz): Try[Int] = db.withSession { implicit session =>
    val mili: Option[Long] = Option(Instant.now.toEpochMilli.toLong)
    try {
      val data:JsValue = Quiz.unapply1(quiz.copy(lastUpdated = mili)).get._2
      Success(this.filter(_.id === quiz.id).map(row => (row.data)).update(data))
    } catch {
      case e: Exception => {
        Failure(e)
      }
    }
  }

  def delete(id: String): Int = db.withSession { implicit session =>
      this.filter(_.id === id).delete
  }

  def getAll(from: Int, pageSize: Int, sortBy: String, asc: Boolean, filter: Option[String], filterValue: Option[String]): Try[List[Quiz]] = db.withSession { implicit session =>
    try {
      Success(
        (filter, filterValue) match {
          case (Some("id"), Some(filterValue)) =>
            this.sortBy(getColumnOrdered(_, sortBy, asc)).drop(from).take(pageSize).filter(row => row.id like "%" + filterValue + "%").list
          case (Some(filter), Some(filterValue)) =>
            this.sortBy(getColumnOrdered(_, sortBy, asc)).drop(from).take(pageSize).filter(row => row.data.+>>(filter) like "%" + filterValue + "%").list
          case _ =>
            this.sortBy(getColumnOrdered(_, sortBy, asc)).drop(from).take(pageSize).list
        }
      )
    } catch {
      case e: Exception => {
        Failure(e)
      }
    }
  }

  def getColumnOrdered(quizs: Quizs, sortBy: String, asc: Boolean): ColumnOrdered[String] = {
    sortBy match {
      case "id" => if(asc) quizs.id.asc.nullsFirst else quizs.id.desc.nullsFirst
      case _ => if(asc) quizs.data.+>>(sortBy).asc.nullsFirst else quizs.data.+>>(sortBy).desc.nullsFirst
    }
  }
}
