package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.units.GameUnit

/** Base class for white magic spells. */
abstract class WhiteSpell(name: String, manaCost: Int) extends Spell(name, manaCost):
  override def execute(source: GameUnit, target: Target): Unit =
    source.castWhiteSpell(this, target)
