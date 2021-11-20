package armada.ui.sprites

import armada.engine.Theatre
import armada.utils.ImageUtils.makeSquare
import armada.utils.ImageUtils.rotateNormalized
import armada.utils.ImageUtils.toFXImage
import javafx.scene.canvas.GraphicsContext
import javax.imageio.ImageIO
import kotlin.time.ExperimentalTime

@ExperimentalTime
object MissileSprite {

    private val base = ImageIO.read(javaClass.getResourceAsStream("/armada/sprites/Plane/Missile.png"))
    private val baseQ = base.makeSquare().rotateNormalized(-90.0)
    private val baseR = (0..359).map { a -> baseQ.rotateNormalized(a.toDouble()).toFXImage() }.toTypedArray()

    fun draw(gc: GraphicsContext, xWorld: Double, yWorld: Double, rotation: Double, gridSize: Double) {
        val r = (if (rotation >= 360) rotation - 360 else if (rotation < 0) rotation + 360 else rotation).toInt()
        val x = (xWorld / Theatre.GRID_SQUARE_WORLD_DISTANCE) * gridSize + gridSize / 3
        val y = (yWorld / Theatre.GRID_SQUARE_WORLD_DISTANCE) * gridSize + gridSize / 3
        gc.drawImage(baseR[r], x, y, gridSize / 3.0, gridSize / 3.0)
    }

}