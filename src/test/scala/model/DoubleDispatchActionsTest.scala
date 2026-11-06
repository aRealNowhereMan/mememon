package cl.uchile.dcc.model

import cl.uchile.dcc.model.actions.*
import cl.uchile.dcc.model.items.potions.*
import cl.uchile.dcc.model.items.weapons.*
import cl.uchile.dcc.model.map.Panel
import cl.uchile.dcc.model.units.*
import munit.FunSuite

class DoubleDispatchActionsTest extends FunSuite:
  test("unit sources expose available actions and findAction works") {
    val enemy = new Enemy("Goblin", 100, 20, 5, 10)
    assertEquals(enemy.actions.map(_.name), List("Attack", "Move"))
    assertEquals(enemy.findAction("Attack").map(_.name), Some("Attack"))
    assertEquals(enemy.findAction("Move").map(_.name), Some("Move"))
    assertEquals(enemy.findAction("NonExistent"), None)

    val knight = new Knight("Arthur", 100, 10, 20)
    val sword = new Sword("Excalibur", 30, 5)
    val potion = new HealingPotion()
    knight.addItem(sword)
    knight.addItem(potion)
    assertEquals(knight.actions.map(_.name), List("Attack", "Move", "Equip weapon", "Consume potion"))
    assertEquals(knight.weaponsInInventory, List(sword))
    assertEquals(knight.potionsInInventory, List(potion))

    val blackMage = new BlackMage("Vivi", 80, 5, 10, 100)
    assertEquals(blackMage.actions.map(_.name), List("Attack", "Move", "Equip weapon", "Consume potion", "Thunder", "Meteor"))

    val whiteMage = new WhiteMage("Garnet", 80, 5, 10, 100)
    assertEquals(whiteMage.actions.map(_.name), List("Attack", "Move", "Equip weapon", "Consume potion", "Healing", "Purification"))
  }

  test("physical attack inflicts attack minus defense and reduces target HP") {
    val knight = new Knight("Arthur", 100, 10, 20)
    val sword = new Sword("Steel Sword", 25, 5)
    knight.weapon = Some(sword)
    val targetEnemy = new Enemy("Orc", 80, 15, 5, 15)

    val attack = new Attack()
    knight.doAction(attack, targetEnemy)
    // 25 - 5 = 20 damage -> 80 - 20 = 60
    assertEquals(targetEnemy.hitPoints, 60)

    // Enemy attacks knight: 15 attack - 10 defense = 5 damage -> 100 - 5 = 95
    targetEnemy.doAction(attack, knight)
    assertEquals(knight.hitPoints, 95)

    // Defense exceeds attack: no negative damage (0 damage)
    val heavyKnight = new Knight("Wall", 100, 50, 30)
    targetEnemy.doAction(attack, heavyKnight)
    assertEquals(heavyKnight.hitPoints, 100)
  }

  test("movement displaces unit between adjacent panels") {
    val p1 = new Panel(1, 1)
    val p2 = new Panel(1, 2)
    p1.addNeighbor(p2)
    p2.addNeighbor(p1)

    val unit = new Knight("Explorer", 100, 5, 10)
    p1.addUnit(unit)
    assertEquals(unit.panel, Some(p1))
    assertEquals(p1.units, List(unit))
    assertEquals(p2.units, Nil)

    val move = new Move()
    unit.doAction(move, p2)
    assertEquals(unit.panel, Some(p2))
    assertEquals(p1.units, Nil)
    assertEquals(p2.units, List(unit))
  }

  test("equip weapon via action updates weapon slot and ownership") {
    val knight = new Knight("Lancelot", 100, 10, 15)
    val sword1 = new Sword("Broadsword", 20, 5)
    val sword2 = new Sword("Claymore", 35, 12)
    knight.addItem(sword1)
    knight.addItem(sword2)

    // Equip sword1 via action targeting self
    val equip1 = new EquipWeapon(List(sword1))
    knight.doAction(equip1, knight)
    assertEquals(knight.weapon, Some(sword1))
    assertEquals(sword1.owner, Some(knight))

    // Equip sword2 via action targeting the weapon directly
    val equip2 = new EquipWeapon()
    knight.doAction(equip2, sword2)
    assertEquals(knight.weapon, Some(sword2))
    assertEquals(sword2.owner, Some(knight))
    assertEquals(sword1.owner, None)
  }

  test("potions apply effects and are removed from inventory") {
    val knight = new Knight("Paladin", 100, 20, 15)
    knight.hitPoints = 50
    val healing = new HealingPotion()
    val strength = new StrengthPotion()
    knight.addItem(healing)
    knight.addItem(strength)

    // Consume Healing Potion: 20% of 100 = 20 HP -> 50 + 20 = 70
    knight.doAction(new ConsumePotion(List(healing)), knight)
    assertEquals(knight.hitPoints, 70)
    assertEquals(knight.inventory, List(strength))

    // Consume Strength Potion: +15% of 20 = 3 -> defense becomes 23
    knight.doAction(new ConsumePotion(), strength)
    assertEquals(knight.defense, 23)
    assertEquals(knight.inventory, Nil)
  }

  test("magic character consumes mana potion and magic strength potion") {
    val mage = new BlackMage("Vivi", 100, 5, 10, 100)
    mage.mana = 20
    val manaPot = new ManaPotion()
    val strPot = new MagicStrengthPotion()
    mage.addItem(manaPot)
    mage.addItem(strPot)

    // Mana potion restores 30% of maxMana (30) -> 20 + 30 = 50
    mage.doAction(new ConsumePotion(List(manaPot)), mage)
    assertEquals(mage.mana, 50)
    assertEquals(mage.inventory, List(strPot))

    // Magic strength potion activates bonus
    assert(!mage.isMagicStrengthActive)
    mage.doAction(new ConsumePotion(List(strPot)), mage)
    assert(mage.isMagicStrengthActive)
    assertEquals(mage.inventory, Nil)
  }

  test("thunder spell reduces target HP by magic attack and consumes mana") {
    val mage = new BlackMage("Vivi", 100, 5, 10, 50)
    val wand = new Wand("Fire Wand", 10, 4, 30)
    mage.addItem(wand)
    mage.equip(wand)
    val enemy = new Enemy("Goblin", 100, 15, 10, 10)

    val thunder = new Thunder()
    mage.doAction(thunder, enemy)
    // Mana: 50 - 20 = 30. Damage: 30 directly -> 100 - 30 = 70
    assertEquals(mage.mana, 30)
    assertEquals(enemy.hitPoints, 70)
  }

  test("thunder spell consumes magic strength boost when active") {
    val mage = new BlackMage("Vivi", 100, 5, 10, 50)
    val wand = new Wand("Oak Wand", 10, 4, 40)
    mage.addItem(wand)
    mage.equip(wand)
    val potion = new MagicStrengthPotion()
    mage.addItem(potion)
    mage.consume(potion)
    assert(mage.isMagicStrengthActive)

    val enemy = new Enemy("Boss", 100, 10, 5, 10)
    mage.doAction(new Thunder(), enemy)
    // Magic strength active: 40 * 1.5 = 60 damage -> 100 - 60 = 40
    assertEquals(enemy.hitPoints, 40)
    assert(!mage.isMagicStrengthActive)

    // Next thunder has normal damage (40): 40 - 40 = 0 (defeated)
    mage.doAction(new Thunder(), enemy)
    assertEquals(enemy.hitPoints, 0)
    assert(enemy.isDefeated)
  }

  test("meteor spell attacks all units on target panel") {
    val mage = new BlackMage("Vivi", 100, 5, 10, 100)
    val staff = new Staff("Arch Staff", 15, 6, 25)
    mage.addItem(staff)
    mage.equip(staff)

    val panel = new Panel(2, 2)
    val e1 = new Enemy("Orc 1", 50, 10, 5, 10)
    val e2 = new Enemy("Orc 2", 80, 10, 5, 10)
    panel.addUnit(e1)
    panel.addUnit(e2)

    val meteor = new Meteor()
    mage.doAction(meteor, panel)
    // Mana: 100 - 50 = 50. Damage to panel units: 25 each
    assertEquals(mage.mana, 50)
    assertEquals(e1.hitPoints, 25)
    assertEquals(e2.hitPoints, 55)
  }

  test("healing spell restores HP of target character and costs mana") {
    val healer = new WhiteMage("Rosa", 80, 5, 8, 60)
    val wand = new Wand("Holy Wand", 10, 3, 40)
    healer.addItem(wand)
    healer.equip(wand)

    val knight = new Knight("Cecil", 100, 10, 20)
    knight.hitPoints = 30

    val heal = new Healing()
    healer.doAction(heal, knight)
    // Mana: 60 - 15 = 45. Heal: 20% of 100 (20) + 40 / 4 (10) = 30 -> 30 + 30 = 60
    assertEquals(healer.mana, 45)
    assertEquals(knight.hitPoints, 60)
  }

  test("purification spell curses target enemy and costs mana") {
    val caster = new WhiteMage("Rosa", 80, 5, 8, 60)
    val staff = new Staff("Judgement Staff", 12, 5, 50)
    caster.addItem(staff)
    caster.equip(staff)

    val enemy = new Enemy("Demon", 100, 20, 10, 12)

    val purification = new Purification()
    caster.doAction(purification, enemy)
    // Mana: 60 - 25 = 35. Damage: 15% of 100 (15) + 50 / 2 (25) = 40 -> 100 - 40 = 60
    assertEquals(caster.mana, 35)
    assertEquals(enemy.hitPoints, 60)
  }
