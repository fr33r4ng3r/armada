package armada.engine

import armada.engine.api.ScanData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.Executors
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime
import kotlin.time.milliseconds

@OptIn(ExperimentalTime::class)
@DelicateCoroutinesApi
class Satellite(val grid: BattleGrid, val scanRateTicksPerTile: Int, val scanBufferSize: Int) {

    val scansLayer = (0 until grid.width).map { x -> Array(grid.height) { y -> Tile(x, y) } }.toTypedArray()
    val sender: MutableSharedFlow<ScanData> = MutableSharedFlow()

    private val buffer = Array(scanBufferSize) { grid.damageLayer[0][0] }
    private val delay = 10L * scanRateTicksPerTile
    private var job: Job? = null

    fun startScanner() {
        job = GlobalScope.launch(Executors.newSingleThreadExecutor {
            val thread = Thread(it)
            thread.isDaemon = true
            thread
        }.asCoroutineDispatcher()) {
            var x = 0
            var y = 0
            var dx = 1
            var dy = 1
            while (isActive) {
                if (dx > 0 && x < grid.width - 1) {
                    x += 1
                } else if (dx < 0 && x > 0) {
                    x -= 1
                } else if (dy > 0 && y < grid.height - 1) {
                    y += 1
                    dx *= -1
                } else if (dy < 0 && y > 0) {
                    y -= 1
                    dx *= -1
                } else {
                    dx *= -1
                    dy *= -1
                    continue
                }
                (0 until buffer.size - 1).forEach { i ->
                    buffer[i] = buffer[i + 1]
                }
                (0 until grid.width).forEach { xS ->
                    (0 until grid.height).forEach { yS ->
                        val content = scansLayer[xS][yS].content as? Scan
                        if (content != null && content.signal > 0) {
                            scansLayer[xS][yS].content = Scan(content.signal - 1)
                        }
                    }
                }
                val tile = grid.damageLayer[x][y]
                buffer[buffer.size - 1] = tile
                sender.emit(ScanData(tile.x, tile.y, tileToHeat(tile)))
                scansLayer[x][y].content = Scan(scanBufferSize)
                delay(milliseconds(delay))
            }
        }
    }

    suspend fun stopScanner() {
        job?.cancelAndJoin()
    }

    fun scanBuffer(): List<ScanData> {
        return buffer.map { t -> ScanData(t.x, t.y, tileToHeat(t)) }
    }

    companion object {
        class Scan(val signal: Int) : Tile.Companion.Content

        fun tileToHeat(tile: Tile): Double {
            return 1.0 - ((tile.content as? Ship.Companion.HullSegment)?.health?.get() ?: 2) / 2.0
        }
    }

}