package com.beauty

import com.github.tminglei.slickpg._

import scala.slick.driver.PostgresDriver

/** Custom Postgres driver with slick-pg extensions added.
  *
  */
trait DarkroomDriver extends PostgresDriver with PgArraySupport with PgSprayJsonSupport {
  def pgjson = "jsonb"

  override lazy val Implicit = new ImplicitsPlus {}
  override val simple = new SimpleQLPlus {}

  // SimpleArrayPlainImplicits is extended here so that we can use slickpg's `nextArray` to pull out a postgres array
  // into a scala collection.
  trait ImplicitsPlus extends Implicits with ArrayImplicits with JsonImplicits with SimpleArrayPlainImplicits
  trait SimpleQLPlus extends SimpleQL with ImplicitsPlus
}

object DarkroomDriver extends DarkroomDriver
