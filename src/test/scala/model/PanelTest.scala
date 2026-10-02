package cl.uchile.dcc.model

import cl.uchile.dcc.model.map.Panel
import cl.uchile.dcc.model.units.Knight
import munit.FunSuite

class PanelTest extends FunSuite:
  test("a panel stores coordinates and manages distinct units") {
    val panel = new Panel(2, 3)
    assertEquals((panel.x, panel.y), (2, 3))
    assertEquals(panel.units, Nil)
    val first = new Knight("K", 100, 5, 10)
    val second = new Knight("K", 100, 5, 10)
    panel.addUnit(first)
    panel.addUnit(first)
    panel.addUnit(second)
    val snapshot = panel.units
    assertEquals(snapshot, List(first, second))
    panel.removeUnit(first)
    panel.removeUnit(first)
    assertEquals(panel.units, List(second))
    assertEquals(snapshot, List(first, second))
    panel.removeUnit(second)
    assertEquals(panel.units, Nil)
  }

  test("neighbors are the four orthogonal cells, not diagonals or the same cell") {
    val center = new Panel(2, 2)
    val neighbors = List(new Panel(1, 2), new Panel(3, 2), new Panel(2, 1), new Panel(2, 3))
    assertEquals(center.neighbors, Nil)
    neighbors.foreach { neighbor =>
      assert(center.isAdjacentTo(neighbor))
      center.addNeighbor(neighbor)
      center.addNeighbor(neighbor)
      assertEquals(neighbor.neighbors, Nil)
    }
    assertEquals(center.neighbors, neighbors)
    List(center, new Panel(2, 2), new Panel(3, 3), new Panel(2, 4)).foreach { invalid =>
      assert(!center.isAdjacentTo(invalid))
      intercept[IllegalArgumentException] { center.addNeighbor(invalid) }
    }
    val snapshot = center.neighbors
    center.removeNeighbor(neighbors.head)
    center.removeNeighbor(neighbors.head)
    assertEquals(center.neighbors, neighbors.tail)
    assertEquals(snapshot, neighbors)
  }

  test("a corner may register just two neighbors and links may be reciprocal") {
    val corner = new Panel(1, 1)
    val right = new Panel(2, 1)
    val up = new Panel(1, 2)
    corner.addNeighbor(right)
    corner.addNeighbor(up)
    right.addNeighbor(corner)
    assertEquals(corner.neighbors, List(right, up))
    assertEquals(right.neighbors, List(corner))
    assert(!new Panel(Int.MinValue, 0).isAdjacentTo(new Panel(Int.MaxValue, 0)))
  }
