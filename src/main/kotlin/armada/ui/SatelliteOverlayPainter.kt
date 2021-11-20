package armada.ui

import armada.WiringHarness
import armada.engine.Satellite
import armada.ui.sprites.ScannerSprite
import javafx.scene.canvas.GraphicsContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
@DelicateCoroutinesApi
class SatelliteOverlayPainter {

    fun paint(gc: GraphicsContext, gridSize: Double) {
        val satellite = WiringHarness.satellite
        satellite.scansLayer.forEach { row ->
            row.forEach { tile ->
                when (tile.content) {
                    is Satellite.Companion.Scan -> {
                        val signal = (tile.content as? Satellite.Companion.Scan)?.signal
                        if (signal != null && signal > 0) {
                            ScannerSprite.draw(gc, tile.x, tile.y, signal, gridSize)
                        }
                    }
                }
            }
        }
    }
}