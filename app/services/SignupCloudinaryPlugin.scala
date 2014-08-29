package services

import cloudinary.plugin.CloudinaryPlugin
import com.cloudinary.Cloudinary

/**
 * Initializes from cloudinary.url instead of multiple parameters. Doesn't automatically download javascrips and such in dev mode.
 */
class SignupCloudinaryPlugin(app: play.api.Application) extends CloudinaryPlugin(app) {
  override lazy val cloudinary
    = new Cloudinary(cloudinaryUrl = app.configuration.getString("cloudinary.url").getOrElse(throw new IllegalArgumentException("Configuration must set url")))

  override lazy val enabled = {
    !app.configuration.getString("cloudinaryplugin").filter(_ == "disabled").isDefined
  }

  override def onStart() {
    cloudinary
  }
}
