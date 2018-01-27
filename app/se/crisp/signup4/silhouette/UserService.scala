package se.crisp.signup4.silhouette

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import se.crisp.signup4.models.User

import scala.concurrent.Future

trait UserService extends IdentityService[User]{
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]]
}
