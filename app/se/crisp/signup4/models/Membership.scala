package se.crisp.signup4.models

case class Membership(id: Option[Long] = None,
                      group: Group,
                      user: User) extends Ordered[Membership] {
  def compare(that: Membership): Int = {
    this.user.compare(that.user)
  }
}
