package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.items.Usable

/** An action associated with a typed list of usable items. */
abstract class ItemAction[A <: Usable](val name: String, val items: List[A]) extends Action
