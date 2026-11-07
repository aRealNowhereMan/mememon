package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.units.MagicCharacter

/** Describes and executes the white magic curse spell on an enemy. */
class Purification extends WhiteSpell("Purification", 25):
  override def applySpellEffect(caster: MagicCharacter, target: Target, magicDamage: Int): Unit =
    target.receivePurification(caster, magicDamage)
