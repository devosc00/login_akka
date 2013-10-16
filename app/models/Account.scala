package models

import scala.slick.driver.H2Driver.simple._
import play.api.db.DB
import play.api.Play.current

case class Account(id: Option [Int], email: String, password: String, name: String, permission: Permission)

object Account extends Table[Account]("ACCOUNT") {
  lazy val database = Database.forDataSource(DB.getDataSource())

  def id = column[Int]("ACC_ID", O.PrimaryKey)
  def email = column[String]("EMAIL")
  def password = column[String]("PASSWORD")
  def name = column[String]("NAME")
  def permission = column[Permission]("PERMISSION")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = id.? ~ email ~ password ~ name ~ permission <> (Account.apply _, Account.unapply _)

  def authenticate(email: String, password: String): Option[Account] = {
    findByEmail(email).filter { account => password.equals(account.password) }
  }

  def findByEmail(email: String): Option[Account] = {
    database withSession { implicit session =>
      val q1 = for (u <- Account if u.email === email) yield u
      q1.list.headOption.asInstanceOf[Option[Account]]
    }
  }

  def findById(id: Int): Option[Account] = {
    database withSession { implicit session =>
      val q1 = for (u <- Account if u.id === id) yield u
      q1.list.headOption.asInstanceOf[Option[Account]]
    }
  }

  def findByName(name: String) = {
    for (n <- Account if n.name === name) yield n
  }

  def findAll: Seq[Account] = {
    database withSession { implicit session =>
      val q1 = for (u <- Account) yield u
      q1.list
    }
  }
}