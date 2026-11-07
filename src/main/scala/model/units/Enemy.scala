package cl.uchile.dcc.model.units

import cl.uchile.dcc.model.actions.{Action, Attack, Move}
import cl.uchile.dcc.model.exceptions.{DefeatedTargetException, InvalidTargetException}

/** An enemy with innate attack capability and target behavior. */
class Enemy(name: String, hitPoints: Int, val attack: Int, defense: Int, weight: Int)
    extends GameUnit(name, hitPoints, defense, weight):
  override def actions: List[Action] = List(new Attack(), new Move())

  override def calculatePhysicalAttackDamage(): Int = attack

  override def receiveHealing(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Healing can only be received by characters.")

  override def receivePurification(caster: MagicCharacter, magicDamage: Int): Unit =
    if isDefeated then
      throw new DefeatedTargetException(s"Defeated enemy '$name' cannot receive actions.")
    val damage = math.max(1, (hitPoints * 0.15).toInt) + (magicDamage / 2)
    hitPoints = math.max(0, hitPoints - damage)
