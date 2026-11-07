package cl.uchile.dcc.controller

import cl.uchile.dcc.model.Player
import cl.uchile.dcc.model.actions.{Action, Target}
import cl.uchile.dcc.model.combat.TurnScheduler
import cl.uchile.dcc.model.map.Panel
import cl.uchile.dcc.model.units.GameUnit
import scala.collection.mutable.ListBuffer

/** Main game controller managing game flow, turns, and action execution. */
class GameController:
  private var activeScheduler: TurnScheduler = new TurnScheduler()
  private var activeUnit: Option[GameUnit] = None
  private val registeredPlayers: ListBuffer[Player] = ListBuffer.empty
  private val registeredPanels: ListBuffer[Panel] = ListBuffer.empty
  private var recordedError: Option[Throwable] = None

  /** The turn scheduler managing action bars. */
  def scheduler: TurnScheduler = activeScheduler

  /** Registered players in the game. */
  def players: List[Player] = registeredPlayers.toList

  /** Registered panels composing the map. */
  def panels: List[Panel] = registeredPanels.toList

  /** The last error caught during action execution, if any. */
  def lastError: Option[Throwable] = recordedError

  /** Message of the last error caught, if any. */
  def lastErrorMessage: Option[String] = recordedError.map(_.getMessage)

  /** Registers a player and their units into the combat scheduler. */
  def addPlayer(player: Player): Unit =
    if !registeredPlayers.contains(player) then
      registeredPlayers += player
      player.units.foreach(addUnit)

  /** Adds a unit to the turn scheduler. */
  def addUnit(unit: GameUnit): Unit =
    activeScheduler.addUnit(unit)

  /** Removes a unit from combat and clears it if it was the active unit. */
  def removeUnit(unit: GameUnit): Unit =
    activeScheduler.removeUnit(unit)
    if activeUnit.contains(unit) then
      activeUnit = None

  /** Registers a panel in the game controller. */
  def addPanel(panel: Panel): Unit =
    if !registeredPanels.contains(panel) then
      registeredPanels += panel

  /** The unit whose turn is currently active. */
  def currentUnit: Option[GameUnit] = activeUnit

  /** Manually sets the active unit, primarily for testing specific turn scenarios. */
  def currentUnit_=(unit: Option[GameUnit]): Unit =
    activeUnit = unit

  /**
   * Advances the turn scheduler until at least one unit is ready,
   * setting and returning the unit entitled to take the next turn.
   */
  def decideNextTurn(step: Double = 1.0): Option[GameUnit] =
    val livingUnits = activeScheduler.units.filterNot(_.isDefeated)
    if livingUnits.isEmpty then
      activeUnit = None
      activeUnit
    else
      while activeScheduler.nextUnit.isEmpty do
        activeScheduler.advance(step)
      activeUnit = activeScheduler.nextUnit
      activeUnit

  /** Returns all available actions for a given unit. */
  def availableActions(unit: GameUnit): List[Action] =
    unit.actions

  /**
   * Executes an action of a unit against a target.
   * If the action succeeds, defeated units are removed from the scheduler,
   * the acting unit's bar is emptied, the next turn is decided, and returns true.
   * If an exception occurs, it is recorded and the turn state is preserved.
   */
  def executeAction(unit: GameUnit, action: Action, target: Target): Boolean =
    try
      unit.doAction(action, target)
      recordedError = None

      // Remove any units that were defeated during this action from combat
      activeScheduler.units.filter(_.isDefeated).foreach(removeUnit)

      // Action succeeded: reset action bar and advance to next turn
      activeScheduler.resetBar(unit)
      decideNextTurn()
      true
    catch
      case e: Exception =>
        recordedError = Some(e)
        false

  /** Executes an action using the unit whose turn is currently active. */
  def executeAction(action: Action, target: Target): Boolean =
    activeUnit match
      case Some(unit) => executeAction(unit, action, target)
      case None =>
        val err = new IllegalStateException("Cannot execute action without an active unit.")
        recordedError = Some(err)
        false

  /** Resets the controller and game state completely. */
  def resetGame(): Unit =
    activeScheduler = new TurnScheduler()
    activeUnit = None
    registeredPlayers.clear()
    registeredPanels.clear()
    recordedError = None
