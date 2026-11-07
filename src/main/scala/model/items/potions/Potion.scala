package cl.uchile.dcc.model.items.potions

import cl.uchile.dcc.model.actions.Target
import cl.uchile.dcc.model.exceptions.InvalidTargetException
import cl.uchile.dcc.model.items.Usable
import cl.uchile.dcc.model.items.weapons.Weapon
import cl.uchile.dcc.model.units.{Character, GameUnit, MagicCharacter}

/** A named potion that can apply an effect to a character. */
abstract class Potion(val name: String) extends Usable with Target:
  /** Applies this potion's specific effect onto the consuming character. */
  def applyEffect(character: Character): Unit

  override def asWeapon: Option[Weapon] = None

  override def asPotion: Option[Potion] = Some(this)

  override def receiveAttack(attacker: GameUnit, attackDamage: Int): Unit =
    throw new InvalidTargetException("Potions cannot receive physical attacks.")

  override def receiveMove(unit: GameUnit): Unit =
    throw new InvalidTargetException("Potions cannot be movement destinations.")

  override def receiveThunder(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Potions cannot receive thunder spells.")

  override def receiveMeteor(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Potions cannot receive meteor spells.")

  override def receiveHealing(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Potions cannot receive healing spells.")

  override def receivePurification(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Potions cannot receive purification spells.")

  override def receiveEquipWeapon(character: Character, weapon: Weapon): Unit =
    throw new InvalidTargetException("Potions cannot be equipped as weapons.")

  override def receiveConsumePotion(character: Character, potion: Potion): Unit =
    character.consume(this)
