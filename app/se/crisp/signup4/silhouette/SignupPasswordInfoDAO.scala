package se.crisp.signup4.silhouette

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.api.util.PasswordInfo
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import play.api.Configuration
import se.crisp.signup4.models.dao.UserDAO

import scala.concurrent.{ExecutionContext, Future}

class SignupPasswordInfoDAO @Inject()(val userDao: UserDAO, val configuration: Configuration)(implicit ec: ExecutionContext) extends DelegableAuthInfoDAO[PasswordInfo] {
  private lazy val salt = Option(configuration.get[String]("password.salt"))

  override def find(loginInfo: LoginInfo): Future[Option[PasswordInfo]] = Future {
    userDao.findByEmail(loginInfo.providerKey).map(user => PasswordInfo("md5", user.password, salt))
  }

  override def add(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {
    authInfo
  }

  override def update(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {
    authInfo
  }

  override def save(loginInfo: LoginInfo, authInfo: PasswordInfo): Future[PasswordInfo] = Future {
    authInfo
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = Future {}
}
