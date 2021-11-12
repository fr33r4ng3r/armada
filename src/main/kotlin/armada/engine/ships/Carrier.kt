package armada.engine.ships

import armada.engine.Dimensions
import armada.engine.Ship

class Carrier: Ship(dimensions, 2) {
    companion object {
        val dimensions = Dimensions(2, 4)
    }
}