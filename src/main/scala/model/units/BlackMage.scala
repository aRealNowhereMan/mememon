package cl.uchile.dcc.model.units

import cl.uchile.dcc.model.actions.{Action, BlackSpell, Meteor, Target, Thunder}
import cl.uchile.dcc.model.items.weapons.{Dagger, Staff, Wand}

/** A black mage character specialized in black magic offensive spells. */
class BlackMage(name: String, hitPoints: Int, defense: Int, weight: Int, maxMana: Int)
    extends MagicCharacter(name, hitPoints, defense, weight, maxMana):
  override def actions: List[Action] =
    super.actions ++ List(new Thunder(), new Meteor())

  override def equipDagger(dagger: Dagger): Unit = doEquip(dagger)
  override def equipWand(wand: Wand): Unit = doEquip(wand)
  override def equipStaff(staff: Staff): Unit = doEquip(staff)

  override def castBlackSpell(spell: BlackSpell, target: Target): Unit =
    castSpell(spell, target)
