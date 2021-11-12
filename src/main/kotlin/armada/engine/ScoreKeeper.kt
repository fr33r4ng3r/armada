package armada.engine

import armada.WiringHarness
import armada.utils.FormatUtils.format
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.Executors

@DelicateCoroutinesApi
class ScoreKeeper(private val munitionsUpdater: Updater, private val timeUpdater: Updater) {

    private val delay = 10L
    private var job: Job? = null
    private var startTime = LocalDateTime.now()

    fun start() {
        val battery = WiringHarness.battery
        startTime = LocalDateTime.now()
        job = GlobalScope.launch(Executors.newSingleThreadExecutor {
            val thread = Thread(it)
            thread.isDaemon = true
            thread
        }.asCoroutineDispatcher()) {
            while (isActive) {
                val duration = Duration.between(startTime, LocalDateTime.now())
                launch(Dispatchers.JavaFx) {
                    timeUpdater.update(
                        "${duration.toMinutesPart().format(2)}:${
                            duration.toSecondsPart().format(2)
                        }:${(duration.toMillisPart() / 10).format(2)}"
                    )
                    munitionsUpdater.update("${battery.munitionsRemaining}")
                }
                Thread.sleep(delay, 0)
            }
        }
    }

    suspend fun stop(): Score {
        job?.cancelAndJoin()
        return Score(Duration.between(startTime, LocalDateTime.now()), WiringHarness.battery.munitionsRemaining)
    }

    companion object {
        fun interface Updater {
            suspend fun update(value: String)
        }
    }

}