package cl.uchile.dcc.model.units

import cl.uchile.dcc.model.actions.{Spell, Target}
import cl.uchile.dcc.model.exceptions.{NonMagicalWeaponException, NotEnoughManaException}
import cl.uchile.dcc.model.items.potions.{MagicStrengthPotion, ManaPotion}

/** A character that possesses mana points and the ability to cast spells. */
abstract class MagicCharacter(
    name: String,
    initialHitPoints: Int,
    initialDefense: Int,
    weight: Int,
    val maxMana: Int
) extends Character(name, initialHitPoints, initialDefense, weight):
  private var currentMana: Int = maxMana
  private var magicStrengthActive: Boolean = false

  /** Current mana points. */
  def mana: Int = currentMana

  /** Sets current mana, between zero and the initial maximum. */
  def mana_=(value: Int): Unit =
    require(value >= 0 && value <= maxMana)
    currentMana = value

  /** Whether the magic strength potion effect is currently active. */
  def isMagicStrengthActive: Boolean = magicStrengthActive

  /** Consumes the magic strength bonus multiplier (1.5 if active, 1.0 otherwise). */
  def consumeMagicStrengthBonus(): Double =
    if magicStrengthActive then
      magicStrengthActive = false
      1.5
    else
      1.0

  override def consumeManaPotion(potion: ManaPotion): Unit =
    val restored = math.max(1, (maxMana * 0.30).toInt)
    mana = math.min(maxMana, mana + restored)

  override def consumeMagicStrengthPotion(potion: MagicStrengthPotion): Unit =
    magicStrengthActive = true

  /** Casts a magical spell checking mana and weapon requirements. */
  def castSpell(spell: Spell, target: Target): Unit =
    weapon match
      case Some(w) if w.isMagic =>
        if mana < spell.manaCost then
          throw new NotEnoughManaException(
            s"Magic character '$name' does not have enough mana ($mana / ${spell.manaCost})."
          )
        mana = mana - spell.manaCost
        val baseDamage = w.magicAttackValue
        val multiplier = consumeMagicStrengthBonus()
        val effectiveDamage = (baseDamage * multiplier).toInt
        spell.applySpellEffect(this, target, effectiveDamage)
      case _ =>
        throw new NonMagicalWeaponException(
          s"Magic character '$name' must have an equipped magic weapon to cast spells."
        )
