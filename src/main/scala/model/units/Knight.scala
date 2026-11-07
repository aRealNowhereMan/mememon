package cl.uchile.dcc.model.units

import cl.uchile.dcc.model.items.weapons.{Bow, Dagger, Sword}

/** A knight character capable of equipping swords, daggers and bows. */
class Knight(name: String, hitPoints: Int, defense: Int, weight: Int)
    extends Character(name, hitPoints, defense, weight):
  override def equipSword(sword: Sword): Unit = doEquip(sword)
  override def equipDagger(dagger: Dagger): Unit = doEquip(dagger)
  override def equipBow(bow: Bow): Unit = doEquip(bow)
