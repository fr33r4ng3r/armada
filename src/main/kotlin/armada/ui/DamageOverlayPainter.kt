package armada.ui

import armada.WiringHarness
import armada.engine.Ship
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.time.ExperimentalTime

@ExperimentalTime
class DamageOverlayPainter {

    fun paint(gc: GraphicsContext, gridSize: Double) {
        val grid = WiringHarness.battleGrid
        grid.damageLayer.forEach { row ->
            row.forEach { tile ->
                when(tile.content) {
                    is Ship.Companion.HullSegment -> {
                        if ((tile.content as Ship.Companion.HullSegment).health.get() > 0) {
                            gc.fill = Color.web("#33cc33", 0.25)
                        } else {
                            gc.fill = Color.web("#cc0000", 0.25)
                        }
                        gc.fillRect(tile.x * gridSize, tile.y * gridSize, gridSize, gridSize)
                    }
                }
            }
        }
    }
}