package cl.uchile.dcc.model.units

/** An enemy with its own attack points and no weapon slot. */
class Enemy(name: String, hitPoints: Int, val attack: Int, defense: Int, weight: Int)
    extends GameUnit(name, hitPoints, defense, weight)
