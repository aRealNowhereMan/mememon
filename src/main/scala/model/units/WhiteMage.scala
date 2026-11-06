package cl.uchile.dcc.model.units

import cl.uchile.dcc.model.actions.{Action, Healing, Purification, Target, WhiteSpell}
import cl.uchile.dcc.model.items.weapons.{Bow, Staff, Wand}

/** A white mage character specialized in white magic restorative and curse spells. */
class WhiteMage(name: String, hitPoints: Int, defense: Int, weight: Int, maxMana: Int)
    extends MagicCharacter(name, hitPoints, defense, weight, maxMana):
  override def actions: List[Action] =
    super.actions ++ List(new Healing(), new Purification())

  override def equipBow(bow: Bow): Unit = doEquip(bow)
  override def equipWand(wand: Wand): Unit = doEquip(wand)
  override def equipStaff(staff: Staff): Unit = doEquip(staff)

  override def castWhiteSpell(spell: WhiteSpell, target: Target): Unit =
    castSpell(spell, target)
