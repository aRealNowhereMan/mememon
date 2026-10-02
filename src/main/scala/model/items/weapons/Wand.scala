package cl.uchile.dcc.model.items.weapons

/** A wand with physical and magical attack points. */
class Wand(name: String, attack: Int, weight: Int, magicAttack: Int)
    extends MagicWeapon(name, attack, weight, magicAttack)
