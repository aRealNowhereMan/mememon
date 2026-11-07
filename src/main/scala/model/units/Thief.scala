package cl.uchile.dcc.model.units

import cl.uchile.dcc.model.items.weapons.{Dagger, Sword}

/** A thief character capable of equipping swords and daggers. */
class Thief(name: String, hitPoints: Int, defense: Int, weight: Int)
    extends Character(name, hitPoints, defense, weight):
  override def equipSword(sword: Sword): Unit = doEquip(sword)
  override def equipDagger(dagger: Dagger): Unit = doEquip(dagger)
