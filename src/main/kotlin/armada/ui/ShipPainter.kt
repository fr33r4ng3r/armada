package armada.ui

import armada.WiringHarness
import armada.engine.ships.*
import armada.ui.sprites.*
import javafx.scene.canvas.GraphicsContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
@DelicateCoroutinesApi
class ShipPainter {

    private val ships = mapOf(
        BattleShip::class to BattleshipSprite,
        Carrier::class to CarrierSprite,
        Destroyer::class to DestroyerSprite,
        Cruiser::class to CruiserSprite,
        PatrolBoat::class to PatrolBoatSprite
    )

    fun paint(gc: GraphicsContext, gridSize: Double) {
        val theatre = WiringHarness.theatre
        @Suppress("UNCHECKED_CAST")
        theatre.ships.forEach { ship ->
            val pos = theatre.positionOf(ship) ?: return@forEach
            ships[ship::class]?.draw(gc, pos.x, pos.y, pos.orientation.toDegrees(), gridSize)
        }
    }
}