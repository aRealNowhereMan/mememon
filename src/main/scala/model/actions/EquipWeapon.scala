package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.items.weapons.Weapon

/** Describes equipping one of the available weapons. */
class EquipWeapon(items: List[Weapon]) extends ItemAction[Weapon]("Equip weapon", items)
