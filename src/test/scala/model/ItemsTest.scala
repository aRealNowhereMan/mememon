package cl.uchile.dcc.model

import cl.uchile.dcc.model.items.weapons.*
import cl.uchile.dcc.model.items.potions.*
import cl.uchile.dcc.model.units.Knight
import munit.FunSuite

class ItemsTest extends FunSuite:
  test("all five weapons store physical statistics and optional ownership") {
    val weapons = List(
      new Sword("Sword", 20, 5), new Dagger("Dagger", 20, 5),
      new Bow("Bow", 20, 5), new Wand("Wand", 20, 5, 40), new Staff("Staff", 20, 5, 40)
    )
    assertEquals(weapons.map(_.name), List("Sword", "Dagger", "Bow", "Wand", "Staff"))
    val owner = new Knight("Owner", 100, 5, 10)
    val nextOwner = new Knight("Next", 100, 5, 10)
    weapons.foreach { weapon =>
      assertEquals(weapon.attack, 20)
      assertEquals(weapon.weight, 5)
      assertEquals(weapon.owner, None)
      weapon.owner = Some(owner)
      assertEquals(weapon.owner, Some(owner))
      weapon.owner = Some(nextOwner)
      assertEquals(weapon.owner, Some(nextOwner))
      weapon.owner = None
      assertEquals(weapon.owner, None)
    }
  }

  test("magical weapons have additional magical attack") {
    assertEquals(new Wand("W", 10, 3, 50).magicAttack, 50)
    assertEquals(new Staff("S", 15, 8, 70).magicAttack, 70)
  }

  test("all four potions have default and custom names") {
    val defaults = List(new HealingPotion(), new StrengthPotion(), new ManaPotion(), new MagicStrengthPotion())
    assertEquals(defaults.map(_.name), List("Healing potion", "Strength potion", "Mana potion", "Magic strength potion"))
    val custom = List(new HealingPotion("H"), new StrengthPotion("S"), new ManaPotion("M"), new MagicStrengthPotion("MS"))
    assertEquals(custom.map(_.name), List("H", "S", "M", "MS"))
  }
