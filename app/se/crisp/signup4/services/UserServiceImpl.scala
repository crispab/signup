package se.crisp.signup4.services

import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.LoginInfo
import se.crisp.signup4.models.User
import se.crisp.signup4.models.dao.UserDAO
import se.crisp.signup4.silhouette.UserService

import scala.concurrent.Future

@Singleton
class UserServiceImpl @Inject()(val userDAO: UserDAO) extends UserService {
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = Future.successful(userDAO.findByEmail(loginInfo.providerKey))
}
