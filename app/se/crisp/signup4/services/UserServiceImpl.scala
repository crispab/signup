package se.crisp.signup4.services

import javax.inject.{Inject, Singleton}

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
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

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user, otherwise create a new user with the given profile.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile): Future[User] = {

    userDAO.findByEmail(profile.email.getOrElse("")) match {
      case Some(user) =>
        userDAO.updateProviderKey(profile.email.getOrElse(""), profile.loginInfo.providerKey)
        Future.successful(user)
      case None => Future.failed(new Exception())
    }
  }
}
