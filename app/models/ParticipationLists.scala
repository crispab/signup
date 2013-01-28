package models

case class ParticipationLists(event: Event,
                              on: Seq[Participation],
                              maybe: Seq[Participation],
                              off: Seq[Participation],
                              unregistered: Seq[Participation]) {
  def isEmpty = on.isEmpty && maybe.isEmpty && off.isEmpty && unregistered.isEmpty
}
