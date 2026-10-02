package cl.uchile.dcc.model.items.potions

import cl.uchile.dcc.model.items.Usable

/** A named potion; its effects will be implemented in a later delivery. */
abstract class Potion(val name: String) extends Usable
