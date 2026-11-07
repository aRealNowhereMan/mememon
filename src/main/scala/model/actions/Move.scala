package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.units.GameUnit

/** Describes and executes movement to an adjacent panel. */
class Move extends Action:
  val name: String = "Move"

  override def execute(source: GameUnit, target: Target): Unit =
    target.receiveMove(source)
