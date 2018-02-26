package se.crisp.signup4.services

import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.exceptions.IdentityNotFoundException
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import play.api.Logger
import se.crisp.signup4.models.User
import se.crisp.signup4.models.dao.UserDAO
import se.crisp.signup4.silhouette.UserService

import scala.concurrent.Future

@Singleton
class UserServiceImpl @Inject()(val userDAO: UserDAO) extends UserService {
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]] = Future.successful(loginInfo.providerID match {
    case "credentials" => userDAO.findByEmail(loginInfo.providerKey)
    case _ => userDAO.findByProviderKey(loginInfo.providerKey)
  })

  def save(profile: CommonSocialProfile): Future[User] = {
    val emailAddress = profile.email.getOrElse("")
    userDAO.findByEmail(emailAddress) match {
      case Some(user) =>
        userDAO.updateProviderKey(profile.email.getOrElse(""), profile.loginInfo.providerKey)
        Future.successful(user)
      case None =>
        Logger.info("Could not find user with email: " + emailAddress)
        Future.failed(new IdentityNotFoundException(profile.email.getOrElse("")))
    }
  }
}
