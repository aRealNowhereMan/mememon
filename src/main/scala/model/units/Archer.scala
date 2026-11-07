package cl.uchile.dcc.model.units

import cl.uchile.dcc.model.items.weapons.{Bow, Sword, Wand}

/** An archer character capable of equipping swords, bows and wands. */
class Archer(name: String, hitPoints: Int, defense: Int, weight: Int)
    extends Character(name, hitPoints, defense, weight):
  override def equipSword(sword: Sword): Unit = doEquip(sword)
  override def equipBow(bow: Bow): Unit = doEquip(bow)
  override def equipWand(wand: Wand): Unit = doEquip(wand)
