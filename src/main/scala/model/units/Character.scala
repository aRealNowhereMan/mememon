package cl.uchile.dcc.model.units

import cl.uchile.dcc.model.actions.{Action, Attack, ConsumePotion, EquipWeapon, Move, Target}
import cl.uchile.dcc.model.exceptions.{DefeatedTargetException, InvalidActionException, InvalidTargetException, InvalidWeaponException, UnarmedAttackException}
import cl.uchile.dcc.model.items.Usable
import cl.uchile.dcc.model.items.potions.{HealingPotion, MagicStrengthPotion, ManaPotion, Potion, StrengthPotion}
import cl.uchile.dcc.model.items.weapons.{Bow, Dagger, Staff, Sword, Wand, Weapon}

/** A character with weapon slot, personal inventory and equipment rules. */
abstract class Character(name: String, initialHitPoints: Int, initialDefense: Int, weight: Int)
    extends GameUnit(name, initialHitPoints, initialDefense, weight):
  private var storedWeapon: Option[Weapon] = None
  private var storedInventory: List[Usable] = List.empty

  /** Currently equipped weapon, if any. */
  def weapon: Option[Weapon] = storedWeapon

  /** Stores a weapon slot value directly. */
  def weapon_=(value: Option[Weapon]): Unit = storedWeapon = value

  /** An immutable snapshot of the inventory. */
  def inventory: List[Usable] = storedInventory

  /** Adds an item to the end of the inventory. */
  def addItem(item: Usable): Unit = storedInventory = storedInventory :+ item

  /** Removes an item from the inventory, if present. */
  def removeItem(item: Usable): Unit =
    storedInventory = storedInventory.filterNot(_ == item)

  /** List of weapons currently stored in the inventory. */
  def weaponsInInventory: List[Weapon] = storedInventory.flatMap(_.asWeapon)

  /** List of potions currently stored in the inventory. */
  def potionsInInventory: List[Potion] = storedInventory.flatMap(_.asPotion)

  /** Available actions for this character. */
  override def actions: List[Action] =
    List(
      new Attack(),
      new Move(),
      new EquipWeapon(weaponsInInventory),
      new ConsumePotion(potionsInInventory)
    )

  /** Weight calculation including half the weight of the equipped weapon. */
  override def actionBarMaximum: Double =
    weight + weapon.map(_.weight / 2.0).getOrElse(0.0)

  override def calculatePhysicalAttackDamage(): Int =
    weapon match
      case Some(w) => w.attack
      case None =>
        throw new UnarmedAttackException(s"Character '$name' cannot attack without an equipped weapon.")

  override def executeEquipWeapon(action: EquipWeapon, target: Target): Unit =
    val weaponToEquip = action.items.headOption.orNull
    target.receiveEquipWeapon(this, weaponToEquip)

  override def executeConsumePotion(action: ConsumePotion, target: Target): Unit =
    val potionToConsume = action.items.headOption.orNull
    target.receiveConsumePotion(this, potionToConsume)

  override def receiveEquipWeapon(character: Character, weaponToEquip: Weapon): Unit =
    if character == this then
      if weaponToEquip != null then equip(weaponToEquip)
      else throw new InvalidActionException("No weapon specified to equip.")
    else
      throw new InvalidTargetException("Cannot equip a weapon onto another character.")

  override def receiveConsumePotion(character: Character, potionToConsume: Potion): Unit =
    if character == this then
      if potionToConsume != null then consume(potionToConsume)
      else throw new InvalidActionException("No potion specified to consume.")
    else
      throw new InvalidTargetException("Cannot consume a potion onto another character.")

  /** Equips a weapon using double dispatch to enforce weapon compatibility. */
  def equip(newWeapon: Weapon): Unit =
    newWeapon.equipTo(this)

  def equipSword(sword: Sword): Unit =
    throw new InvalidWeaponException(s"Character '$name' cannot equip swords.")

  def equipDagger(dagger: Dagger): Unit =
    throw new InvalidWeaponException(s"Character '$name' cannot equip daggers.")

  def equipBow(bow: Bow): Unit =
    throw new InvalidWeaponException(s"Character '$name' cannot equip bows.")

  def equipWand(wand: Wand): Unit =
    throw new InvalidWeaponException(s"Character '$name' cannot equip wands.")

  def equipStaff(staff: Staff): Unit =
    throw new InvalidWeaponException(s"Character '$name' cannot equip staves.")

  protected def doEquip(newWeapon: Weapon): Unit =
    weapon.foreach(_.owner = None)
    newWeapon.owner = Some(this)
    weapon = Some(newWeapon)

  /** Consumes a potion from this character's inventory. */
  def consume(potion: Potion): Unit =
    if !inventory.contains(potion) then
      throw new InvalidActionException(s"Potion '${potion.name}' is not in inventory.")
    potion.applyEffect(this)
    removeItem(potion)

  def consumeHealingPotion(potion: HealingPotion): Unit =
    val healAmount = math.max(1, (maxHitPoints * 0.20).toInt)
    hitPoints = math.min(maxHitPoints, hitPoints + healAmount)

  def consumeStrengthPotion(potion: StrengthPotion): Unit =
    val bonus = math.max(1, (defense * 0.15).toInt)
    defense = defense + bonus

  def consumeManaPotion(potion: ManaPotion): Unit = ()

  def consumeMagicStrengthPotion(potion: MagicStrengthPotion): Unit = ()

  override def receiveHealing(caster: MagicCharacter, magicDamage: Int): Unit =
    if isDefeated then
      throw new DefeatedTargetException(s"Defeated character '$name' cannot receive actions.")
    val heal = math.max(1, (maxHitPoints * 0.20).toInt) + (magicDamage / 4)
    hitPoints = math.min(maxHitPoints, hitPoints + heal)

  override def receivePurification(caster: MagicCharacter, magicDamage: Int): Unit =
    throw new InvalidTargetException("Purification can only be received by enemies.")
