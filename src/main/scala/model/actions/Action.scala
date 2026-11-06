package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.units.GameUnit

/** Describes an action and defines its execution via dynamic dispatch. */
trait Action:
  /** Display name of the action. */
  def name: String

  /** Executes this action from a source unit to a target entity. */
  def execute(source: GameUnit, target: Target): Unit
