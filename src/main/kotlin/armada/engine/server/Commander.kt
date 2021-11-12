package armada.engine.server

import armada.WiringHarness
import armada.engine.Engine
import armada.engine.api.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.reactor.mono
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@DelicateCoroutinesApi
@RestController
class Commander {

    @GetMapping("/ping", produces = ["text/plain"])
    fun blog(): Mono<String> {
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

}