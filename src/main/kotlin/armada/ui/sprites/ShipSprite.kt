package armada.ui.sprites

import armada.engine.*
import javafx.scene.canvas.GraphicsContext
import javafx.scene.image.Image

interface ShipSprite : Sprite, DimensionsProvider {

    data class Meta(
        val image0: Image,
        val image90: Image,
        val defaultOrientation: Orientation,
        val baseGridSize: Double,
    )

    fun meta(): Meta

    override fun draw(gc: GraphicsContext, x: Int, y: Int, rotation: Double, gridSize: Double) {

        val meta = meta()

        val drawWidth = when (meta.defaultOrientation) {
            Orientation.vertical -> dimensions.width
            Orientation.horizontal -> dimensions.length
        } * gridSize

        val drawHeight = when (meta.defaultOrientation) {
            Orientation.vertical -> dimensions.length
            Orientation.horizontal -> dimensions.width
        } * gridSize

        val fActual = (gridSize / meta.baseGridSize)
        val offsetX = (drawWidth - (fActual * meta.image0.width)) / 2
        val offsetY = (drawHeight - (fActual * meta.image0.height)) / 2
        val wActual = meta.image0.width * fActual
        val hActual = meta.image0.height * fActual

        if (Orientation.of(rotation) != meta.defaultOrientation) {
            val xActual = (x * gridSize) + offsetY
            val yActual = (y * gridSize) + offsetX
            gc.drawImage(meta.image90, xActual, yActual, hActual, wActual)
        } else {
            val xActual = (x * gridSize) + offsetX
            val yActual = (y * gridSize) + offsetY
            gc.drawImage(meta.image0, xActual, yActual, wActual, hActual)
        }
    }
}