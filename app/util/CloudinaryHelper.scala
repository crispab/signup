package util

import models.User

object CloudinaryHelper {

  import play.api.Play.current
  val CLOUDINARY_FOLDER = play.api.Play.configuration.getString("cloudinary.folder").getOrElse("signup")

  def publicId(user: User) = {
    user.id.get.toString
  }

  def parametrizedUrl(uploadUrl: String) = {
    uploadUrl.replaceAll("\\/image\\/upload\\/", "/image/upload/c_thumb,g_face,w_{size},h_{size}/")
  }

}
