package cl.uchile.dcc.model.actions

/** A named spell with a mana cost. */
abstract class Spell(val name: String, val manaCost: Int) extends Action
