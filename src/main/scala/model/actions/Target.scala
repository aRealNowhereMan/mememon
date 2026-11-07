package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.items.potions.Potion
import cl.uchile.dcc.model.items.weapons.Weapon
import cl.uchile.dcc.model.units.{Character, GameUnit, MagicCharacter}

/** An entity capable of receiving actions. */
trait Target:
  /** Receives a physical attack from an attacker unit. */
  def receiveAttack(attacker: GameUnit, attackDamage: Int): Unit

  /** Receives movement from a unit attempting to displace. */
  def receiveMove(unit: GameUnit): Unit

  /** Receives the thunder spell effect. */
  def receiveThunder(caster: MagicCharacter, magicDamage: Int): Unit

  /** Receives the meteor spell effect. */
  def receiveMeteor(caster: MagicCharacter, magicDamage: Int): Unit

  /** Receives the healing spell effect. */
  def receiveHealing(caster: MagicCharacter, magicDamage: Int): Unit

  /** Receives the purification spell effect. */
  def receivePurification(caster: MagicCharacter, magicDamage: Int): Unit

  /** Receives a weapon equipment request. */
  def receiveEquipWeapon(character: Character, weapon: Weapon): Unit

  /** Receives a potion consumption request. */
  def receiveConsumePotion(character: Character, potion: Potion): Unit
