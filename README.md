# Proyecto Semestral: Final Reality Tactics

## Resumen del Proyecto y Avance hasta EF2 (Tarea 2)

Este proyecto implementa la lógica de combate táctico por turnos inspirada en *Final Fantasy Tactics*, desarrollada en Scala 3 aplicando metodologías avanzadas de diseño orientado a objetos (**MVC**, **Double Dispatch**, principios **SOLID**).

A continuación se detalla la arquitectura, decisiones de diseño, patrones empleados y cumplimiento estricto de las especificaciones de las entregas **EP3**, **EP4** y **EF2**.

---

## 1. Organización del Código

El proyecto está organizado en paquetes desacoplados bajo `cl.uchile.dcc`:

* `cl.uchile.dcc.controller`:
  * `GameController`: Motor lógico que conecta el modelo con el flujo del juego, administra jugadores, paneles, la unidad en turno y la ejecución segura de acciones.
* `cl.uchile.dcc.model.actions`:
  * `Source`: Contrato para entidades capaces de originar y ejecutar acciones.
  * `Target`: Contrato para entidades receptoras de acciones (unidades, paneles, utilizables).
  * `Action`: Contrato base de acción ejecutable.
  * `Attack`, `Move`, `EquipWeapon`, `ConsumePotion`.
  * `Spell`, `BlackSpell`, `WhiteSpell`, `Thunder`, `Meteor`, `Healing`, `Purification`.
* `cl.uchile.dcc.model.combat`:
  * `TurnScheduler`: Programador dinámico de turnos basado en peso y tiempo.
* `cl.uchile.dcc.model.exceptions`:
  * Jerarquía de excepciones de dominio: `GameException`, `InvalidMovementException`, `DefeatedUnitException`, `NotEnoughManaException`, `DefeatedTargetException`, `InvalidTargetException`, `UnarmedAttackException`, `NonMagicalWeaponException`, `InvalidWeaponException`, `InvalidActionException`.
* `cl.uchile.dcc.model.items`:
  * `Usable`: Contrato de ítem almacenable en inventario.
  * `weapons`: `Weapon`, `MagicWeapon`, `Sword`, `Dagger`, `Bow`, `Wand`, `Staff`.
  * `potions`: `Potion`, `HealingPotion`, `StrengthPotion`, `ManaPotion`, `MagicStrengthPotion`.
* `cl.uchile.dcc.model.map`:
  * `Panel`: Casilla del mapa con coordenadas, almacén de unidades, vecinos ortogonales y receptor de movimiento / meteoritos.
* `cl.uchile.dcc.model.units`:
  * `GameUnit`: Clase abstracta base que implementa `Source` y `Target`.
  * `Character`: Personaje con inventario, armas y doble dispatch para equipamiento.
  * `MagicCharacter`: Personaje mágico con maná y hechizos.
  * `Knight`, `Archer`, `Thief`, `BlackMage`, `WhiteMage`, `Enemy`.

---

## 2. Patrones de Diseño y Decisiones de Arquitectura

### A. Double Dispatch y Eliminación Total de `isInstanceOf` / `getClass`
El curso exige expresamente: *"No uses isInstanceOf/getClass para elegir comportamiento fuera de equals en los ejercicios del ramo"*. El desafío central de la Tarea 2 consistía en desambiguar la tríada dinámica: `(Source, Action, Target)`.

Para lograr esto sin inspección de tipos ni *downcasting*:
1. **Primer Dispatch (Invocación):**
   `source.doAction(action, target)` verifica que el source esté con vida y delega en `action.execute(source, target)`.
2. **Segundo Dispatch (Acción a Target / Source):**
   La acción conoce su tipo específico y delega en el método apropiado del `Target` o del `Source`:
   * `Attack` solicita a `source.calculatePhysicalAttackDamage()` el daño físico (un personaje verifica arma y extrae ataque; un enemigo retorna su ataque innato) y luego invoca `target.receiveAttack(source, damage)`.
   * `Move` invoca directamente `target.receiveMove(source)`. Si el target es un `Panel`, este valida que sea adyacente al panel actual de la unidad y actualiza su posición.
   * `EquipWeapon` y `ConsumePotion` delegan a `source.executeEquipWeapon` y `source.executeConsumePotion`, invocando `target.receiveEquipWeapon` o `target.receiveConsumePotion`. Si el objetivo es el arma/poción o el personaje mismo, se resuelven polimórficamente.
   * `BlackSpell` y `WhiteSpell` delegan en `source.castBlackSpell` y `source.castWhiteSpell`, garantizando que magos negros solo lancen magia negra y magos blancos solo magia blanca.
3. **Tercer Dispatch (Compatibilidad de Armas y Efectos de Pociones):**
   * Al equipar un arma, `character.equip(weapon)` llama a `weapon.equipTo(character)`. Cada subclase de arma (`Sword`, `Dagger`, `Bow`, `Wand`, `Staff`) invoca el método correspondiente del personaje (`equipSword`, `equipBow`, etc.). Las clases que no permiten dicha arma lanzan `InvalidWeaponException`.
   * Al consumir una poción, `potion.applyEffect(character)` llama al método especializado (`consumeHealingPotion`, `consumeStrengthPotion`, `consumeManaPotion`, `consumeMagicStrengthPotion`), aplicando restauraciones según si el personaje es común o mágico.

