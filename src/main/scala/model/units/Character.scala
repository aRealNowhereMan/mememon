package cl.uchile.dcc.model.units

import cl.uchile.dcc.model.items.Usable
import cl.uchile.dcc.model.items.weapons.Weapon

/** A character with an optional weapon and a personal inventory. */
abstract class Character(name: String, hitPoints: Int, defense: Int, weight: Int)
    extends GameUnit(name, hitPoints, defense, weight):
  private var storedWeapon: Option[Weapon] = None
  private var storedInventory: List[Usable] = List.empty

  /** The weapon currently held in the weapon slot. */
  def weapon: Option[Weapon] = storedWeapon

  /** Stores a weapon slot value; equipment rules belong to a later delivery. */
  def weapon_=(value: Option[Weapon]): Unit = storedWeapon = value

  /** An immutable snapshot of the inventory. */
  def inventory: List[Usable] = storedInventory

  /** Adds an item to the end of the inventory. */
  def addItem(item: Usable): Unit = storedInventory = storedInventory :+ item

  /** Removes an item from the inventory, if present. */
  def removeItem(item: Usable): Unit =
    storedInventory = storedInventory.filterNot(_ == item)

  /** Character weight plus half of the equipped weapon's weight. */
  override def actionBarMaximum: Double =
    weight + weapon.map(_.weight / 2.0).getOrElse(0.0)
