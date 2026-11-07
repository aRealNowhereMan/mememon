package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.units.MagicCharacter

/** Describes and executes the area-of-effect meteor spell on a panel. */
class Meteor extends BlackSpell("Meteor", 50):
  override def applySpellEffect(caster: MagicCharacter, target: Target, magicDamage: Int): Unit =
    target.receiveMeteor(caster, magicDamage)
