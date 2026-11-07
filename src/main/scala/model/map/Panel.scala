package cl.uchile.dcc.model.map

import cl.uchile.dcc.model.actions.Target
import cl.uchile.dcc.model.exceptions.{InvalidMovementException, InvalidTargetException}
import cl.uchile.dcc.model.items.potions.Potion
import cl.uchile.dcc.model.items.weapons.Weapon
import cl.uchile.dcc.model.units.{Character, GameUnit, MagicCharacter}

/** A map cell with units, orthogonal neighbors and target capabilities. */
class Panel(val x: Int, val y: Int) extends Target:
  private var storedUnits: List[GameUnit] = List.empty
  private var storedNeighbors: List[Panel] = List.empty

  /** An immutable snapshot of the units on this panel. */
  def units: List[GameUnit] = storedUnits

  /** Adds a unit only if it is not already on this panel. */
  def addUnit(unit: GameUnit): Unit =
    if !storedUnits.contains(unit) then
      storedUnits = storedUnits :+ unit
      unit.panel = Some(this)

  /** Removes a unit if present. */
  def removeUnit(unit: GameUnit): Unit =
    if storedUnits.contains(unit) then
      storedUnits = storedUnits.filterNot(_ == unit)
      if unit.panel.contains(this) then
        unit.panel = None

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

  override def receiveAttack(attacker: GameUnit, attackDamage: Int): Unit =
    throw new InvalidTargetException("Panels cannot receive physical attacks.")

  override def receiveMove(unit: GameUnit): Unit =
    unit.panel match
      case Some(current) =>
        if !current.neighbors.contains(this) then
          throw new InvalidMovementException(
            s"Panel ($x, $y) is not adjacent to current panel (${current.x}, ${current.y})."
          )
        current.removeUnit(unit)
        addUnit(unit)
      case None =>
        addUnit(unit)

  override def receiveThunder(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Panels cannot receive thunder spells.")

  override def receiveMeteor(caster: MagicCharacter, magicDamage: Int): Unit =
    units.filterNot(_.isDefeated).foreach { targetUnit =>
      targetUnit.hitPoints = math.max(0, targetUnit.hitPoints - magicDamage)
    }

  override def receiveHealing(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Panels cannot receive healing spells.")

  override def receivePurification(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Panels cannot receive purification spells.")

  override def receiveEquipWeapon(character: Character, weapon: Weapon): Unit =
    throw new InvalidTargetException("Panels cannot equip weapons.")

  override def receiveConsumePotion(character: Character, potion: Potion): Unit =
    throw new InvalidTargetException("Panels cannot consume potions.")
