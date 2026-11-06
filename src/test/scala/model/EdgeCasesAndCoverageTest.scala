package cl.uchile.dcc.model

import cl.uchile.dcc.model.actions.*
import cl.uchile.dcc.model.exceptions.*
import cl.uchile.dcc.model.items.potions.*
import cl.uchile.dcc.model.items.weapons.*
import cl.uchile.dcc.model.map.Panel
import cl.uchile.dcc.model.units.*
import munit.FunSuite

class EdgeCasesAndCoverageTest extends FunSuite:
  test("panel rejects incompatible actions with InvalidTargetException") {
    val panel = new Panel(0, 0)
    val knight = new Knight("K", 100, 10, 10)
    val mage = new BlackMage("BM", 80, 5, 10, 100)
    val whiteMage = new WhiteMage("WM", 80, 5, 10, 100)
    val sword = new Sword("S", 20, 5)
    val potion = new HealingPotion()

    intercept[InvalidTargetException] { panel.receiveAttack(knight, 20) }
    intercept[InvalidTargetException] { panel.receiveThunder(mage, 30) }
    intercept[InvalidTargetException] { panel.receiveHealing(whiteMage, 20) }
    intercept[InvalidTargetException] { panel.receivePurification(whiteMage, 25) }
    intercept[InvalidTargetException] { panel.receiveEquipWeapon(knight, sword) }
    intercept[InvalidTargetException] { panel.receiveConsumePotion(knight, potion) }
  }

  test("weapons reject incompatible actions with InvalidTargetException") {
    val sword = new Sword("Blade", 20, 5)
    val knight = new Knight("K", 100, 10, 10)
    val mage = new BlackMage("BM", 80, 5, 10, 100)
    val potion = new HealingPotion()

    intercept[InvalidTargetException] { sword.receiveAttack(knight, 20) }
    intercept[InvalidTargetException] { sword.receiveMove(knight) }
    intercept[InvalidTargetException] { sword.receiveThunder(mage, 20) }
    intercept[InvalidTargetException] { sword.receiveMeteor(mage, 20) }
    intercept[InvalidTargetException] { sword.receiveHealing(null, 20) }
    intercept[InvalidTargetException] { sword.receivePurification(null, 20) }
    intercept[InvalidTargetException] { sword.receiveConsumePotion(knight, potion) }

    assertEquals(sword.asWeapon, Some(sword))
    assertEquals(sword.asPotion, None)
    assertEquals(sword.isMagic, false)
    assertEquals(sword.magicAttackValue, 0)
  }

  test("potions reject incompatible actions with InvalidTargetException") {
    val potion = new HealingPotion()
    val knight = new Knight("K", 100, 10, 10)
    val mage = new BlackMage("BM", 80, 5, 10, 100)
    val sword = new Sword("S", 20, 5)

    intercept[InvalidTargetException] { potion.receiveAttack(knight, 20) }
    intercept[InvalidTargetException] { potion.receiveMove(knight) }
    intercept[InvalidTargetException] { potion.receiveThunder(mage, 20) }
    intercept[InvalidTargetException] { potion.receiveMeteor(mage, 20) }
    intercept[InvalidTargetException] { potion.receiveHealing(null, 20) }
    intercept[InvalidTargetException] { potion.receivePurification(null, 20) }
    intercept[InvalidTargetException] { potion.receiveEquipWeapon(knight, sword) }

    assertEquals(potion.asWeapon, None)
    assertEquals(potion.asPotion, Some(potion))
  }

  test("units reject movement and meteor onto themselves with InvalidTargetException") {
    val knight = new Knight("K", 100, 10, 10)
    val other = new Knight("O", 100, 10, 10)
    val mage = new BlackMage("BM", 80, 5, 10, 100)
    val sword = new Sword("S", 20, 5)
    val potion = new HealingPotion()

    intercept[InvalidTargetException] { knight.receiveMove(other) }
    intercept[InvalidTargetException] { knight.receiveMeteor(mage, 30) }
    intercept[InvalidTargetException] { knight.receiveEquipWeapon(other, sword) }
    intercept[InvalidTargetException] { knight.receiveConsumePotion(other, potion) }
  }

  test("non-magical character consuming magical potions does not alter state") {
    val knight = new Knight("K", 100, 10, 10)
    val manaPot = new ManaPotion()
    val magicStrPot = new MagicStrengthPotion()
    knight.addItem(manaPot)
    knight.addItem(magicStrPot)

    knight.consume(manaPot)
    knight.consume(magicStrPot)
    assertEquals(knight.inventory, Nil)
  }

  test("consuming potion not in inventory throws InvalidActionException") {
    val knight = new Knight("K", 100, 10, 10)
    val potion = new HealingPotion()
    intercept[InvalidActionException] {
      knight.consume(potion)
    }
  }

  test("equipping or consuming without specified item throws InvalidActionException") {
    val knight = new Knight("K", 100, 10, 10)
    intercept[InvalidActionException] {
      knight.doAction(new EquipWeapon(Nil), knight)
    }
    intercept[InvalidActionException] {
      knight.doAction(new ConsumePotion(Nil), knight)
    }
  }

  test("equipping a new weapon detaches owner of previous weapon") {
    val knight = new Knight("K", 100, 10, 10)
    val sword1 = new Sword("S1", 20, 5)
    val sword2 = new Sword("S2", 25, 6)
    knight.addItem(sword1)
    knight.addItem(sword2)

    knight.equip(sword1)
    assertEquals(sword1.owner, Some(knight))
    assertEquals(knight.weapon, Some(sword1))

    knight.equip(sword2)
    assertEquals(sword1.owner, None)
    assertEquals(sword2.owner, Some(knight))
    assertEquals(knight.weapon, Some(sword2))
  }

  test("magical weapon properties on Wand and Staff") {
    val wand = new Wand("W", 10, 3, 40)
    val staff = new Staff("S", 12, 5, 60)
    assertEquals(wand.isMagic, true)
    assertEquals(wand.magicAttackValue, 40)
    assertEquals(staff.isMagic, true)
    assertEquals(staff.magicAttackValue, 60)
  }
