package armada.engine

enum class Orientation {
    horizontal, vertical;

    fun toDegrees(): Double {
        return if (this == vertical) 0.0 else 90.0
    }

    companion object {
        fun of(rotation: Double): Orientation {
            return if (rotation == 0.0) vertical else horizontal
        }
    }
}