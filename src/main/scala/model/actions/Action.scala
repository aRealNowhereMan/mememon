package cl.uchile.dcc.model.actions

/** Describes an action, without executing it or storing sources or targets. */
trait Action:
  /** Display name of the action. */
  def name: String
