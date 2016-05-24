package com.github.dnvriend

import java.util.UUID

import org.scalatest.{ BeforeAndAfterAll, _ }
import org.scalatest.concurrent.{ Eventually, ScalaFutures }
import org.scalatest.prop.PropertyChecks

import scala.concurrent.{ ExecutionContext, Future }
import scala.concurrent.duration._
import scala.util.Try

import doobie.imports._
import scalaz._, Scalaz._
import scalaz.concurrent.Task

trait TestSpec extends FlatSpec with Matchers with ScalaFutures with TryValues with OptionValues with Eventually with BeforeAndAfterAll with BeforeAndAfterEach {
  import ExecutionContext.Implicits.global

  /**
   * A Transactor is simply a structure that knows how to connect to a database, hand out connections,
   * and clean them up; and with this knowledge it can transform ConnectionIO ~> Task, which gives us
   * something we can run. Specifically it gives us a Task that, when run, will connect to the database
   * and run our program in a single transaction.
   */
  val xa: Transactor[Task] = DriverManagerTransactor[Task]("org.postgresql.Driver", "jdbc:postgresql://boot2docker:5432/docker", "docker", "docker")
  implicit val pc: PatienceConfig = PatienceConfig(timeout = 3.seconds)

  /**
   * Returns a random UUID
   */
  def randomId = UUID.randomUUID.toString.take(5)

  implicit class PimpedByteArray(self: Array[Byte]) {
    def getString: String = new String(self)
  }

  implicit class PimpedFuture[T](self: Future[T]) {
    def toTry: Try[T] = Try(self.futureValue)
  }
}
