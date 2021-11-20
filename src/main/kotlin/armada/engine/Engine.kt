@file:OptIn(ExperimentalTime::class)

package armada.engine

import armada.engine.api.RegistrationData
import armada.engine.api.TheatreData
import armada.engine.server.Server
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.CONFLATED
import kotlinx.coroutines.javafx.JavaFx
import org.slf4j.LoggerFactory
import org.springframework.boot.Banner
import org.springframework.boot.WebApplicationType
import org.springframework.boot.runApplication
import org.springframework.context.ConfigurableApplicationContext
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@DelicateCoroutinesApi
object Engine {

    val LOG = LoggerFactory.getLogger(this::class.java)

    val registrationChannel = Channel<RegistrationData>()
    val startChannel = Channel<Theatre>()
    val finishChannel = Channel<Score>()
    val drawChannel = Channel<Int>()

    private val schedule = Array<LinkedBlockingDeque<Action>>(100000) { LinkedBlockingDeque() }

    private val markIdx = AtomicInteger(0);

    @Volatile
    private var markTime = System.nanoTime()

    fun startServer(): ConfigurableApplicationContext {
        val app = runApplication<Server> {
            webApplicationType = WebApplicationType.REACTIVE
            setDefaultProperties(
                mapOf(
                    "server.port" to "7000",
                    "spring.rsocket.server.transport" to "websocket",
                    "spring.rsocket.server.mapping-path" to "/rsocket"
                )
            )
            setBannerMode(Banner.Mode.OFF)
            mainApplicationClass = Server::class.java
        }
        startLoop()
        return app
    }

    private fun startLoop() {
        GlobalScope.launch(Executors.newSingleThreadExecutor {
            val thread = Thread(it)
            thread.isDaemon = true
            thread
        }.asCoroutineDispatcher()) {
            markTime = System.nanoTime()
            while (isActive) {
                delay(milliseconds(10))
                val now = System.nanoTime()
                val deltaMs = TimeUnit.MILLISECONDS.convert(now - markTime, TimeUnit.NANOSECONDS)
                markTime = now
                val mark = markIdx.get()
                val next = mark + (deltaMs / 10).toInt()
                markIdx.set(next)
                (mark..next).forEach { i ->
                    var idx = i
                    while (idx >= schedule.size) {
                        idx -= schedule.size
                    }
                    val dQ = schedule[idx]
                    if (dQ.isEmpty()) return@forEach
                    val actions = LinkedList<Action?>()
                    dQ.drainTo(actions)
                    LOG.debug("launching ${actions.size} actions from $idx")
                    actions.forEach {
                        launch(Dispatchers.Default) {
                            it?.perform()
                        }
                    }
                }
                launch(Dispatchers.JavaFx) {
                    drawChannel.send(mark)
                }
            }
        }
    }

    suspend fun start(theatre: Theatre) {
        startChannel.send(theatre)
    }

    suspend fun finish(score: Score) {
        finishChannel.send(score)
    }

    suspend fun register(data: RegistrationData): TheatreData? = coroutineScope {
        registrationChannel.send(data)
        val theatre = startChannel.receive()
        val ships =
            theatre.ships.map { TheatreData.ShipData(it.javaClass.name, it.dimensions.width, it.dimensions.length) }
        val result = TheatreData(theatre.grid.width, theatre.grid.height, theatre.ships.size, ships)
        return@coroutineScope result
    }

    fun scheduleImmediate(deltaT: Int, action: Action) {
        var idx = markIdx.get() + deltaT
        while (idx >= schedule.size) {
            idx -= schedule.size
        }
        schedule[idx].offer(action)
        LOG.debug("immediate action scheduled at $idx")
    }

    suspend fun schedule(deltaT: Int, action: Action): Action.Result {
        val ch = Channel<Action.Result>(CONFLATED)
        val mark = markIdx.get()
        var idx = mark + deltaT
        while (idx >= schedule.size) {
            idx -= schedule.size
        }
        val wrapAction = Action {
            val result = action.perform()
            ch.send(result)
            result
        }
        schedule[idx].offer(wrapAction)
        LOG.debug("response action scheduled at $idx = $mark + $deltaT")
        return ch.receive()
    }

    fun interface Action {
        suspend fun perform(): Result
        data class Result(val success: Boolean, val message: String? = null)
        companion object {
            val SUCCESS = Result(true)
        }
    }

    class Todo(val tFuture: Int, val action: Action)
}
