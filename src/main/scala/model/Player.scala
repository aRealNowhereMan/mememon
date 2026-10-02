package cl.uchile.dcc.model

import cl.uchile.dcc.model.units.GameUnit

/** A player controlling a fixed list of characters and/or enemies. */
class Player(val units: List[GameUnit]):
  /** A player is defeated when no living units remain, including an empty team. */
  def isDefeated: Boolean = units.forall(_.isDefeated)
