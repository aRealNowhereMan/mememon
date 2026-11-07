package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.units.MagicCharacter

/** Describes and executes the single-target thunder spell. */
class Thunder extends BlackSpell("Thunder", 20):
  override def applySpellEffect(caster: MagicCharacter, target: Target, magicDamage: Int): Unit =
    target.receiveThunder(caster, magicDamage)
