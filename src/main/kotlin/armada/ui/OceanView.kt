package armada.ui

import javafx.scene.canvas.Canvas
import javafx.scene.image.Image
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlin.time.ExperimentalTime


@ExperimentalTime
@DelicateCoroutinesApi
class OceanView : Canvas() {

    private val water = Image(javaClass.getResourceAsStream("/armada/tiles/water.png"))
    val gridPainter = BattleGridPainter()
    val shipPainter = ShipPainter()
    val turretPainter = TurretPainter()
    val damagePainter = DamageOverlayPainter()
    val effectsPainter = EffectsPainter()
    val actionsPainter = ActionsOverlayPainter()
    val projectilePainter = ProjectilePainter()
    val satelliteOverlayPainter = SatelliteOverlayPainter()

    init {
        widthProperty().addListener { _ -> draw() }
        heightProperty().addListener { _ -> draw() }
    }

    fun draw() {
        val width = width
        val height = height
        val gc = graphicsContext2D
        gc.clearRect(0.0, 0.0, width, height)
        gc.drawImage(water, 0.0, 0.0, width, height)

        val gridSize = gridPainter.paint(gc, width, height)
        shipPainter.paint(gc, gridSize)
        turretPainter.paint(gc, gridSize)
        damagePainter.paint(gc, gridSize)
        effectsPainter.paint(gc, gridSize)
        actionsPainter.paint(gc, gridSize)
        projectilePainter.paint(gc, gridSize)
        satelliteOverlayPainter.paint(gc, gridSize)
    }

    override fun isResizable(): Boolean {
        return true
    }

    override fun prefWidth(height: Double): Double {
        return width
    }

    override fun prefHeight(width: Double): Double {
        return height
    }
}