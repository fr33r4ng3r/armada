package armada

import armada.engine.*
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.concurrent.atomic.AtomicReference

@DelicateCoroutinesApi
object WiringHarness {

    private val batteryRef = AtomicReference(Battery(BattleGrid.DEFAULT, 0))
    private val satelliteRef = AtomicReference(Satellite(BattleGrid.DEFAULT, 10, 3))
    private val battleGridRef = AtomicReference(BattleGrid.DEFAULT)
    private val theatreRef = AtomicReference(Theatre(BattleGrid.DEFAULT, batteryRef.get(), satellite))
    private val scoreKeeperRef = AtomicReference(ScoreKeeper({}, {}))

    var battleGrid: BattleGrid
        get() = battleGridRef.get()
        set(value) = battleGridRef.set(value)

    var theatre: Theatre
        get() = theatreRef.get()
        set(value) = theatreRef.set(value)

    var battery: Battery
        get() = batteryRef.get()
        set(value) = batteryRef.set(value)

    var satellite: Satellite
        get() = satelliteRef.get()
        set(value) = satelliteRef.set(value)

    var scoreKeeper: ScoreKeeper
        get() = scoreKeeperRef.get()
        set(value) = scoreKeeperRef.set(value)

}