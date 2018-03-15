package se.crisp.signup4.silhouette

import javax.inject.Inject

import com.mohiva.play.silhouette.api.LoginInfo
import com.mohiva.play.silhouette.impl.providers.OAuth2Info
import com.mohiva.play.silhouette.persistence.daos.DelegableAuthInfoDAO
import play.api.Configuration
import play.api.libs.json.Json.{stringify, toJson}
import play.api.libs.json._
import se.crisp.signup4.models.dao.UserDAO

import scala.concurrent.{ExecutionContext, Future}

class SignupOAuth2InfoDAO @Inject()(val userDao: UserDAO, val configuration: Configuration, implicit val ec: ExecutionContext) extends DelegableAuthInfoDAO[OAuth2Info] {
  implicit val oauth2InfoReads: Reads[OAuth2Info] = Json.reads[OAuth2Info]
  implicit val oauth2InfoWrites: Writes[OAuth2Info] = Json.writes[OAuth2Info]

  override def find(loginInfo: LoginInfo): Future[Option[OAuth2Info]] = Future {
    val maybeMaybeString: Option[Option[String]] = userDao.findByProviderKey(loginInfo.providerKey).map(user => user.authInfo)
    maybeMaybeString.flatMap(json => Json.fromJson[OAuth2Info](Json.parse(json.get)) match {
      case s: JsSuccess[OAuth2Info] => Option(s.get)
      case _: JsError => None
      })
  }

  override def add(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = Future {
    userDao.updateAuthInfo(loginInfo.providerKey,stringify(toJson(authInfo)))
    authInfo
  }

  override def update(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = Future {
    userDao.updateAuthInfo(loginInfo.providerKey,stringify(toJson(authInfo)))
    authInfo
  }

  override def remove(loginInfo: LoginInfo): Future[Unit] = Future {}

  override def save(loginInfo: LoginInfo, authInfo: OAuth2Info): Future[OAuth2Info] = Future {
    userDao.updateAuthInfo(loginInfo.providerKey,stringify(toJson(authInfo)))
    authInfo
  }
}
