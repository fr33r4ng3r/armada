package armada.ui

import armada.WiringHarness
import armada.ui.sprites.TurretSprite
import javafx.scene.canvas.GraphicsContext
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.time.ExperimentalTime

@ExperimentalTime
@DelicateCoroutinesApi
class TurretPainter {

    fun paint(gc: GraphicsContext, gridSize: Double) {
        val battery = WiringHarness.battery

        battery.turrets.forEach { turret ->
            TurretSprite.draw(gc, turret.x, turret.y, turret.rotation, gridSize)
        }
    }
}