package cl.uchile.dcc.model

import cl.uchile.dcc.model.combat.TurnScheduler
import cl.uchile.dcc.model.units.*
import cl.uchile.dcc.model.items.weapons.Sword
import munit.FunSuite

class TurnSchedulerTest extends FunSuite:
  test("an empty scheduler has no turn and ignores absent units") {
    val scheduler = new TurnScheduler()
    val unit = new Knight("K", 100, 5, 10)
    scheduler.advance(5)
    scheduler.resetBar(unit)
    scheduler.removeUnit(unit)
    assertEquals(scheduler.units, Nil)
    assertEquals(scheduler.maximums, Nil)
    assertEquals(scheduler.readyUnits, Nil)
    assertEquals(scheduler.nextUnit, None)
    assertEquals(scheduler.actionBar(unit), None)
    assert(!scheduler.isReady(unit))
  }

  test("registration starts at zero, preserves progress on duplicates and uses identity") {
    val scheduler = new TurnScheduler()
    val first = new Knight("K", 100, 5, 10)
    val second = new Knight("K", 100, 5, 10)
    scheduler.addUnit(first)
    assertEquals(scheduler.actionBar(first), Some(0.0))
    scheduler.advance(3)
    scheduler.addUnit(first)
    scheduler.addUnit(second)
    assertEquals(scheduler.units, List(first, second))
    assertEquals(scheduler.actionBar(first), Some(3.0))
    assertEquals(scheduler.actionBar(second), Some(0.0))
  }

  test("maximums include half weapon weight and reflect replacement or removal") {
    val scheduler = new TurnScheduler()
    val knight = new Knight("K", 100, 5, 10)
    val enemy = new Enemy("E", 100, 20, 5, 8)
    scheduler.addUnit(knight)
    scheduler.addUnit(enemy)
    assertEquals(scheduler.maximums, List((knight, 10.0), (enemy, 8.0)))
    knight.weapon = Some(new Sword("S", 20, 5))
    assertEquals(scheduler.maximum(knight), 12.5)
    assertEquals(scheduler.maximums, List((knight, 12.5), (enemy, 8.0)))
    scheduler.advance(12)
    assert(!scheduler.isReady(knight))
    knight.weapon = Some(new Sword("Heavy", 20, 20))
    assertEquals(scheduler.maximum(knight), 20.0)
    knight.weapon = None
    assert(scheduler.isReady(knight))
  }

  test("advance changes every bar by the same amount and accepts zero and fractions") {
    val scheduler = new TurnScheduler()
    val first = new Knight("A", 100, 5, 10)
    val second = new Enemy("B", 100, 20, 5, 20)
    scheduler.addUnit(first)
    scheduler.advance(2)
    scheduler.addUnit(second)
    scheduler.advance(0)
    scheduler.advance(3.5)
    assertEquals(scheduler.actionBar(first), Some(5.5))
    assertEquals(scheduler.actionBar(second), Some(3.5))
    assert(!scheduler.isReady(first))
    assertEquals(scheduler.nextUnit, None)
  }

  test("an exact fractional maximum is ready and excess is retained") {
    val scheduler = new TurnScheduler()
    val unit = new Knight("K", 100, 5, 10)
    unit.weapon = Some(new Sword("S", 20, 5))
    scheduler.addUnit(unit)
    scheduler.advance(12)
    assert(!scheduler.isReady(unit))
    scheduler.advance(0.5)
    assert(scheduler.isReady(unit))
    assertEquals(scheduler.nextUnit, Some(unit))
    scheduler.advance(100)
    assertEquals(scheduler.actionBar(unit), Some(112.5))
  }

  test("priority is decreasing excess, rather than raw progress or registration order") {
    val scheduler = new TurnScheduler()
    val heavy = new Knight("Heavy", 100, 5, 30)
    val light = new Enemy("Light", 100, 20, 5, 10)
    val waiting = new Knight("Waiting", 100, 5, 100)
    scheduler.addUnit(heavy)
    scheduler.advance(25)
    scheduler.addUnit(light)
    scheduler.addUnit(waiting)
    scheduler.advance(10)
    // Heavy has 35 points (excess 5), light has 10 points (excess 0).
    assertEquals(scheduler.readyUnits, List(heavy, light))
    scheduler.resetBar(heavy)
    scheduler.advance(35)
    // Light and waiting have 45 points, but only light is ready; its excess is 35.
    assertEquals(scheduler.readyUnits, List(light, heavy))
    assertEquals(scheduler.nextUnit, Some(light))
    assertEquals(scheduler.nextUnit, Some(light))
    assertEquals(scheduler.actionBar(light), Some(45.0))
  }

  test("ties use insertion order and resetting the chosen unit exposes the next turn") {
    val scheduler = new TurnScheduler()
    val first = new Knight("First", 100, 5, 10)
    val second = new Knight("Second", 100, 5, 10)
    scheduler.addUnit(first)
    scheduler.addUnit(second)
    scheduler.advance(15)
    assertEquals(scheduler.readyUnits, List(first, second))
    assertEquals(scheduler.nextUnit, Some(first))
    scheduler.resetBar(first)
    assertEquals(scheduler.actionBar(first), Some(0.0))
    assertEquals(scheduler.actionBar(second), Some(15.0))
    assertEquals(scheduler.nextUnit, Some(second))
    scheduler.resetBar(second)
    assertEquals(scheduler.nextUnit, None)
    scheduler.advance(10)
    assertEquals(scheduler.nextUnit, Some(first))
  }

  test("removing a ready unit removes its turn and re-adding starts fresh at the end") {
    val scheduler = new TurnScheduler()
    val first = new Knight("First", 100, 5, 10)
    val second = new Knight("Second", 100, 5, 10)
    scheduler.addUnit(first)
    scheduler.addUnit(second)
    scheduler.advance(10)
    val snapshot = scheduler.units
    scheduler.removeUnit(first)
    assertEquals(scheduler.actionBar(first), None)
    assert(!scheduler.isReady(first))
    assertEquals(scheduler.nextUnit, Some(second))
    assertEquals(snapshot, List(first, second))
    scheduler.addUnit(first)
    assertEquals(scheduler.units, List(second, first))
    assertEquals(scheduler.actionBar(first), Some(0.0))
    scheduler.resetBar(second)
    scheduler.advance(10)
    assertEquals(scheduler.readyUnits, List(second, first))
    scheduler.removeUnit(second)
    scheduler.removeUnit(first)
    assertEquals(scheduler.nextUnit, None)
  }

  test("invalid increments are rejected without changing progress") {
    val scheduler = new TurnScheduler()
    val unit = new Knight("K", 100, 5, 10)
    scheduler.addUnit(unit)
    scheduler.advance(2)
    List(-1.0, Double.NaN, Double.PositiveInfinity, Double.NegativeInfinity).foreach { amount =>
      intercept[IllegalArgumentException] { scheduler.advance(amount) }
      assertEquals(scheduler.actionBar(unit), Some(2.0))
    }
  }
