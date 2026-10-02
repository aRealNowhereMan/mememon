package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.items.Usable

/** An action with a typed list of available items. */
abstract class ItemAction[A <: Usable](val name: String, val items: List[A]) extends Action
