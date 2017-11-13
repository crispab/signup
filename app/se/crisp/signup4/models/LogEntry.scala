package se.crisp.signup4.models

import java.util.Date


case class LogEntry(
  id: Option[Long] = None,
  event: Event,
  message: String,
  when: Date = new Date()
)
