package armada.ui

import armada.WiringHarness
import armada.engine.Battery
import armada.ui.sprites.CrossHairsSprite
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class ActionsOverlayPainter {

    fun paint(gc: GraphicsContext, gridSize: Double) {
        val grid = WiringHarness.battleGrid
        val gZone = gridSize / 10
        grid.actionsLayer.forEach { row ->
            row.forEach { tile ->
                when (tile.content) {
                    is Battery.Companion.TargetAction -> {
                        val z = (tile.content as Battery.Companion.TargetAction).zone * gZone
                        val cX = (tile.x * gridSize) + (gridSize / 2)
                        val cY = (tile.y * gridSize) + (gridSize / 2)
                        gc.stroke = Color.web("#ff9900", 0.75)
                        gc.strokeOval(cX - (z / 2), cY - (z / 2), z, z)
                    }
                    is Battery.Companion.TargetLocked -> {
                        CrossHairsSprite.draw(gc, tile.x, tile.y, 0.0, gridSize)
//                        val z = 5 * gZone
//                        val cX = (tile.x * gridSize) + (gridSize / 2)
//                        val cY = (tile.y * gridSize) + (gridSize / 2)
//                        gc.fill = Color.web("#ff9900", 0.5)
//                        gc.fillOval(cX - (z / 2), cY - (z / 2), z, z)
//                        gc.stroke = Color.web("#ff9900", 0.75)
//                        gc.strokeLine(cX - gridSize / 2, cY, cX + gridSize / 2, cY)
//                        gc.strokeLine(cX, cY - gridSize / 2, cX, cY + gridSize / 2)
                    }
                }
            }
        }
    }
}