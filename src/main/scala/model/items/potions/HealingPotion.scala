package cl.uchile.dcc.model.items.potions

import cl.uchile.dcc.model.units.Character

/** Restores 20% of the consuming character's maximum hit points. */
class HealingPotion(name: String = "Healing potion") extends Potion(name):
  override def applyEffect(character: Character): Unit = character.consumeHealingPotion(this)
