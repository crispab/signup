package se.crisp.signup4.models

case class Group(
                  id: Option[Long] = None,
                  name: String,
                  description: String = "",
                  mailFrom: String = "",
                  mailSubjectPrefix: String = ""
                  )
