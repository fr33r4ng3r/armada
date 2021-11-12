package armada.engine

import armada.engine.ships.*
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*
import java.util.Collections.synchronizedList
import kotlin.collections.HashMap
import kotlin.random.Random

@DelicateCoroutinesApi
class Theatre(val grid: BattleGrid, val battery: Battery, val satellite: Satellite) {

    val ships = arrayOf(
        BattleShip(),
        Carrier(),
        Cruiser(),
        Destroyer(),
        PatrolBoat()
    )

    val projectiles = synchronizedList(LinkedList<Projectile>())

    private val map = HashMap<Ship, Position>()

    private val rnd = Random(System.nanoTime())

    fun positionOf(ship: Ship): Position? {
        return map[ship]
    }

    fun randomize() {
        ships.forEach { ship ->
            while (true) {
                val orientation = rnd.nextInt(0, 2).let { i -> Orientation.values()[i] }
                var x = rnd.nextInt(0, grid.width)
                var y = rnd.nextInt(0, grid.height)
                when (orientation) {
                    Orientation.vertical -> {
                        val offsetX = grid.width - (x + ship.dimensions.width)
                        if (offsetX < 0) {
                            x += offsetX
                        }
                        val offsetY = grid.height - (y + ship.dimensions.length)
                        if (offsetY < 0) {
                            y += offsetY
                        }
                    }
                    Orientation.horizontal -> {
                        val offsetX = grid.width - (x + ship.dimensions.length)
                        if (offsetX < 0) {
                            x += offsetX
                        }
                        val offsetY = grid.height - (y + ship.dimensions.width)
                        if (offsetY < 0) {
                            y += offsetY
                        }
                    }
                }
                if (checkOverlapping(ship, x, y, orientation)) {
                    continue
                }
                occupySpace(ship, x, y, orientation)
                map[ship] = Position(x, y, orientation)
                break
            }
        }
    }

    private fun occupySpace(ship: Ship, x: Int, y: Int, orientation: Orientation) {
        (0 until ship.dimensions.width).forEach { wU ->
            (0 until ship.dimensions.length).forEach { lU ->
                when (orientation) {
                    Orientation.vertical -> {
                        grid.damageLayer[x + wU][y + lU].content = ship.hull[wU][lU]
                    }
                    Orientation.horizontal -> {
                        grid.damageLayer[x + lU][y + wU].content = ship.hull[wU][lU]
                    }
                }
            }
        }
    }

    private fun checkOverlapping(ship: Ship, x: Int, y: Int, orientation: Orientation): Boolean {
        (0 until ship.dimensions.width).forEach { wU ->
            (0 until ship.dimensions.length).forEach { lU ->
                when (orientation) {
                    Orientation.vertical -> {
                        if (grid.damageLayer[x + wU][y + lU].content != null) {
                            return true
                        }
                    }
                    Orientation.horizontal -> {
                        if (grid.damageLayer[x + lU][y + wU].content != null) {
                            return true
                        }
                    }
                }
            }
        }
        return false
    }

    companion object {
        const val GRID_SQUARE_WORLD_DISTANCE = 100.0
    }
}