package cl.uchile.dcc.model.units

/** Base statistics shared by characters and enemies. */
abstract class GameUnit(
    val name: String,
    val maxHitPoints: Int,
    val defense: Int,
    val weight: Int
):
  private var currentHitPoints: Int = maxHitPoints

  /** Current health of this unit. */
  def hitPoints: Int = currentHitPoints

  /** Sets current health, between zero and the initial maximum. */
  def hitPoints_=(value: Int): Unit =
    require(value >= 0 && value <= maxHitPoints)
    currentHitPoints = value

  /** Whether this unit has no health left. */
  def isDefeated: Boolean = hitPoints == 0

  /** Points needed to complete this unit's action bar. */
  def actionBarMaximum: Double = weight.toDouble
