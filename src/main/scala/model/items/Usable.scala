package cl.uchile.dcc.model.items

/** An item that can be stored in a character's inventory. */
trait Usable:
  /** Display name of this item. */
  def name: String
