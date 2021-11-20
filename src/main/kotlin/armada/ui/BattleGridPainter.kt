package armada.ui

import armada.WiringHarness
import javafx.scene.canvas.GraphicsContext
import javafx.scene.paint.Color
import kotlin.math.min
import kotlin.time.ExperimentalTime

@ExperimentalTime
class BattleGridPainter {

    fun paint(gc: GraphicsContext, width: Double, height: Double): Double {
        val grid = WiringHarness.battleGrid
        val xInterval = width / grid.width
        val yInterval = height / grid.height
        val i = min(xInterval, yInterval)
        val xMax = grid.width * i
        val yMax = grid.height * i

        gc.stroke = Color.web("#FFFFFF", 0.01)
        (0..grid.width).forEach { x ->
            (0..grid.height).forEach { y ->
                gc.strokeLine(x * i, 0.0, x * i, yMax)
                gc.strokeLine(0.0, y * i, xMax, y * i)
            }
        }
        return i
    }

}