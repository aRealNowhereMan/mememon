package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.units.MagicCharacter

/** Describes and executes the white magic healing spell on a character. */
class Healing extends WhiteSpell("Healing", 15):
  override def applySpellEffect(caster: MagicCharacter, target: Target, magicDamage: Int): Unit =
    target.receiveHealing(caster, magicDamage)
