package cl.uchile.dcc.model

import cl.uchile.dcc.model.units.*
import cl.uchile.dcc.model.items.potions.HealingPotion
import cl.uchile.dcc.model.items.weapons.Sword
import munit.FunSuite

class UnitsTest extends FunSuite:
  test("all character classes store their initial statistics and empty slots") {
    val characters = List(
      new Knight("Knight", 100, 10, 20),
      new Archer("Archer", 100, 10, 20),
      new Thief("Thief", 100, 10, 20),
      new BlackMage("Black mage", 100, 10, 20, 50),
      new WhiteMage("White mage", 100, 10, 20, 50)
    )
    assertEquals(characters.map(_.name), List("Knight", "Archer", "Thief", "Black mage", "White mage"))
    characters.foreach { character =>
      assertEquals(character.hitPoints, 100)
      assertEquals(character.maxHitPoints, 100)
      assertEquals(character.defense, 10)
      assertEquals(character.weight, 20)
      assertEquals(character.weapon, None)
      assertEquals(character.inventory, Nil)
      assertEquals(character.actionBarMaximum, 20.0)
      assert(!character.isDefeated)
    }
  }

  test("both magical classes store and update mana within its limits") {
    val mages = List(new BlackMage("B", 100, 5, 10, 50), new WhiteMage("W", 100, 5, 10, 50))
    mages.foreach { mage =>
      assertEquals(mage.mana, 50)
      assertEquals(mage.maxMana, 50)
      mage.mana = 0
      assertEquals(mage.mana, 0)
      mage.mana = 25
      assertEquals(mage.mana, 25)
      mage.mana = 50
      intercept[IllegalArgumentException] { mage.mana = -1 }
      intercept[IllegalArgumentException] { mage.mana = 51 }
      assertEquals(mage.mana, 50)
    }
  }

  test("enemy stores attack and has an action bar based only on its weight") {
    val enemy = new Enemy("Goblin", 80, 30, 5, 12)
    assertEquals(enemy.name, "Goblin")
    assertEquals(enemy.hitPoints, 80)
    assertEquals(enemy.maxHitPoints, 80)
    assertEquals(enemy.attack, 30)
    assertEquals(enemy.defense, 5)
    assertEquals(enemy.weight, 12)
    assertEquals(enemy.actionBarMaximum, 12.0)
  }

  test("health changes determine defeat and respect maximum health") {
    val unit = new Knight("K", 100, 5, 10)
    unit.hitPoints = 1
    assert(!unit.isDefeated)
    unit.hitPoints = 0
    assert(unit.isDefeated)
    unit.hitPoints = 100
    assert(!unit.isDefeated)
    intercept[IllegalArgumentException] { unit.hitPoints = -1 }
    intercept[IllegalArgumentException] { unit.hitPoints = 101 }
    assertEquals(unit.hitPoints, 100)
  }

  test("inventory accepts weapons and potions and exposes independent snapshots") {
    val character = new Knight("K", 100, 5, 10)
    val other = new Knight("K", 100, 5, 10)
    val sword = new Sword("Sword", 20, 5)
    val potion = new HealingPotion()
    character.addItem(sword)
    character.addItem(potion)
    val snapshot = character.inventory
    assertEquals(snapshot, List(sword, potion))
    character.removeItem(sword)
    character.removeItem(sword)
    assertEquals(character.inventory, List(potion))
    assertEquals(snapshot, List(sword, potion))
    character.removeItem(potion)
    assertEquals(character.inventory, Nil)
    assertEquals(other.inventory, Nil)
    assert(character != other)
  }

  test("weapon slot can be assigned, replaced and emptied") {
    val character = new Knight("K", 100, 5, 10)
    val light = new Sword("Light", 20, 5)
    val heavy = new Sword("Heavy", 30, 20)
    character.weapon = Some(light)
    assertEquals(character.weapon, Some(light))
    assertEquals(character.actionBarMaximum, 12.5)
    character.weapon = Some(heavy)
    assertEquals(character.actionBarMaximum, 20.0)
    character.weapon = None
    assertEquals(character.actionBarMaximum, 10.0)
  }

  test("a mixed player team is defeated only when all its units are defeated") {
    val knight = new Knight("K", 100, 5, 10)
    val enemy = new Enemy("E", 80, 20, 5, 10)
    val player = new Player(List(knight, enemy))
    assertEquals(player.units, List(knight, enemy))
    assert(!player.isDefeated)
    knight.hitPoints = 0
    assert(!player.isDefeated)
    enemy.hitPoints = 0
    assert(player.isDefeated)
    knight.hitPoints = 1
    assert(!player.isDefeated)
    assert(new Player(Nil).isDefeated)
  }
