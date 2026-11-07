package cl.uchile.dcc.model.items.potions

import cl.uchile.dcc.model.units.Character

/** Increases the magic damage of the next spell cast by 50%. */
class MagicStrengthPotion(name: String = "Magic strength potion") extends Potion(name):
  override def applyEffect(character: Character): Unit = character.consumeMagicStrengthPotion(this)
