package cl.uchile.dcc.model.units

import cl.uchile.dcc.model.actions.{Action, BlackSpell, ConsumePotion, EquipWeapon, Source, Target, WhiteSpell}
import cl.uchile.dcc.model.exceptions.{DefeatedTargetException, DefeatedUnitException, InvalidActionException, InvalidTargetException}
import cl.uchile.dcc.model.items.potions.Potion
import cl.uchile.dcc.model.items.weapons.Weapon
import cl.uchile.dcc.model.map.Panel

/** Base statistics, action source and target capabilities shared by all units. */
abstract class GameUnit(
    val name: String,
    val maxHitPoints: Int,
    baseDefense: Int,
    val weight: Int
) extends Source with Target:
  private var currentHitPoints: Int = maxHitPoints
  private var currentDefense: Int = baseDefense
  private var storedPanel: Option[Panel] = None

  /** Current health of this unit. */
  def hitPoints: Int = currentHitPoints

  /** Sets current health, between zero and the initial maximum. */
  def hitPoints_=(value: Int): Unit =
    require(value >= 0 && value <= maxHitPoints)
    currentHitPoints = value

  /** Current defense of this unit. */
  def defense: Int = currentDefense

  /** Sets current defense to a non-negative value. */
  def defense_=(value: Int): Unit =
    require(value >= 0)
    currentDefense = value

  /** Map panel where this unit is currently placed. */
  def panel: Option[Panel] = storedPanel

  /** Sets the panel this unit is placed on. */
  def panel_=(value: Option[Panel]): Unit = storedPanel = value

  /** Whether this unit has no health left. */
  def isDefeated: Boolean = hitPoints == 0

  /** Points needed to complete this unit's action bar. */
  def actionBarMaximum: Double = weight.toDouble

  /** Computes physical attack damage for this unit. */
  def calculatePhysicalAttackDamage(): Int

  /** Dispatches equipping a weapon from this unit. */
  def executeEquipWeapon(action: EquipWeapon, target: Target): Unit =
    throw new InvalidActionException(s"Unit '$name' cannot equip weapons.")

  /** Dispatches consuming a potion from this unit. */
  def executeConsumePotion(action: ConsumePotion, target: Target): Unit =
    throw new InvalidActionException(s"Unit '$name' cannot consume potions.")

  /** Dispatches black spell casting from this unit. */
  def castBlackSpell(spell: BlackSpell, target: Target): Unit =
    throw new InvalidActionException(s"Unit '$name' cannot cast black magic.")

  /** Dispatches white spell casting from this unit. */
  def castWhiteSpell(spell: WhiteSpell, target: Target): Unit =
    throw new InvalidActionException(s"Unit '$name' cannot cast white magic.")

  override def findAction(actionName: String): Option[Action] =
    actions.find(_.name.equalsIgnoreCase(actionName))

  override def doAction(action: Action, target: Target): Unit =
    if isDefeated then
      throw new DefeatedUnitException(s"Defeated unit '$name' cannot perform actions.")
    action.execute(this, target)

  override def receiveAttack(attacker: GameUnit, attackDamage: Int): Unit =
    if isDefeated then
      throw new DefeatedTargetException(s"Defeated unit '$name' cannot receive actions.")
    val actualDamage = math.max(0, attackDamage - defense)
    hitPoints = math.max(0, hitPoints - actualDamage)

  override def receiveThunder(caster: MagicCharacter, magicDamage: Int): Unit =
    if isDefeated then
      throw new DefeatedTargetException(s"Defeated unit '$name' cannot receive actions.")
    hitPoints = math.max(0, hitPoints - magicDamage)

  override def receiveMove(unit: GameUnit): Unit =
    throw new InvalidTargetException("Units cannot be movement destinations.")

  override def receiveMeteor(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Meteor must target a panel.")

  override def receiveEquipWeapon(character: Character, weapon: Weapon): Unit =
    throw new InvalidTargetException("Cannot equip weapon onto another unit.")

  override def receiveConsumePotion(character: Character, potion: Potion): Unit =
    throw new InvalidTargetException("Cannot consume potion onto another unit.")
