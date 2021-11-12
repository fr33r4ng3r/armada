package armada.ui.sprites

import armada.utils.ImageUtils.tile
import armada.utils.ImageUtils.toFXImage
import javafx.scene.canvas.GraphicsContext
import javax.imageio.ImageIO

object ExplosionSprite {

    private val base = ImageIO.read(javaClass.getResourceAsStream("/armada/sprites/Effects/Fire.png"))
    private val baseQ = base.tile(4, 5).map { it.toFXImage() }

    fun draw(gc: GraphicsContext, x: Int, y: Int, frame: Int, gridSize: Double) {
        gc.drawImage(baseQ[frame], x * gridSize, y * gridSize, gridSize, gridSize)
    }

}