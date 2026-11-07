package cl.uchile.dcc.model.items.weapons

import cl.uchile.dcc.model.actions.Target
import cl.uchile.dcc.model.exceptions.InvalidTargetException
import cl.uchile.dcc.model.items.Usable
import cl.uchile.dcc.model.items.potions.Potion
import cl.uchile.dcc.model.units.{Character, GameUnit, MagicCharacter}

/** Shared weapon statistics, optional ownership and target behavior. */
abstract class Weapon(val name: String, val attack: Int, val weight: Int)
    extends Usable with Target:
  private var storedOwner: Option[Character] = None

  /** Character that owns this weapon, if any. */
  def owner: Option[Character] = storedOwner

  /** Stores ownership of this weapon. */
  def owner_=(value: Option[Character]): Unit = storedOwner = value

  /** Equips this weapon onto the given character via double dispatch. */
  def equipTo(character: Character): Unit

  /** Whether this weapon can channel magical spells. */
  def isMagic: Boolean = false

  /** The magical attack value provided by this weapon. */
  def magicAttackValue: Int = 0

  override def asWeapon: Option[Weapon] = Some(this)

  override def asPotion: Option[Potion] = None

  override def receiveAttack(attacker: GameUnit, attackDamage: Int): Unit =
    throw new InvalidTargetException("Weapons cannot receive physical attacks.")

  override def receiveMove(unit: GameUnit): Unit =
    throw new InvalidTargetException("Weapons cannot be movement destinations.")

  override def receiveThunder(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Weapons cannot receive thunder spells.")

  override def receiveMeteor(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Weapons cannot receive meteor spells.")

  override def receiveHealing(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Weapons cannot receive healing spells.")

  override def receivePurification(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Weapons cannot receive purification spells.")

  override def receiveEquipWeapon(character: Character, weapon: Weapon): Unit =
    character.equip(this)

  override def receiveConsumePotion(character: Character, potion: Potion): Unit =
    throw new InvalidTargetException("Weapons cannot be consumed as potions.")
