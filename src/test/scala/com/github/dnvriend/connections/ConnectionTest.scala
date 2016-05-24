package com.github.dnvriend.connections

import com.github.dnvriend.TestSpec
import doobie.imports._
import scalaz._, Scalaz._
import scalaz.concurrent.Task

class ConnectionTest extends TestSpec {
  it should "0" in {
    val actions: ConnectionIO[Int] = 42.point[ConnectionIO]
    val task = actions.transact(xa)
    task.run shouldBe 42
  }

  it should "1" in {
    val actions: ConnectionIO[Int] = sql"select 42".query[Int].unique
    val task2 = actions.transact(xa)
    task2.run shouldBe 42
  }

  it should "2" in {
    val actions: ConnectionIO[(Int, Double)] =
      for {
        a ← sql"select 42".query[Int].unique
        b ← sql"select random()".query[Double].unique
      } yield (a, b)

    actions.transact(xa).run should matchPattern {
      case (x: Int, y: Double) ⇒
    }
  }

  it should "3" in {
    val actions: ConnectionIO[(Int, Double)] = {
      val a = sql"select 42".query[Int].unique
      val b = sql"select random()".query[Double].unique
      (a |@| b).tupled
    }
    actions.transact(xa).run should matchPattern {
      case (x: Int, y: Double) ⇒
    }
  }

  it should "4" in {
    val actions: ConnectionIO[(Int, Double)] = {
      val a = sql"select 42".query[Int].unique
      val b = sql"select random()".query[Double].unique
      (a |@| b).tupled
    }
    actions.replicateM(2).transact(xa).run should matchPattern {
      case (a: Int, b: Double) :: (c: Int, d: Double) :: Nil ⇒
    }
  }
}
