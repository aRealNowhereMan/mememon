package cl.uchile.dcc.model.items.weapons

/** A staff with physical and magical attack points. */
class Staff(name: String, attack: Int, weight: Int, magicAttack: Int)
    extends MagicWeapon(name, attack, weight, magicAttack)
