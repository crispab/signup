package services

import cloudinary.plugin.CloudinaryPlugin
import com.cloudinary.Transformation
import com.typesafe.plugin._
import models.User
import org.apache.commons.codec.digest.DigestUtils


trait ImageUrl {
  def url(user: User, size: Int = 40): String
  def identifier: String
}



object GravatarUrl extends ImageUrl {
  lazy val GRAVATAR_BASE_URL = "https://secure.gravatar.com/avatar/"
  lazy val  GRAVATAR_DEFAULT_SIZE = 80
  lazy val  GRAVATAR_NOT_FOUND_IMAGE_TYPE = "?default=blank"

  def sizeParam(size: Int): String = {
    if (size == GRAVATAR_DEFAULT_SIZE) {
      ""
    } else {
      "&size=" + size
    }
  }

  override def url(user: User, size: Int): String = {
    GRAVATAR_BASE_URL + DigestUtils.md5Hex(user.email.trim.toLowerCase) + ".jpg" + GRAVATAR_NOT_FOUND_IMAGE_TYPE + sizeParam(size)
  }

  override def identifier = "Gravatar"
}



object CloudinaryUrl extends ImageUrl {
  import play.api.Play.current
  lazy val CLOUDINARY_FOLDER = play.api.Play.configuration.getString("cloudinary.folder").getOrElse("signup")
  lazy val cloudinary = use[CloudinaryPlugin].cloudinary

  override def url(user: User, size: Int): String = {
    cloudinary.url().secure(secureValue = true).transformation(Transformation().w_(size).h_(size).c_("thumb").g_("face")).version(user.imageVersion).generate(publicId(user))
  }

  def publicId(user: User) = {
    CLOUDINARY_FOLDER + "/" + user.id.get.toString
  }

  override def identifier = "Cloudinary"

}



object ImageUrl {
  def apply(user: User, size: Int = 40): String = {
    provider(user.imageProvider).url(user, size)
  }

  private def provider(providerName: String): ImageUrl = {
    if(providerName.toLowerCase.contains(CloudinaryUrl.identifier.toLowerCase)) {
      CloudinaryUrl
    } else {
      GravatarUrl
    }
  }
}

