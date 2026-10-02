package cl.uchile.dcc.model.units

/** A character that also stores mana points. */
abstract class MagicCharacter(
    name: String, hitPoints: Int, defense: Int, weight: Int, val maxMana: Int
) extends Character(name, hitPoints, defense, weight):
  private var currentMana: Int = maxMana

  /** Current mana points. */
  def mana: Int = currentMana

  /** Sets current mana, between zero and the initial maximum. */
  def mana_=(value: Int): Unit =
    require(value >= 0 && value <= maxMana)
    currentMana = value
