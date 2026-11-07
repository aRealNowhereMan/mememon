package cl.uchile.dcc.controller

import cl.uchile.dcc.model.Player
import cl.uchile.dcc.model.actions.*
import cl.uchile.dcc.model.items.weapons.*
import cl.uchile.dcc.model.map.Panel
import cl.uchile.dcc.model.units.*
import munit.FunSuite

class GameControllerTest extends FunSuite:
  test("controller registers players, panels, and units into scheduler") {
    val controller = new GameController()
    val knight = new Knight("Arthur", 100, 10, 20)
    val enemy = new Enemy("Goblin", 50, 15, 5, 10)
    val player1 = new Player(List(knight))
    val player2 = new Player(List(enemy))
    val panel = new Panel(0, 0)

    controller.addPlayer(player1)
    controller.addPlayer(player2)
    controller.addPlayer(player1) // duplicate ignored
    controller.addPanel(panel)
    controller.addPanel(panel) // duplicate ignored

    assertEquals(controller.players, List(player1, player2))
    assertEquals(controller.panels, List(panel))
    assertEquals(controller.scheduler.units, List(knight, enemy))
    assertEquals(controller.currentUnit, None)
    assertEquals(controller.lastError, None)
  }

  test("decideNextTurn advances action bars until a unit is ready") {
    val controller = new GameController()
    val knight = new Knight("Arthur", 100, 10, 20) // max bar = 20
    val enemy = new Enemy("Goblin", 50, 15, 5, 10)   // max bar = 10
    controller.addUnit(knight)
    controller.addUnit(enemy)

    // Goblin has lower max bar (10), so Goblin will be ready first
    val next = controller.decideNextTurn(step = 5.0)
    assertEquals(next, Some(enemy))
    assertEquals(controller.currentUnit, Some(enemy))
  }

  test("availableActions returns the actions defined for the unit") {
    val controller = new GameController()
    val enemy = new Enemy("Goblin", 50, 15, 5, 10)
    assertEquals(controller.availableActions(enemy).map(_.name), List("Attack", "Move"))

    val mage = new BlackMage("Vivi", 80, 5, 10, 50)
    assertEquals(
      controller.availableActions(mage).map(_.name),
      List("Attack", "Move", "Equip weapon", "Consume potion", "Thunder", "Meteor")
    )
  }

  test("successful action execution resets acting unit bar, eliminates defeated units, and advances turn") {
    val controller = new GameController()
    val knight = new Knight("Arthur", 100, 10, 10)
    val sword = new Sword("Sword", 30, 2)
    knight.addItem(sword)
    knight.weapon = Some(sword)

    val weakEnemy = new Enemy("Weak Goblin", 15, 10, 0, 10) // 15 HP, 0 def -> 1 hit defeat
    controller.addUnit(knight)
    controller.addUnit(weakEnemy)

    // Setup turn
    controller.decideNextTurn(step = 10.0)
    val active = controller.currentUnit.get
    assert(active == knight || active == weakEnemy)

    // Force knight to act
    controller.currentUnit = Some(knight)
    val success = controller.executeAction(knight, new Attack(), weakEnemy)
    assert(success)
    assertEquals(controller.lastError, None)
    assert(weakEnemy.isDefeated)

    // Defeated enemy should be removed from scheduler
    assertEquals(controller.scheduler.units, List(knight))
  }

  test("failed action preserves active turn and records error in controller") {
    val controller = new GameController()
    val unarmedKnight = new Knight("Unarmed", 100, 10, 10)
    val enemy = new Enemy("Goblin", 50, 10, 5, 10)
    controller.addUnit(unarmedKnight)
    controller.addUnit(enemy)

    // Advance so unarmedKnight is ready
    controller.decideNextTurn(step = 15.0)
    controller.currentUnit = Some(unarmedKnight)

    // Unarmed knight tries to attack -> fails because unarmed!
    val success = controller.executeAction(unarmedKnight, new Attack(), enemy)
    assert(!success)
    assert(controller.lastError.isDefined)
    assert(controller.lastErrorMessage.get.contains("without an equipped weapon"))

    // Crucial requirement: turn must NOT change!
    assertEquals(controller.currentUnit, Some(unarmedKnight))
    // And action bar must NOT have been reset
    assert(controller.scheduler.actionBar(unarmedKnight).get >= 10.0)
  }

  test("executeAction without active unit fails cleanly") {
    val controller = new GameController()
    val dummy = new Panel(0, 0)
    val success = controller.executeAction(new Move(), dummy)
    assert(!success)
    assert(controller.lastError.isDefined)
  }

  test("resetGame clears controller state") {
    val controller = new GameController()
    val knight = new Knight("Arthur", 100, 10, 20)
    controller.addUnit(knight)
    controller.addPanel(new Panel(1, 1))
    controller.decideNextTurn(step = 25.0)
    assert(controller.currentUnit.isDefined)

    controller.resetGame()
    assertEquals(controller.scheduler.units, Nil)
    assertEquals(controller.currentUnit, None)
    assertEquals(controller.players, Nil)
    assertEquals(controller.panels, Nil)
    assertEquals(controller.lastError, None)
  }
