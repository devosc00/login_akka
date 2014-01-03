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
  city: String,
  orders: Int)


case class Page[A](items: Seq[A], page: Int = 0, offset: Long, total: Long) {
  lazy val prev = Option(page - 1).filter(_ >= 0)
  lazy val next = Option(page + 1).filter(_ => (offset + items.size) < total)
}


object Companies extends Table[Company]("COMPANIES") {
  lazy val database = Database.forDataSource(DB.getDataSource())

  def compId = column[Long]("COMP_ID", O.PrimaryKey, O.AutoInc) // This is the primary key column
  def name = column[String]("COMP_NAME")
  def city = column[String]("CITY")
  def orders = column[Int]("ORDERS")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = compId.? ~ name ~ city ~ orders <> (Company.apply _, Company.unapply _)
  def autoInc = compId.? ~ name ~ city ~ orders <> (Company, Company.unapply _) returning compId

  def findAll() = for (s <- Companies) yield s


  def options = this.findAll.map(x => x.compId -> x.name)

   def create(company: Company)(implicit s: Session) = database withTransaction {
    Companies.autoInc.insert(company)
  } 

   def findByPk(pk: Long) =
    for (u <- Companies if u.compId === pk) yield u
}


