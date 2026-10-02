package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.items.potions.Potion

/** Describes consuming one of the available potions. */
class ConsumePotion(items: List[Potion]) extends ItemAction[Potion]("Consume potion", items)
