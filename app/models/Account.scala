package models

import play.api.db.slick.Config.driver.simple._
import play.api.db.DB
import play.api.Play.current
import scala.reflect.runtime.universe._


case class Account(accId: Option[Long], email: String, password: String, name: String,
compID: Long, position: String, permission: Permission)

object Accounts extends Table[Account]("ACCOUNT") {
  lazy val database = Database.forDataSource(DB.getDataSource())

  def accId = column[Long]("ACC_ID", O.PrimaryKey, O.AutoInc)
  def email = column[String]("EMAIL")
  def password = column[String]("PASSWORD")
  def name = column[String]("NAME")
  def compID = column[Long]("COMP_ID")
  def position = column[String]("POSITION") 
  def permission = column[Permission]("PERMISSION")

  def company = foreignKey("COMP_FK", compID, Companies)(_.compId)
  // Every table needs a * projection with the same type as the table's type parameter
  def * = accId.? ~ email ~ password ~ name ~ compID ~ position ~ permission <> (Account.apply _, Account.unapply _)
  def autoInc = email ~ password ~ name ~ compID ~ position ~ permission returning accId


  def authenticate(email: String, password: String): Option[Account] = {
    findByEmail(email).filter { account => password.equals(account.password) }
  }
  

  def fillPass(id: Long) = { 
    Accounts.filter(_.accId === id).map(p => p.password).toString

  }

  def findAll(filter: String ="%") = {
    for {
      s <- Accounts
      c <- s.company
      if (s.name like ("%" + filter))
    } yield (s, c)
  }

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%") = {
    val members = typeOf[Account].members.filter(m => m.isTerm && !m.isMethod).toList
    val fields = members.map(_.name).reverse.zipWithIndex
    println("Fields of Accounts: " + fields) // List((id ,0), (name ,1), (supID ,2), (price ,3), (sales ,4), (total ,5))
    findAll(filter).sortBy(_._1.name).drop(page * pageSize).take(pageSize)
  }

  def create(a: Account)(implicit s: Session) = database withTransaction {
    Accounts.autoInc.insert(a.email, a.password, a.name, a.compID, a.position, a.permission)
  }

  def update(id: Long, a: Account)(implicit s: Session) = database withTransaction {
          val up = Accounts.filter(_.accId === id)
            .map(q => q.name ~ q.email ~ q.position ~ q.permission)
            up.update(( a.name, a.email, a.position, a.permission))
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

  def findByPk(id: Long) = {
    for (n <- Accounts if n.accId === id) yield n
  }

  def findAll: Seq[Account] = {
    database withSession { implicit session =>
      val q1 = for (u <- Accounts) yield u
      q1.list
    }
  }
}