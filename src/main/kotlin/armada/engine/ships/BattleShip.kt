package armada.engine.ships

import armada.engine.Dimensions
import armada.engine.Ship

class BattleShip: Ship(dimensions, 2) {
    companion object {
        val dimensions = Dimensions(1, 4)
    }
}