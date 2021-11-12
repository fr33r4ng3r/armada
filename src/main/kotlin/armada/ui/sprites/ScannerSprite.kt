package armada.ui.sprites

import armada.utils.ImageUtils.fade
import armada.utils.ImageUtils.makeSquare
import armada.utils.ImageUtils.scaleToWidth
import armada.utils.ImageUtils.toFXImage
import javafx.scene.canvas.GraphicsContext
import javax.imageio.ImageIO

object ScannerSprite {

    private val base = ImageIO.read(javaClass.getResourceAsStream("/armada/sprites/Effects/Glow.png"))
    private val base0 = (1..5).map { base.makeSquare().scaleToWidth(256.0).fade(0.20 - (it * 0.04)).toFXImage() }

    fun draw(gc: GraphicsContext, x: Int, y: Int, signal: Int, gridSize: Double) {
        val inset = -gridSize / 2
        gc.drawImage(base0[signal], x * gridSize + inset, y * gridSize + inset, gridSize - inset * 2, gridSize - inset * 2)
    }

}