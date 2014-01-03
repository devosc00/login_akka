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
/*  val userForm = Form(
    mapping(
      //"id" -> optional(number),
      "name" -> optional(nonEmptyText),
      "accID" -> optional(of[Long]),
      "compID" -> optional(of[Long]),
      "position" -> text,
      "doneParts" -> number,
      "setup" -> number
    )(User.apply)(User.unapply)
      )
*/
  val accForm = Form[Account](
    mapping(
      "accId" -> optional(longNumber),
      "email" -> text,
      "password" -> text,
      "name" -> text,
      "compID" -> ignored(49L),
      "position" -> text,
      "permission" -> text
      )((accId, email, password, name, compID, position, _) => 
      new Account(accId, email, password, name, compID, position, NormalUser))
       ((a: Account) => Some((a.accId, a.email, a.password, a.name, a.compID, a.position, a.permission.toString)))
        )


  def index = Action { Home }

  /**
   * Display the paginated list.
   *
   * @param page Current page number (starts from 0)
   * @param orderBy Column to be sorted
   * @param filter Filter applied on entity names
   */
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
  /**
   * Display the 'new userForm'.
   */
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

  /**
   * Display the 'edit userForm' of an existing entity.
   *
   * @param id Id of the entity to edit
   */
  def edit(pk: Long) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database withSession {
 Accounts.findByPk(pk).list.headOption match {
        case Some(e) => Ok(html.users.editForm(pk, accForm.fill(e)))
        case None => NotFound
      }
    }
  }

  /**
   * Handle the 'edit userForm' submission
   *
   * @param id Id of the entity to edit
   */
  def update(pk: Long) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database withSession {
      accForm.bindFromRequest.fold(
        formWithErrors => BadRequest(html.users.editForm(pk, formWithErrors)),
        entity => {
          Accounts.update(pk, entity)
          Home.flashing("success" -> s"Użytkownik ${entity.name} został uaktualniony")
        })
    }
  }


  /**
   * Handle entity deletion.
   */
  def delete(pk: Long) = StackAction(AuthorityKey -> Administrator) { implicit request =>
    database withSession {
      Home.flashing(Accounts.findByPk(pk).delete match {  
        case 0 => "failure" -> "Nie został usunięty"
        case x => "success" -> s"Został usunięty (deleted $x row(s))"
      })
    }
  }
}