package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.units.GameUnit

/** Base class for black magic offensive spells. */
abstract class BlackSpell(name: String, manaCost: Int) extends Spell(name, manaCost):
  override def execute(source: GameUnit, target: Target): Unit =
    source.castBlackSpell(this, target)
