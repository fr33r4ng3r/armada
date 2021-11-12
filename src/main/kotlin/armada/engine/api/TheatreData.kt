package armada.engine.api

data class TheatreData(val gridWidth: Int, val gridHeight: Int, val numberOfShips: Int, val ships: List<ShipData>) {
    data class ShipData(val descriptor: String, val width: Int, val length: Int)
}
