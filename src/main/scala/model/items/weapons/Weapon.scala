package cl.uchile.dcc.model.items.weapons

import cl.uchile.dcc.model.items.Usable
import cl.uchile.dcc.model.units.Character

/** Shared weapon statistics and an optional owner. */
abstract class Weapon(val name: String, val attack: Int, val weight: Int) extends Usable:
  private var storedOwner: Option[Character] = None

  /** Character that owns this weapon, if any. */
  def owner: Option[Character] = storedOwner

  /** Stores ownership; this does not execute an equip action. */
  def owner_=(value: Option[Character]): Unit = storedOwner = value
