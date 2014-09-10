package io.abacus.soroban.elements

sealed trait Label

case object Person extends Label
case class Location(specific:String) extends Label
case object Organization extends Label
case object Money extends Label
case object Date extends Label



object Label {
  def stringToLabel(label:String):Option[Label] = {
    label match {
      case "PERSON" => Some(Person)
      case "ORGANIZATION" => Some(Organization)
      case "LOCATION" => Some(Location("generic"))
      case "MONEY" => Some(Money)
      case "DATE" => Some(Date)
      case _ => None
    }
  }
}