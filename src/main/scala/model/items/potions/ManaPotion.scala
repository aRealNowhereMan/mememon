package cl.uchile.dcc.model.items.potions

import cl.uchile.dcc.model.units.Character

/** Restores 30% of a magical character's maximum mana. */
class ManaPotion(name: String = "Mana potion") extends Potion(name):
  override def applyEffect(character: Character): Unit = character.consumeManaPotion(this)
