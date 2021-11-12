package armada.ui.sprites

import armada.utils.ImageUtils.makeSquare
import armada.utils.ImageUtils.rotate
import armada.utils.ImageUtils.rotateNormalized
import armada.utils.ImageUtils.scaleToWidth
import armada.utils.ImageUtils.toFXImage
import javafx.scene.canvas.GraphicsContext
import javax.imageio.ImageIO

object TurretSprite : Sprite {

    private val base = ImageIO.read(javaClass.getResourceAsStream("/armada/sprites/Turret/Turret.png"))
    private val baseScaled = base.makeSquare().scaleToWidth(256.0)
    private val baseR = (0..359).map { a -> baseScaled.rotateNormalized(a.toDouble()).toFXImage() }.toTypedArray()

    override fun draw(gc: GraphicsContext, x: Int, y: Int, rotation: Double, gridSize: Double) {
        val r = (if (rotation >= 360) rotation - 360 else if (rotation < 0) rotation + 360 else rotation).toInt()
        gc.drawImage(baseR[r], x * gridSize, y * gridSize, gridSize, gridSize)
    }

}