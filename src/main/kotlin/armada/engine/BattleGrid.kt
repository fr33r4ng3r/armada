package armada.engine

class BattleGrid(val width: Int, val height: Int) {

    val damageLayer = (0 until width).map { x -> Array(height) { y -> Tile(x, y) } }.toTypedArray()

    val actionsLayer = (0 until width).map { x -> Array(height) { y -> Tile(x, y) } }.toTypedArray()

    val effectsLayer = (0 until width).map { x -> Array(height) { y -> Tile(x, y) } }.toTypedArray()

    companion object {
        val DEFAULT = BattleGrid(20, 20)
    }
}