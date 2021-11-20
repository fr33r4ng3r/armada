package armada.engine.server

import armada.WiringHarness
import armada.engine.api.ScanData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onStart
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import kotlin.time.ExperimentalTime

@ExperimentalTime
@DelicateCoroutinesApi
@Controller
@MessageMapping("api.v1.messages")
class Radio {

    @MessageMapping("scanner")
    fun send(): Flow<ScanData> {
        val satellite = WiringHarness.satellite
        return satellite.sender.onStart { satellite.scanBuffer().first() }
    }

}