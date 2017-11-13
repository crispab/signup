package se.crisp.signup4.services

import javax.inject.Inject
import javax.inject.Singleton

import cloudinary.model.CloudinaryResourceBuilder
import com.cloudinary.{Cloudinary, Transformation}
import org.apache.commons.codec.digest.DigestUtils
import play.api.Configuration
import se.crisp.signup4.models.User


trait ImageProvider {
  def url(user: User, size: Int): String
  def identifier: String
}


@Singleton
class GravatarUrl extends ImageProvider {
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

  override def identifier: String = GravatarUrl.identifier
}

object GravatarUrl {
  def identifier = "Gravatar"
}


@Singleton
class CloudinaryUrl @Inject() (val cloudinaryResourceBuilder: CloudinaryResourceBuilder,
                               val configuration: Configuration) extends ImageProvider {
  lazy val CLOUDINARY_FOLDER: String = configuration.getString("cloudinary.folder").getOrElse("signup")
  lazy val cloudinary: Cloudinary = cloudinaryResourceBuilder.cld


  override def url(user: User, size: Int): String = {
    cloudinary.url().secure(secureValue = true).transformation(Transformation().w_(size).h_(size).c_("thumb").g_("face")).version(user.imageVersion).generate(publicId(user))
  }

  def publicId(user: User): String = {
    CLOUDINARY_FOLDER + "/" + user.id.get.toString
  }

  override def identifier: String = CloudinaryUrl.identifier

}

object CloudinaryUrl {
  def identifier = "Cloudinary"
}



class ImageUrl @Inject() (gravatarUrl: GravatarUrl, cloudinaryUrl: CloudinaryUrl) {
  def apply(user: User, size: Int): String = {
    provider(user.imageProvider).url(user, size)
  }

  private def provider(providerName: String): ImageProvider = {
    if(providerName.toLowerCase.contains(cloudinaryUrl.identifier.toLowerCase)) {
      cloudinaryUrl
    } else {
      gravatarUrl
    }
  }
}

