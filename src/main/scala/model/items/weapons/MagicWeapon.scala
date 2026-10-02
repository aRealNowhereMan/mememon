package cl.uchile.dcc.model.items.weapons

/** A weapon with additional magical attack points. */
abstract class MagicWeapon(name: String, attack: Int, weight: Int, val magicAttack: Int)
    extends Weapon(name, attack, weight)
