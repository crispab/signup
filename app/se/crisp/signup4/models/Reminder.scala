package se.crisp.signup4.models

import java.util.Date


case class Reminder(id: Option[Long] = None,
                         date: Date,
                         event: Event)
