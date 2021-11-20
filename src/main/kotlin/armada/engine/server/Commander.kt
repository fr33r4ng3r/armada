package armada.engine.server

import armada.WiringHarness
import armada.engine.Engine
import armada.engine.Score
import armada.engine.api.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.reactor.flux
import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.concurrent.TimeUnit
import kotlin.time.ExperimentalTime

@ExperimentalTime
@OptIn(ExperimentalCoroutinesApi::class)
@DelicateCoroutinesApi
@RestController
@RequestMapping("api/v1")
class Commander {

    @GetMapping("/ping", produces = ["text/plain"])
    fun ping(): Mono<String> {
        return Mono.just("pong")
    }

    @PostMapping("/register")
    @ResponseBody
    fun register(@RequestBody registration: RegistrationData): Mono<TheatreData?> {
        return mono { Engine.register(registration) }
    }

    @PostMapping("/target")
    @ResponseBody
    fun target(@RequestBody target: TargetData): Mono<ActionResultData> {
        val battery = WiringHarness.battery
        val todo = with(target) { battery.turrets[turret].target(x, y) }
        return mono { ActionResultData.from(Engine.schedule(todo.tFuture, todo.action)) }
    }

    @PostMapping("/load")
    @ResponseBody
    fun loadGun(@RequestBody data: GunData): Mono<ActionResultData> {
        val battery = WiringHarness.battery
        val todo = with(data) { battery.turrets[turret].guns[gun].load() }
        return mono { ActionResultData.from(Engine.schedule(todo.tFuture, todo.action)) }
    }

    @PostMapping("/fire")
    @ResponseBody
    fun fireGun(@RequestBody data: GunData): Mono<ActionResultData> {
        val battery = WiringHarness.battery
        val todo = with(data) { battery.turrets[turret].guns[gun].fire() }
        return mono { ActionResultData.from(Engine.schedule(todo.tFuture, todo.action)) }
    }

    @GetMapping("/scan")
    @ResponseBody
    fun scan(): Flux<ScanData> {
        val satellite = WiringHarness.satellite
        return flux { satellite.scanBuffer().asFlow() }
    }

    @GetMapping("/finish")
    @ResponseBody
    fun finish(): Mono<ScoreData> {
        return mono {
            val munitions = WiringHarness.battery.munitions
            val score = WiringHarness.scoreKeeper.stop()
            Engine.finish(score)
            ScoreData(
                true,
                0,
                score.time.toNanos() / TimeUnit.SECONDS.toNanos(1).toDouble(),
                munitions - score.munitionsRemaining
            )
        }
    }

}