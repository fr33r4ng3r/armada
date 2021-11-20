package armada.ui

import armada.WiringHarness
import armada.ui.sprites.MissileSprite
import javafx.scene.canvas.GraphicsContext
import kotlin.time.ExperimentalTime

@ExperimentalTime
class ProjectilePainter {

    fun paint(gc: GraphicsContext, gridSize: Double) {
        val theatre = WiringHarness.theatre
        ArrayList(theatre.projectiles).forEach { p ->
            MissileSprite.draw(gc, p.xWorld, p.yWorld, p.orientation, gridSize)
        }
    }
}