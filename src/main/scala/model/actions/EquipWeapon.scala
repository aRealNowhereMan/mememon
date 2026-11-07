package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.items.weapons.Weapon
import cl.uchile.dcc.model.units.GameUnit

/** Describes and executes equipping one of the available weapons. */
class EquipWeapon(items: List[Weapon]) extends ItemAction[Weapon]("Equip weapon", items):
  def this(weapon: Weapon) = this(List(weapon))
  def this() = this(Nil)

  override def execute(source: GameUnit, target: Target): Unit =
    source.executeEquipWeapon(this, target)
