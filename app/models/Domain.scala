package models

import play.api.db.slick.Config.driver.simple._
import scala.reflect.runtime.universe._
import play.api.db.DB
import play.api.Play.current
// Use the implicit threadLocalSession
//import Database.threadLocalSession
//import scala.slick.ast.Join

case class Company(
  compId: Option[Long],
  name: String,
  street: String,
  city: String,
  zipCode: String)

case class User(
  //id: Option[Long],
  //name: String,
  name: Option[String],
  accID: Option[Long],
  compID: Option[Long],
  position: String,
  doneParts: Int,
  setup: Int)

/**
 * Helper for pagination.
 */
case class Page[A](items: Seq[A], page: Int = 0, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}

// Definition of the SUPPLIERS table
object Companies extends Table[Company]("COMPANIES") {
  lazy val database = Database.forDataSource(DB.getDataSource())

  def compId = column[Long]("COMP_ID", O.PrimaryKey, O.AutoInc) // This is the primary key column
  def name = column[String]("COMP_NAME")
  def street = column[String]("STREET")
  def city = column[String]("CITY")
  def zipCode = column[String]("ZIP")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = compId.? ~ name ~ street ~ city ~ zipCode <> (Company.apply _, Company.unapply _)
  def autoInc = compId.? ~ name ~ street ~ city ~ zipCode <> (Company, Company.unapply _) returning compId

  def findAll() = for (s <- Companies) yield s

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options = this.findAll.map(x => x.compId -> x.name)

   def create(company: Company)(implicit s: Session) = database withTransaction {
    Companies.autoInc.insert(company)
  } 
}

// Definition of the COFFEES table
object Users extends Table[User]("USERS") {
  lazy val database = Database.forDataSource(DB.getDataSource())

  //def id = column[Int]("USER_ID", O.PrimaryKey, O AutoInc) // This is the primary key column
  def name = column[String]("USER_NAME", O.PrimaryKey) // This is the primary key column
  def accID = column[Long]("ACC_ID")
  def compID = column[Long]("COMP_ID")
  def position = column[String]("POSSITION")
  def doneParts = column[Int]("DONE_PARTS")
  def setup = column[Int]("SETUP")
  //def account = column[Account]("ACCOUNT")
  //def * = id.? ~ name ~ supID ~ price ~ sales ~ total <> (Coffee.apply _, Coffee.unapply _)
  def * = name.? ~ accID.? ~ compID.? ~ position ~ doneParts ~ setup <> (User.apply _, User.unapply _)
  //def autoInc = id.? ~ name ~ supID ~ price ~ sales ~ total <> (Coffee, Coffee.unapply _) returning id
  // A reified foreign key relation that can be navigated to create a join
  def account = foreignKey("ACC_FK", accID, Accounts)(_.accId)

  def company = foreignKey("COMP_FK", compID, Companies)(_.compId)

  def findAll(filter: String ="%") = {
    for {
      s <- Users
      c <- s.company
      if (s.name like ("%" + filter))
    } yield (s, c)
  }

  def list(page: Int = 0, pageSize: Int = 10, orderBy: Int = 1, filter: String = "%") = {
    val members = typeOf[User].members.filter(m => m.isTerm && !m.isMethod).toList
    val fields = members.map(_.name).reverse.zipWithIndex
    println("Fields of users: " + fields) // List((id ,0), (name ,1), (supID ,2), (price ,3), (sales ,4), (total ,5))
    findAll(filter).sortBy(_._1.name).drop(page * pageSize).take(pageSize)
  }

  def findByPK(pk: String) =
    for (u <- Users if u.name === pk) yield u


  def create(user: User)(implicit s: Session) = database withTransaction {
    Users.insert(user)
  } 

 /* def findAccId(pk: String) = 
    val user = { user => findByPK(pk).get.accID }
    */
}