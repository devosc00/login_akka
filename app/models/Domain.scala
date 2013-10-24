package models

import scala.slick.driver.H2Driver.simple._
import scala.reflect.runtime.universe._

// Use the implicit threadLocalSession
//import Database.threadLocalSession
//import scala.slick.ast.Join

case class Company(
  compId: Option[Int],
  name: String,
  street: String,
  city: String,
  state: String,
  zipCode: String)

case class User(
  //id: Option[Int],
  //name: String,
  name: Option[String],
  accID: Int,
  compID: Int,
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
  def compId = column[Int]("COMP_ID", O.PrimaryKey, O.AutoInc) // This is the primary key column
  def name = column[String]("COMP_NAME")
  def street = column[String]("STREET")
  def city = column[String]("CITY")
  def state = column[String]("STATE")
  def zipCode = column[String]("ZIP")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = compId.? ~ name ~ street ~ city ~ state ~ zipCode <> (Company.apply _, Company.unapply _)
  def autoInc = compId.? ~ name ~ street ~ city ~ state ~ zipCode <> (Company, Company.unapply _) returning compId

  def findAll() = for (s <- Companies) yield s

  /**
   * Construct the Map[String,String] needed to fill a select options set.
   */
  def options = this.findAll.map(x => x.compId -> x.name)
}

// Definition of the COFFEES table
object Users extends Table[User]("USERS") {
  //def id = column[Int]("USER_ID", O.PrimaryKey, O AutoInc) // This is the primary key column
  def name = column[String]("USER_NAME", O.PrimaryKey) // This is the primary key column
  def accID = column[Int]("ACC_ID")
  def compID = column[Int]("COMP_ID")
  def position = column[String]("POSSITION")
  def doneParts = column[Int]("DONE_PARTS")
  def setup = column[Int]("SETUP")
  //def account = column[Account]("ACCOUNT")
  //def * = id.? ~ name ~ supID ~ price ~ sales ~ total <> (Coffee.apply _, Coffee.unapply _)
  def * = name.? ~ accID ~ compID ~ position ~ doneParts ~ setup <> (User.apply _, User.unapply _)
  //def autoInc = id.? ~ name ~ supID ~ price ~ sales ~ total <> (Coffee, Coffee.unapply _) returning id
  // A reified foreign key relation that can be navigated to create a join
  def account = foreignKey("ACC_FK", accID, Account)(_.accId)

  def company = foreignKey("COMP_FK", compID, Companies)(_.compId)

  def findAll(filter: String = "%") = {
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


  def create(user: User) = database withTransaction {
    Users.insert(user)
  } 

 /* def findAccId(pk: String) = 
    val user = { user => findByPK(pk).get.accID }
    */
}