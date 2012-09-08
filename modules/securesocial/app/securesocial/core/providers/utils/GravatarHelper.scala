/**
 * Copyright 2012 Jorge Aliss (jaliss at gmail dot com) - twitter: @jaliss
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package securesocial.core.providers.utils

import java.security.MessageDigest
import play.api.libs.ws.WS

object GravatarHelper {
  val GravatarUrl = "http://www.gravatar.com/avatar/%s?d=404"
  val Md5 = "MD5"

  def avatarFor(email: String): Option[String] = {
    hash(email).map( hash => {
      val url = GravatarUrl.format(hash)
      WS.url(url).get().await(10000).fold(
        onError => None,
        onSucess => if (onSucess.status == 200) Some(url) else None
      )
    }).getOrElse(None)
  }

  private def hash(email: String): Option[String] = {
    val s = email.trim.toLowerCase
    if ( s.length > 0 ) {
      val out = MessageDigest.getInstance(Md5).digest(s.getBytes)
      Some(BigInt(1, out).toString(16))
    } else {
      None
    }
  }
}