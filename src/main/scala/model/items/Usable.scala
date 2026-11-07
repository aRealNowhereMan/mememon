package cl.uchile.dcc.model.items

import cl.uchile.dcc.model.items.potions.Potion
import cl.uchile.dcc.model.items.weapons.Weapon

/** An item that can be stored in a character's inventory. */
trait Usable:
  /** Display name of this item. */
  def name: String

  /** Casts to Weapon if this usable is a weapon, without using isInstanceOf. */
  def asWeapon: Option[Weapon]

  /** Casts to Potion if this usable is a potion, without using isInstanceOf. */
  def asPotion: Option[Potion]
