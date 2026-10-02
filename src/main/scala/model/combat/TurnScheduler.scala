package cl.uchile.dcc.model.combat

import cl.uchile.dcc.model.units.GameUnit
import scala.collection.mutable

/** Tracks action bars and selects ready units, without running the combat loop. */
class TurnScheduler:
  private val bars = mutable.LinkedHashMap.empty[GameUnit, Double]

  /** Units in insertion order. */
  def units: List[GameUnit] = bars.keys.toList

  /** Adds a unit with an empty bar; adding it again preserves its progress. */
  def addUnit(unit: GameUnit): Unit =
    if !bars.contains(unit) then bars(unit) = 0.0

  /** Removes a unit and its progress, if present. */
  def removeUnit(unit: GameUnit): Unit =
    bars.remove(unit)

  /** Current progress, or None when the unit is not registered. */
  def actionBar(unit: GameUnit): Option[Double] = bars.get(unit)

  /** Calculates a unit's maximum using its current weapon and weight. */
  def maximum(unit: GameUnit): Double = unit.actionBarMaximum

  /** Calculates the maximum for every registered unit, in insertion order. */
  def maximums: List[(GameUnit, Double)] = units.map(unit => (unit, maximum(unit)))

  /** Empties a registered unit's bar; absent units are ignored. */
  def resetBar(unit: GameUnit): Unit =
    if bars.contains(unit) then bars(unit) = 0.0

  /** Adds the same finite, non-negative amount to every bar, retaining excess. */
  def advance(amount: Double): Unit =
    require(amount >= 0 && !amount.isInfinity && !amount.isNaN)
    units.foreach(unit => bars(unit) = bars(unit) + amount)

  /** Whether a registered unit has reached or exceeded its maximum. */
  def isReady(unit: GameUnit): Boolean =
    bars.get(unit).exists(_ >= maximum(unit))

  /** Ready units, by decreasing excess; ties use insertion order. */
  def readyUnits: List[GameUnit] =
    units.zipWithIndex.filter { case (unit, _) => isReady(unit) }
      .sortBy { case (unit, index) => (-(bars(unit) - maximum(unit)), index) }
      .map { case (unit, _) => unit }

  /** The single unit entitled to act, or None if no unit is ready.
    * Querying does not consume the turn. Reset its bar once the action finishes.
    */
  def nextUnit: Option[GameUnit] = readyUnits.headOption