### B. Arquitectura MVC (Modelo - Vista - Controlador)
* Las entidades del modelo (`GameUnit`, `Panel`, `Action`, `TurnScheduler`, etc.) son completamente independientes y no tienen ninguna referencia al `GameController`.
* El `GameController` centraliza el control del juego:
  * Lleva el registro de jugadores, paneles y el `TurnScheduler`.
  * Determina la unidad del turno activo (`currentUnit`).
  * Avanza los turnos mediante `decideNextTurn(step)`.
  * Al ejecutar `executeAction(action, target)`:
    * Si la acción tiene éxito, limpia unidades derrotadas del combate, reinicia la barra de acción de la unidad en el scheduler y avanza al siguiente turno.
    * Si la acción falla (lanza una excepción de juego), la captura en `lastError` para su diagnóstico y **no altera el turno ni reinicia la barra de acción de la unidad**, permitiendo al jugador reintentar otra acción válida.

---

## 3. Restricciones y Excepciones de Dominio (EP4)

Se implementaron rigurosamente las 9 restricciones solicitadas mediante excepciones personalizadas:
1. **Movimiento adyacente:** Moverse a un panel que no sea vecino ortogonal lanza `InvalidMovementException`.
2. **Vida positiva:** Una unidad derrotada (`hitPoints == 0`) no puede ejecutar acciones; lanza `DefeatedUnitException`.
3. **Maná suficiente:** Castear un hechizo sin tener los puntos de maná requeridos lanza `NotEnoughManaException`.
4. **Objetivo no derrotado:** Una unidad derrotada no puede recibir acciones; lanza `DefeatedTargetException`.
5. **Purificación exclusiva a enemigos:** Aplicar `Purification` sobre un personaje o panel lanza `InvalidTargetException`.
6. **Curación exclusiva a personajes:** Aplicar `Healing` sobre un enemigo o panel lanza `InvalidTargetException`.
7. **Ataque físico armado:** Un personaje sin arma equipada que intente atacar lanza `UnarmedAttackException`.
8. **Hechizo con arma mágica:** Un personaje mágico sin arma mágica equipada (`Wand` o `Staff`) que intente lanzar un hechizo lanza `NonMagicalWeaponException`.
9. **Cuadro de armas equipables:**
   * **Caballero (Knight):** Espada, Daga, Arco (Varita y Bastón lanzan `InvalidWeaponException`).
   * **Arquero (Archer):** Espada, Arco, Varita (Daga y Bastón lanzan `InvalidWeaponException`).
   * **Ladrón (Thief):** Espada, Daga (Arco, Varita y Bastón lanzan `InvalidWeaponException`).
   * **Mago Negro (Black Mage):** Daga, Varita, Bastón (Espada y Arco lanzan `InvalidWeaponException`).
   * **Mago Blanco (White Mage):** Arco, Varita, Bastón (Espada y Daga lanzan `InvalidWeaponException`).
   * **Enemigos:** No pueden equipar armas ni consumir pociones (`InvalidActionException`).

---

## 4. Pruebas Unitarias y Cobertura (MUnit)

El proyecto cuenta con un conjunto exhaustivo de pruebas unitarias implementadas con MUnit que cubren casos de uso esperados, límites numéricos, casos de borde y todas las restricciones con `intercept[...]`:

* `UnitsTest`: Inicialización de unidades, inventarios, derrotas y límites de vida/maná.
* `ItemsTest`: Propiedades de las 5 armas y 4 pociones.
* `PanelTest`: Coordenadas, vecindad ortogonal bidireccional y manejo de unidades.
* `TurnSchedulerTest`: Progresión de barras, excedente de turnos, desempates y reinicios.
* `DoubleDispatchActionsTest`: Ejecución completa de las 8 acciones mediante Double Dispatch.
* `GameControllerTest`: Flujo de turnos, registro de jugadores, captura de excepciones y conservación de turnos.
* `RestrictionsAndExceptionsTest`: Validación rigurosa de las 9 restricciones de EP4.
* `EdgeCasesAndCoverageTest`: Casos de borde de objetivos inválidos y compatibilidad general.

### Ejecución de Pruebas

Para ejecutar la suite completa de pruebas desde el directorio del proyecto:

```bash
sbt test
```

---

## 5. Historial de Ramas Git

Siguiendo las pautas del proyecto, el trabajo se dividió ordenadamente en ramas:
* `main`: Código base consolidado.
* `entrega-final-1`: Entrega Final 1 (Tarea 1).
* `entrega-parcial-3`: Entrega Parcial 3 (Sistema de acciones, Source, Target, Double Dispatch y GameController).
* `entrega-parcial-4`: Entrega Parcial 4 (Restricciones, excepciones y captura de errores en controlador).
* `entrega-final-2`: Entrega Final 2 (Tarea 2 completa, documentación exhaustiva y README final).
