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

object UsersController extends Controller with AuthElement with AuthConfigImpl {

  lazy val database = Database.forDataSource(DB.getDataSource())

  val pageSize = 3

  /**
   * This result directly redirect to the application home.
   */
  val Home = Redirect(routes.UsersController.list(0, 2, ""))

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
      "setup" -> number
    )(User.apply)(User.unapply)
      )

  val accForm = Form(
    mapping(
      "accId" -> optional(number),
      "email" -> text,
      "password" -> text
    )((accId, email, password) => Account(accId, email, password, NormalUser))
    ((account: Account) => Some(account.accId, account.email, account.password))
      )
  // -- Users

  val addUserForm = Form(
    tuple(
      "name" -> optional(nonEmptyText),
      "email" -> text,
      "password" -> text,
      "position" -> text,
      "division" -> text
      )
    )

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
      Ok(html.users.list(
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
      Ok(html.users.createForm(userForm, accForm))
    }
  }

  /**
   * Handle the 'new userForm' submission.
   */


  def saveNewOne = StackAction(AuthorityKey -> Administrator) { implicit request =>
    addUserForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.users.createForm(addUserForm)),
      {
        case ( name, email, password, position, division) => {
          val account = Account.create(Account(Some, email, password, division match {
            case "Administrator" => Administartor
            case "NormalUser" => NormalUser
            } )),
          val user = User.create(
            User(name, ))
        }
      }
      )

  def save = StackAction(AuthorityKey -> Administrator) { implicit request =>
    userForm.bindFromRequest.fold(
      formWithErrors => BadRequest(html.users.createForm(formWithErrors, accForm)),
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
        case Some(e) => Ok(html.users.editForm(pk, userForm.fill(e)))
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
        formWithErrors => BadRequest(html.users.editForm(pk, formWithErrors)),
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