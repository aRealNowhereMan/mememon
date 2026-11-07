package cl.uchile.dcc.model.exceptions

/** Base exception for all domain errors in the game. */
abstract class GameException(message: String, cause: Throwable = null)
    extends Exception(message, cause)

/** Thrown when a unit attempts to move to a non-adjacent panel. */
class InvalidMovementException(message: String) extends GameException(message)

/** Thrown when a defeated unit attempts to perform an action. */
class DefeatedUnitException(message: String) extends GameException(message)

/** Thrown when a magic character lacks enough mana to cast a spell. */
class NotEnoughManaException(message: String) extends GameException(message)

/** Thrown when an action targets a defeated unit. */
class DefeatedTargetException(message: String) extends GameException(message)

/** Thrown when an action targets an incompatible entity. */
class InvalidTargetException(message: String) extends GameException(message)

/** Thrown when a character attempts a physical attack without an equipped weapon. */
class UnarmedAttackException(message: String) extends GameException(message)

/** Thrown when a magic character attempts to cast a spell without an equipped magic weapon. */
class NonMagicalWeaponException(message: String) extends GameException(message)

/** Thrown when a character attempts to equip a forbidden weapon class. */
class InvalidWeaponException(message: String) extends GameException(message)

/** Thrown when an action cannot be performed due to general game rules. */
class InvalidActionException(message: String) extends GameException(message)
