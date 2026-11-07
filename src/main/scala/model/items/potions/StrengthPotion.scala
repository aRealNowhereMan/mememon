package cl.uchile.dcc.model.items.potions

import cl.uchile.dcc.model.units.Character

/** Increases the consuming character's defense by 15%. */
class StrengthPotion(name: String = "Strength potion") extends Potion(name):
  override def applyEffect(character: Character): Unit = character.consumeStrengthPotion(this)
