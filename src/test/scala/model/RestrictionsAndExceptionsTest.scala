package cl.uchile.dcc.model

import cl.uchile.dcc.model.actions.*
import cl.uchile.dcc.model.exceptions.*
import cl.uchile.dcc.model.items.potions.*
import cl.uchile.dcc.model.items.weapons.*
import cl.uchile.dcc.model.map.Panel
import cl.uchile.dcc.model.units.*
import munit.FunSuite

class RestrictionsAndExceptionsTest extends FunSuite:
  // Restriction 1: Moving must target an adjacent neighbor panel
  test("moving to a non-adjacent panel throws InvalidMovementException") {
    val p1 = new Panel(0, 0)
    val p2 = new Panel(2, 2) // not adjacent
    val knight = new Knight("Walker", 100, 10, 10)
    p1.addUnit(knight)

    val move = new Move()
    intercept[InvalidMovementException] {
      knight.doAction(move, p2)
    }
    // Unit remains on p1
    assertEquals(knight.panel, Some(p1))
    assertEquals(p1.units, List(knight))
  }

  // Restriction 2: Unit must have positive HP to perform any action
  test("defeated unit cannot perform any action") {
    val deadKnight = new Knight("Fallen", 100, 10, 10)
    deadKnight.hitPoints = 0
    assert(deadKnight.isDefeated)

    val targetEnemy = new Enemy("Goblin", 50, 10, 5, 10)
    val attack = new Attack()

    intercept[DefeatedUnitException] {
      deadKnight.doAction(attack, targetEnemy)
    }
  }

  // Restriction 3: Magic character must have enough mana to cast spell
  test("magic character without enough mana throws NotEnoughManaException") {
    val mage = new BlackMage("Vivi", 80, 5, 10, 15) // max mana 15
    val wand = new Wand("Wand", 10, 3, 20)
    mage.addItem(wand)
    mage.equip(wand)
    val enemy = new Enemy("Target", 50, 10, 5, 10)

    // Thunder costs 20, mage only has 15
    intercept[NotEnoughManaException] {
      mage.doAction(new Thunder(), enemy)
    }
    assertEquals(mage.mana, 15)
  }

  // Restriction 4: Defeated unit cannot receive actions
  test("defeated unit cannot receive actions") {
    val knight = new Knight("Arthur", 100, 10, 10)
    val sword = new Sword("Sword", 25, 5)
    knight.addItem(sword)
    knight.equip(sword)

    val deadEnemy = new Enemy("Ghost", 50, 10, 5, 10)
    deadEnemy.hitPoints = 0
    assert(deadEnemy.isDefeated)

    intercept[DefeatedTargetException] {
      knight.doAction(new Attack(), deadEnemy)
    }
  }

  // Restriction 5: Purification can only be received by enemies
  test("purification targeting character or panel throws InvalidTargetException") {
    val whiteMage = new WhiteMage("Rosa", 80, 5, 10, 100)
    val wand = new Wand("Wand", 10, 3, 30)
    whiteMage.addItem(wand)
    whiteMage.equip(wand)

    val allyKnight = new Knight("Ally", 100, 10, 10)
    val panel = new Panel(1, 1)

    intercept[InvalidTargetException] {
      whiteMage.doAction(new Purification(), allyKnight)
    }
    intercept[InvalidTargetException] {
      whiteMage.doAction(new Purification(), panel)
    }
  }

  // Restriction 6: Healing spell can only be received by characters
  test("healing spell targeting enemy or panel throws InvalidTargetException") {
    val whiteMage = new WhiteMage("Rosa", 80, 5, 10, 100)
    val wand = new Wand("Wand", 10, 3, 30)
    whiteMage.addItem(wand)
    whiteMage.equip(wand)

    val enemy = new Enemy("Enemy", 80, 10, 5, 10)
    val panel = new Panel(1, 1)

    intercept[InvalidTargetException] {
      whiteMage.doAction(new Healing(), enemy)
    }
    intercept[InvalidTargetException] {
      whiteMage.doAction(new Healing(), panel)
    }
  }

  // Restriction 7: Character must have a weapon equipped to perform a physical attack
  test("character without weapon attacking throws UnarmedAttackException") {
    val knight = new Knight("Unarmed", 100, 10, 10)
    val enemy = new Enemy("Target", 50, 10, 5, 10)

    intercept[UnarmedAttackException] {
      knight.doAction(new Attack(), enemy)
    }
  }

  // Restriction 8: Magic character must have a magic weapon to cast spells
  test("magic character with common weapon or no weapon throws NonMagicalWeaponException") {
    val mage = new BlackMage("Vivi", 80, 5, 10, 100)
    val enemy = new Enemy("Target", 50, 10, 5, 10)

    // No weapon
    intercept[NonMagicalWeaponException] {
      mage.doAction(new Thunder(), enemy)
    }

    // Common weapon (dagger) equipped
    val dagger = new Dagger("Common Dagger", 15, 2)
    mage.addItem(dagger)
    mage.equip(dagger)

    intercept[NonMagicalWeaponException] {
      mage.doAction(new Thunder(), enemy)
    }
  }

  // Restriction 9: Weapon compatibility table
  test("character weapon compatibility matches table specifications") {
    val sword = new Sword("Sword", 20, 5)
    val dagger = new Dagger("Dagger", 15, 3)
    val bow = new Bow("Bow", 18, 4)
    val wand = new Wand("Wand", 12, 3, 25)
    val staff = new Staff("Staff", 16, 6, 35)

    // Knight: Sword, Dagger, Bow (NO Wand, NO Staff)
    val knight = new Knight("K", 100, 10, 10)
    knight.equip(sword)
    assertEquals(knight.weapon, Some(sword))
    knight.equip(dagger)
    assertEquals(knight.weapon, Some(dagger))
    knight.equip(bow)
    assertEquals(knight.weapon, Some(bow))
    intercept[InvalidWeaponException] { knight.equip(wand) }
    intercept[InvalidWeaponException] { knight.equip(staff) }

    // Archer: Sword, Bow, Wand (NO Dagger, NO Staff)
    val archer = new Archer("A", 100, 10, 10)
    archer.equip(sword)
    assertEquals(archer.weapon, Some(sword))
    archer.equip(bow)
    assertEquals(archer.weapon, Some(bow))
    archer.equip(wand)
    assertEquals(archer.weapon, Some(wand))
    intercept[InvalidWeaponException] { archer.equip(dagger) }
    intercept[InvalidWeaponException] { archer.equip(staff) }

    // Thief: Sword, Dagger (NO Bow, NO Wand, NO Staff)
    val thief = new Thief("T", 100, 10, 10)
    thief.equip(sword)
    assertEquals(thief.weapon, Some(sword))
    thief.equip(dagger)
    assertEquals(thief.weapon, Some(dagger))
    intercept[InvalidWeaponException] { thief.equip(bow) }
    intercept[InvalidWeaponException] { thief.equip(wand) }
    intercept[InvalidWeaponException] { thief.equip(staff) }

    // Black Mage: Dagger, Wand, Staff (NO Sword, NO Bow)
    val blackMage = new BlackMage("BM", 80, 5, 8, 100)
    blackMage.equip(dagger)
    assertEquals(blackMage.weapon, Some(dagger))
    blackMage.equip(wand)
    assertEquals(blackMage.weapon, Some(wand))
    blackMage.equip(staff)
    assertEquals(blackMage.weapon, Some(staff))
    intercept[InvalidWeaponException] { blackMage.equip(sword) }
    intercept[InvalidWeaponException] { blackMage.equip(bow) }

    // White Mage: Bow, Wand, Staff (NO Sword, NO Dagger)
    val whiteMage = new WhiteMage("WM", 80, 5, 8, 100)
    whiteMage.equip(bow)
    assertEquals(whiteMage.weapon, Some(bow))
    whiteMage.equip(wand)
    assertEquals(whiteMage.weapon, Some(wand))
    whiteMage.equip(staff)
    assertEquals(whiteMage.weapon, Some(staff))
    intercept[InvalidWeaponException] { whiteMage.equip(sword) }
    intercept[InvalidWeaponException] { whiteMage.equip(dagger) }
  }

  test("forbidden actions and cross-magic cast throw InvalidActionException") {
    val enemy = new Enemy("E", 100, 15, 5, 10)
    val sword = new Sword("S", 20, 5)
    val potion = new HealingPotion()

    // Enemy cannot equip or consume
    intercept[InvalidActionException] {
      enemy.doAction(new EquipWeapon(List(sword)), enemy)
    }
    intercept[InvalidActionException] {
      enemy.doAction(new ConsumePotion(List(potion)), enemy)
    }

    // White mage cannot cast black magic (Thunder)
    val whiteMage = new WhiteMage("WM", 80, 5, 8, 100)
    val wand = new Wand("W", 10, 3, 20)
    whiteMage.addItem(wand)
    whiteMage.equip(wand)
    intercept[InvalidActionException] {
      whiteMage.doAction(new Thunder(), enemy)
    }

    // Black mage cannot cast white magic (Healing)
    val blackMage = new BlackMage("BM", 80, 5, 8, 100)
    blackMage.addItem(wand)
    blackMage.equip(wand)
    val ally = new Knight("K", 100, 10, 10)
    intercept[InvalidActionException] {
      blackMage.doAction(new Healing(), ally)
    }
  }
