package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.DB
import play.api.Play.current
import play.api.data.format.Formats._ 

import jp.t2v.lab.play2.auth._

import views._
import models._

import play.api.db.slick.Config.driver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

object UsersController extends Controller with AuthElement with AuthConfigImpl {

  lazy val database = Database.forDataSource(DB.getDataSource())

  val pageSize = 5


  val Home = Redirect(routes.UsersController.list(0, 2, ""))


  val accForm = Form[Account](
    mapping(
      "accId" -> optional(longNumber),
      "email" -> nonEmptyText,
      "password" -> tuple(
          "main" -> text(minLength = 6),
          "confirm" -> text
      ).verifying(
        "Hasła są różne", passwords => passwords._1 == passwords._2 ),
      "name" -> nonEmptyText,
      "compID" -> ignored(49L),
      "position" -> nonEmptyText,
      "permission" -> text
      )((accId, email, password, name, compID, position, permission) => 
         Account(accId, email, password._1, name, compID, position, Permission.valueOf(permission)))
       ((a: Account) => Some((a.accId, a.email, (a.password,""), a.name, a.compID, a.position, a.permission.toString)))
        )

  val upForm = Form[Account](
    mapping(
      "accId" -> optional(longNumber),
      "email" -> text,
      "password" -> ignored("": String),
      "name" -> text,
      "compID" -> ignored(49L),
      "position" -> text,
      "permission" -> text
      )((accId, email, password, name, compID, position, permission) => 
         Account(accId, email, password, name, compID, position, Permission.valueOf(permission)))
       ((a: Account) => Some((a.accId, a.email, a.password, a.name, a.compID, a.position, a.permission.toString)))
        )

  def index = Action { Home }


  def list(page: Int, orderBy: Int, filter: String) = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    database withSession {
      Ok(html.users.list(
        Page(Accounts.list(page, pageSize, orderBy, filter = ("%" + filter + "%")).list,
          page,
          offset = pageSize * page,
          Accounts.findAll(filter).list.size),
        orderBy,
        filter))
    }
  }
 

  def create = StackAction(AuthorityKey -> Administrator) { implicit request =>
        database withSession { 
          Ok(html.users.createForm(accForm))
        }
  }


  def save = StackAction(AuthorityKey -> Administrator) { implicit request =>
      accForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.users.createForm(formWithErrors)),
      entity => {
        database withTransaction {
          Accounts.create(entity)
          Home.flashing("success" -> s"User ${entity.name} został dodany")
        }
      })
  }

 
  def edit(pk: Long) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database withSession {
 Accounts.findByPk(pk).list.headOption match {
        case Some(e) => Ok(html.users.editForm(pk, upForm.fill(e)))
        case None => NotFound
      }
    }
  }


  def update(pk: Long) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database withSession {
      println("update form filled")
      upForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.users.editForm(pk, formWithErrors)),
        entity => {
          Accounts.update(pk, entity)
          Home.flashing("success" -> s"Użytkownik ${entity.name} został uaktualniony")
        })
    }
  }


  def delete(pk: Long) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database withSession {
      Home.flashing(Accounts.findByPk(pk).delete match {  
        case 0 => "failure" -> "Nie został usunięty"
        case x => "success" -> s"Został usunięty (deleted $x row(s))"
      })
    }
  }
}