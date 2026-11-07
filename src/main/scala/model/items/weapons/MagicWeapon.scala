package cl.uchile.dcc.model.items.weapons

/** A weapon that additionally provides magical attack power. */
abstract class MagicWeapon(name: String, attack: Int, weight: Int, val magicAttack: Int)
    extends Weapon(name, attack, weight):
  override def isMagic: Boolean = true
  override def magicAttackValue: Int = magicAttack
