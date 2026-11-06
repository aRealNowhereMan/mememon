package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.units.MagicCharacter

/** A named magical spell with a mana cost and polymorphic effect. */
abstract class Spell(val name: String, val manaCost: Int) extends Action:
  /** Applies this spell's specific effect onto the target entity. */
  def applySpellEffect(caster: MagicCharacter, target: Target, magicDamage: Int): Unit
