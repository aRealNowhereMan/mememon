package cl.uchile.dcc.model

import cl.uchile.dcc.model.actions.*
import cl.uchile.dcc.model.items.weapons.*
import cl.uchile.dcc.model.items.potions.*
import munit.FunSuite

class ActionsTest extends FunSuite:
  test("the eight actions expose their names through the common interface") {
    val actions: List[Action] = List(new Attack(), new Move(), new EquipWeapon(Nil),
      new ConsumePotion(Nil), new Thunder(), new Meteor(), new Healing(), new Purification())
    assertEquals(actions.map(_.name), List("Attack", "Move", "Equip weapon", "Consume potion",
      "Thunder", "Meteor", "Healing", "Purification"))
  }

  test("item actions retain their available items in order and allow empty lists") {
    val sword = new Sword("S", 10, 5)
    val bow = new Bow("B", 15, 10)
    val healing = new HealingPotion()
    val mana = new ManaPotion()
    assertEquals(new EquipWeapon(List(sword, bow)).items, List(sword, bow))
    assertEquals(new ConsumePotion(List(healing, mana)).items, List(healing, mana))
    assertEquals(new EquipWeapon(Nil).items, Nil)
    assertEquals(new ConsumePotion(Nil).items, Nil)
  }

  test("black and white spells store the costs specified in the statement") {
    val black: List[BlackSpell] = List(new Thunder(), new Meteor())
    val white: List[WhiteSpell] = List(new Healing(), new Purification())
    assertEquals(black.map(_.manaCost), List(20, 50))
    assertEquals(white.map(_.manaCost), List(15, 25))
  }
