package armada.engine.ships

import armada.engine.Dimensions
import armada.engine.Ship

class Destroyer: Ship(dimensions, 2) {
    companion object {
        val dimensions = Dimensions(1, 3)
    }
}