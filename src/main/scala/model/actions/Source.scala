package cl.uchile.dcc.model.actions

/** An entity capable of performing actions on targets. */
trait Source:
  /** Display name of the source. */
  def name: String

  /** Available actions this source can perform. */
  def actions: List[Action]

  /** Finds an action by name, if available. */
  def findAction(actionName: String): Option[Action]

  /** Executes an action on a target entity. */
  def doAction(action: Action, target: Target): Unit
