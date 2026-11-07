package cl.uchile.dcc.model.actions

import cl.uchile.dcc.model.items.potions.Potion
import cl.uchile.dcc.model.units.GameUnit

/** Describes and executes consuming one of the available potions. */
class ConsumePotion(items: List[Potion]) extends ItemAction[Potion]("Consume potion", items):
  def this(potion: Potion) = this(List(potion))
  def this() = this(Nil)

  override def execute(source: GameUnit, target: Target): Unit =
    source.executeConsumePotion(this, target)
