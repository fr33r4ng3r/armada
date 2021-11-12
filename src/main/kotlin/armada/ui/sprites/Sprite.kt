package armada.ui.sprites

import javafx.scene.canvas.GraphicsContext

interface Sprite {
    fun draw(gc: GraphicsContext, x: Int, y: Int, rotation: Double, gridSize: Double)
}