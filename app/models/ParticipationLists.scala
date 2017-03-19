package models

case class ParticipationLists(event: Event,
                              on: Seq[Participation],
                              maybe: Seq[Participation],
                              off: Seq[Participation],
                              unregistered: Seq[Participation]) {
  def isEmpty: Boolean = on.isEmpty && maybe.isEmpty && off.isEmpty && unregistered.isEmpty

  def numberOn: Int = {
    on.map(_.numberOfParticipants).sum
  }

  def numberMaybe: Int = {
    maybe.map(_.numberOfParticipants).sum
  }

  def numberOff: Int = off.size

  def numberUnregistered: Int = unregistered.size
}
