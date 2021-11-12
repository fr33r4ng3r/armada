package armada.engine

import java.util.concurrent.atomic.AtomicInteger

abstract class Ship(val dimensions: Dimensions, private val hullStart: Int) {

    val hull = (0 until dimensions.width).map { Array(dimensions.length) { HullSegment(AtomicInteger(hullStart)) } }.toTypedArray()

    companion object {
        class HullSegment(val health: AtomicInteger) : Tile.Companion.Content
    }
}