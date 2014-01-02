package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.DB
import play.api.Play.current

case class Account(accId: Option[Long], email: String, password: String, permission: Permission)

object Accounts extends Table[Account]("ACCOUNT") {
  lazy val database = Database.forDataSource(DB.getDataSource())

  def accId = column[Long]("ACC_ID", O.PrimaryKey, O.AutoInc)
  def email = column[String]("EMAIL")
  def password = column[String]("PASSWORD")
  def permission = column[Permission]("PERMISSION")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = accId.? ~ email ~ password ~ permission <> (Account.apply _, Account.unapply _)
  def autoInc = email ~ password ~ permission returning accId


  def authenticate(email: String, password: String): Option[Account] = {
    findByEmail(email).filter { account => password.equals(account.password) }
  }

  def create(account: Account)(implicit s: Session) = database withTransaction {
    Accounts.autoInc.insert(account.email, account.password, account.permission)
  }

  def findByEmail(email: String): Option[Account] = {
    database withSession { implicit session =>
      val q1 = for (u <- Accounts if u.email === email) yield u
      q1.list.headOption.asInstanceOf[Option[Account]]
    }
  }

  def findById(accId: Long): Option[Account] = {
    database withSession { implicit session =>
      val q1 = for (u <- Accounts if u.accId === accId) yield u
      q1.list.headOption.asInstanceOf[Option[Account]]
    }
  }

  def findAccById(id: Long) = {
    for (n <- Accounts if n.accId === id) yield n
  }

  def findAll: Seq[Account] = {
    database withSession { implicit session =>
      val q1 = for (u <- Accounts) yield u
      q1.list
    }
  }
}