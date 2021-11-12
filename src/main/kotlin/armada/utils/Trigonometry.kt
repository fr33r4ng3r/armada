package armada.utils

import kotlin.math.atan2

object Trigonometry {

    fun deltaDegrees(originX: Int, originY: Int, fromX: Int, fromY: Int, toX: Int, toY: Int): Double {
        val deltaXStart = (originX - fromX).toDouble()
        val deltaYStart = (originY - fromY).toDouble()
        val radStart = atan2(deltaYStart, deltaXStart);
        val deltaXEnd = (originX - toX).toDouble()
        val deltaYEnd = (originY - toY).toDouble()
        val radEnd = atan2(deltaYEnd, deltaXEnd);
        val deltaRads = radEnd - radStart
        var deltaDegress = deltaRads * (180 / Math.PI)
        if (deltaDegress > 180) deltaDegress = 180 - deltaDegress
        if (deltaDegress < -180) deltaDegress = 180 + deltaDegress
        return deltaDegress
    }
}