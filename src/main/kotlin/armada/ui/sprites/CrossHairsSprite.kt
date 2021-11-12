package armada.ui.sprites

import armada.utils.ImageUtils.scaleToWidth
import armada.utils.ImageUtils.toFXImage
import javafx.scene.canvas.GraphicsContext
import javax.imageio.ImageIO

object CrossHairsSprite : Sprite {

    private val base = ImageIO.read(javaClass.getResourceAsStream("/armada/sprites/Effects/CrossHairs.png"))
    private val base0 = base.scaleToWidth(256.0).toFXImage()

    override fun draw(gc: GraphicsContext, x: Int, y: Int, rotation: Double, gridSize: Double) {
        val inset = gridSize / 4
        gc.drawImage(base0, x * gridSize + inset, y * gridSize + inset, gridSize - inset * 2, gridSize - inset * 2)
    }

}