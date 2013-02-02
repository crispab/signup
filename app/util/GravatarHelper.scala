package util

import org.apache.commons.codec.digest.DigestUtils

object GravatarHelper {

  def GRAVATAR_BASE_URL = "https://secure.gravatar.com/avatar/"
  def GRAVATAR_DEFAULT_SIZE = 80
  def GRAVATAR_NOT_FOUND_IMAGE_TYPE = "?default=mm"

  def sizeParam(size: Int): String = {
    if (size == GRAVATAR_DEFAULT_SIZE) {
      ""
    } else {
      "&size=" + size
    }
  }

  def gravatarUrl(email: String, size: Int = GRAVATAR_DEFAULT_SIZE) = {
    GRAVATAR_BASE_URL + DigestUtils.md5Hex(email.trim.toLowerCase) + ".jpg" + GRAVATAR_NOT_FOUND_IMAGE_TYPE + sizeParam(size)
  }
}

