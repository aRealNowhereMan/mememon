package cl.uchile.dcc.model.items.weapons

import cl.uchile.dcc.model.units.Character

/** A dagger weapon. */
class Dagger(name: String, attack: Int, weight: Int) extends Weapon(name, attack, weight):
  override def equipTo(character: Character): Unit = character.equipDagger(this)
