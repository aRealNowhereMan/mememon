package cl.uchile.dcc.model.items.weapons

import cl.uchile.dcc.model.units.Character

/** A magical staff weapon. */
class Staff(name: String, attack: Int, weight: Int, magicAttack: Int)
    extends MagicWeapon(name, attack, weight, magicAttack):
  override def equipTo(character: Character): Unit = character.equipStaff(this)
