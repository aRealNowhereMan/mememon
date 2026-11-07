package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.units.GameUnit

/** Describes and executes a physical attack. */
class Attack extends Action:
  val name: String = "Attack"

  override def execute(source: GameUnit, target: Target): Unit =
    val attackPower = source.calculatePhysicalAttackDamage()
    target.receiveAttack(source, attackPower)
