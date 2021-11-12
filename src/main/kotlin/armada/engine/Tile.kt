package armada.engine

data class Tile(val x: Int, val y: Int) {
    var content: Content? = null

    companion object {
        interface Content
    }
}

