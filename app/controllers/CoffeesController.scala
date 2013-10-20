package controllers

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db.DB
import play.api.Play.current

import jp.t2v.lab.play2.auth._

import views._
import models._

// Use H2Driver to connect to an H2 database
import scala.slick.driver.H2Driver.simple._

// Use the implicit threadLocalSession
import Database.threadLocalSession

object CoffeesController extends Controller with AuthElement with AuthConfigImpl {

  lazy val database = Database.forDataSource(DB.getDataSource())

  val pageSize = 3

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.CoffeesController.list(0, 2, ""))

/*  val supplierSelect = database withSession {
    Companies.options.list.map(item => (item._1.toString, item._2))
  }*/

  /**
   * Describe the Userused in both edit and create screens).
   */
  val userForm = Form(
    mapping(
      //"id" -> optional(number),
      "name" -> optional(nonEmptyText),
      "accID" -> number,
      "compID" -> number,
      "position" -> text,
      "doneParts" -> number,
      "setup" -> number)
    (User.apply)(User.unapply)
      )

  val accForm = Form(
    tuple(
    "accId" -> optional(number),
    "email" -> text,
    "password" -> text
    )
  )

  // -- Users

  /**
   * Handle default path requests, redirect to entities list
   */
  def index = Action { Home }

  /**
   * Display the paginated list.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on entity names
   */
  def list(page: Int, orderBy: Int, filter: String = "%") = StackAction(AuthorityKey -> NormalUser) { implicit request =>
    database withSession {
      Ok(html.coffees.list(
        Page(Users.list(page, pageSize, orderBy, filter).list,
          page,
          offset = pageSize * page,
          Users.findAll(filter).list.size),
        orderBy,
        filter))
    }
  }
  /**
   * Display the 'new userForm'.
   */
  def create = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database withSession {
      Ok(html.coffees.createForm(userForm, accForm))
    }
  }

  /**
   * Handle the 'new userForm' submission.
   */
  def save = StackAction(AuthorityKey -> Administrator) { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.coffees.createForm(formWithErrors)),
      entity => {
        database withTransaction {
          Users.insert(entity)
          Home.flashing("success" -> s"Entity ${entity.name} has been created")
        }
      })
  }

  /**
   * Display the 'edit userForm' of an existing entity.
   *
   * @param id Id of the entity to edit
   */
  def edit(pk: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database withSession {
      Users.findByPK(pk).list.headOption match {
        case Some(e) => Ok(html.coffees.editForm(pk, userForm.fill(e)))
        case None => NotFound
      }
    }
  }

  /**
   * Handle the 'edit userForm' submission
   *
   * @param id Id of the entity to edit
   */
  def update(pk: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database withSession {
      userForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.coffees.editForm(pk, formWithErrors)),
        entity => {
          Home.flashing((Users.findByPK(pk).update(entity)) match {
            case 0 => "failure" -> s"Could not update entity ${entity.name}"
            case _ => "success" -> s"Entity ${entity.name} has been updated"
          })
        })
    }
  }

  /**
   * Handle entity deletion.
   */
  def delete(pk: String) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database withSession {
      Home.flashing(Users.findByPK(pk).delete match {
        case 0 => "failure" -> "Entity has Not been deleted"
        case x => "success" -> s"Entity has been deleted (deleted $x row(s))"
      })
    }
  }
}