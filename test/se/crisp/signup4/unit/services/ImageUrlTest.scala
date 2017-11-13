package se.crisp.signup4.unit.services


import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._

import org.scalatestplus.play._
import se.crisp.signup4.models.User
import se.crisp.signup4.services.{CloudinaryUrl, GravatarUrl, ImageUrl}

class ImageUrlTest extends PlaySpec with MockitoSugar {

  val gravatarUrl = new GravatarUrl
  val cloudinaryUrl = mock[CloudinaryUrl]
  when(cloudinaryUrl.identifier) thenReturn "Cloudinary"

  val imageUrl = new ImageUrl(gravatarUrl, cloudinaryUrl)

  def CORRECT_URL = "https://secure.gravatar.com/avatar/0bc83cb571cd1c50ba6f3e8a78ef1346.jpg?default=blank&size=40"

  "ImageUrl instance " must {

    "handle simple case" in {
      val user = User(firstName = "Hari", lastName = "Seldon", email = "myemailaddress@example.com", imageProvider = GravatarUrl.identifier)
      imageUrl(user, 40) must equal(CORRECT_URL)
    }

    "handle CamelCase" in {
      val user = User(firstName = "Hari", lastName = "Seldon", email = "MyEmailAddress@example.com", imageProvider = GravatarUrl.identifier)
      imageUrl(user, 40) must equal(CORRECT_URL)
    }

    "handle white space" in {
      val user = User(firstName = "Hari", lastName = "Seldon", email = "  MyEmailAddress@example.com ", imageProvider = GravatarUrl.identifier)
      imageUrl(user, 40) must equal(CORRECT_URL)
    }

    "handle empty string" in {
      val user = User(firstName = "Hari", lastName = "Seldon", email = "", imageProvider = GravatarUrl.identifier)
      imageUrl(user, 40).length must be > 0
    }
  }
}

