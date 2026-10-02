package cl.uchile.dcc.model.map

import cl.uchile.dcc.model.units.GameUnit

/** A map cell with units and explicitly registered orthogonal neighbors. */
class Panel(val x: Int, val y: Int):
  private var storedUnits: List[GameUnit] = List.empty
  private var storedNeighbors: List[Panel] = List.empty

  /** An immutable snapshot of the units on this panel. */
  def units: List[GameUnit] = storedUnits

  /** Adds a unit only if it is not already on this panel. */
  def addUnit(unit: GameUnit): Unit =
    if !storedUnits.contains(unit) then storedUnits = storedUnits :+ unit

  /** Removes a unit if present. */
  def removeUnit(unit: GameUnit): Unit =
    storedUnits = storedUnits.filterNot(_ == unit)

  /** An immutable snapshot of registered neighboring panels. */
  def neighbors: List[Panel] = storedNeighbors

  /** Whether the coordinates are one horizontal or vertical step apart. */
  def isAdjacentTo(panel: Panel): Boolean =
    math.abs(x.toLong - panel.x) + math.abs(y.toLong - panel.y) == 1

  /** Registers a neighboring panel on this panel only, without duplicates. */
  def addNeighbor(panel: Panel): Unit =
    require(isAdjacentTo(panel), "Neighbors must be orthogonally adjacent")
    if !storedNeighbors.contains(panel) then storedNeighbors = storedNeighbors :+ panel

  /** Removes a registered neighbor if present. */
  def removeNeighbor(panel: Panel): Unit =
    storedNeighbors = storedNeighbors.filterNot(_ == panel)
