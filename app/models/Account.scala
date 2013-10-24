package models

import scala.slick.driver.H2Driver.simple._
import play.api.db.DB
import play.api.Play.current

case class Account(accId: Option[Int], email: String, password: String, permission: Permission)

object Account extends Table[Account]("ACCOUNT") {
  lazy val database = Database.forDataSource(DB.getDataSource())

  def accId = column[Int]("ACC_ID", O.PrimaryKey, O.AutoInc)
  def email = column[String]("EMAIL")
  def password = column[String]("PASSWORD")
  def permission = column[Permission]("PERMISSION")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = accId.? ~ email ~ password ~ permission <> (Account.apply _, Account.unapply _)
  def autoInc = accId.? ~ email ~ password ~ permission <> (Account, Account.unapply _) returning accId

  val option = this.findAll.map(x => x.accId)

  def authenticate(email: String, password: String): Option[Account] = {
    findByEmail(email).filter { account => password.equals(account.password) }
  }

  def create(account: Account) = database withTransaction {
    Account.autoInc.insert(account)
  }

  def findByEmail(email: String): Option[Account] = {
    database withSession { implicit session =>
      val q1 = for (u <- Account if u.email === email) yield u
      q1.list.headOption.asInstanceOf[Option[Account]]
    }
  }

  def findById(accId: Int): Option[Account] = {
    database withSession { implicit session =>
      val q1 = for (u <- Account if u.accId === accId) yield u
      q1.list.headOption.asInstanceOf[Option[Account]]
    }
  }

  def findAccById(id: Int) = {
    for (n <- Account if n.accId === id) yield n
  }

  def findAll: Seq[Account] = {
    database withSession { implicit session =>
      val q1 = for (u <- Account) yield u
      q1.list
    }
  }
}