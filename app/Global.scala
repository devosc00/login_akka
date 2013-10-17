import play.api.GlobalSettings

import models._
import play.api.db.DB
import play.api.Application
import play.api.Play.current

import scala.slick.driver.H2Driver.simple._
// import scala.slick.driver.MySQLDriver.simple._
import Database.threadLocalSession

object Global extends GlobalSettings {

  override def onStart(app: Application) {

    lazy val database = Database.forDataSource(DB.getDataSource())

    database withSession {
      // Create the tables, including primary and foreign keys
      val ddl = (Companies.ddl ++ Users.ddl ++ Account.ddl)

      //ddl.drop
      ddl.create

      // Insert some Companies
      Companies.insertAll(
        Company(Some(101), "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
        Company(Some(49), "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
        Company(Some(150), "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966"))

      // Insert some coffees (using JDBC's batch insert feature, if supported by the DB)
      Users.insertAll(
        User(Some("Colombian"), 101, "pilarz", 0, 0),
        User(Some("French_Roast"), 49, "tokarz", 0, 0),
        User(Some("Espresso"), 150, "frezer", 0, 0),
        User(Some("Colombian_Decaf"), 101, "tokarz", 0, 0),
        User(Some("French_Roast_Decaf"), 49, "frezer", 0, 0))

      Account.insertAll(
        Account(Some(1), "alice@example.com", "secret", "Alice", Administrator),
        Account(Some(2), "bob@example.com", "secret", "Bob", NormalUser),
        Account(Some(3), "chris@example.com", "secret", "Chris", NormalUser))
    }
  }
}