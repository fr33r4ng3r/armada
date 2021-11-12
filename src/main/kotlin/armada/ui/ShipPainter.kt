package armada.ui

import armada.WiringHarness
import armada.ui.sprites.*
import javafx.scene.canvas.GraphicsContext

class ShipPainter {

    private val ships = arrayOf(
        BattleshipSprite,
        CarrierSprite,
        DestroyerSprite,
        CruiserSprite,
        PatrolBoatSprite
    )

    fun paint(gc: GraphicsContext, gridSize: Double) {
        val theatre = WiringHarness.theatre
        @Suppress("UNCHECKED_CAST")
        ships.forEach { ship ->
            val pos = theatre.positionOf(ship.engineObject) ?: return@forEach
            ship.draw(gc, pos.x, pos.y, pos.orientation.toDegrees(), gridSize)
        }
    }
}