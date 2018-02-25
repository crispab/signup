package se.crisp.signup4.silhouette

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.services.IdentityService
import com.mohiva.play.silhouette.impl.providers.CommonSocialProfile
import se.crisp.signup4.models.User

import scala.concurrent.Future

trait UserService extends IdentityService[User]{
  override def retrieve(loginInfo: LoginInfo): Future[Option[User]]

  /**
    * Saves the social profile for a user.
    *
    * If a user exists for this profile then update the user.
    *
    * @param profile The social profile to save.
    * @return The user for whom the profile was saved.
    */
  def save(profile: CommonSocialProfile): Future[User]
}
